package de.unidue.stanbol.entities.filter.impl;

import de.unidue.stanbol.entities.filter.data.EntitiesFilterEngineConfiguration;
import org.apache.clerezza.rdf.core.*;
import org.apache.felix.scr.annotations.*;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;
import org.apache.stanbol.enhancer.servicesapi.EngineException;
import org.apache.stanbol.enhancer.servicesapi.EnhancementEngine;
import org.apache.stanbol.enhancer.servicesapi.helper.EnhancementEngineHelper;
import org.apache.stanbol.enhancer.servicesapi.impl.AbstractEnhancementEngine;
import org.apache.stanbol.entityhub.servicesapi.model.rdf.RdfResourceEnum;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.ComponentContext;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.apache.stanbol.enhancer.servicesapi.rdf.Properties.ENHANCER_CONFIDENCE;
import static org.apache.stanbol.enhancer.servicesapi.rdf.Properties.ENHANCER_ENTITY_REFERENCE;

/**
 * Dieses Engine löscht alle Entitäten, deren EntityHubRank und deren Confidence zu klein sind.
 */
@Component(
        metatype = true,
        immediate = true,
        inherit = true,
        configurationFactory = true,
        policy = ConfigurationPolicy.REQUIRE)
@Service
@org.apache.felix.scr.annotations.Properties(value = {
        @Property(name = EnhancementEngine.PROPERTY_NAME, value = "entitiesFilterEngine"),
        @Property(name = EntitiesFilterEngineConfiguration.MINIMAL_ENTITY_HUB_RANK_PROPERTY, floatValue = 0),
        @Property(name = EntitiesFilterEngineConfiguration.MINIMAL_CONFIDENCE_PROPERTY, floatValue = 0),
        @Property(name = Constants.SERVICE_RANKING, intValue = -100)
})
public class EntitiesFilterEngine extends AbstractEnhancementEngine<RuntimeException, RuntimeException> implements EnhancementEngine {

    private EntitiesFilterEngineConfiguration engineConfiguration;
    private static UriRef entityHubRanking = new UriRef(RdfResourceEnum.entityRank.getUri());

    @Override
    public int canEnhance(ContentItem contentItem) throws EngineException {
        final String lang = EnhancementEngineHelper.getLanguage(contentItem);
        if ("de".equalsIgnoreCase(lang)) {
            return ENHANCE_SYNCHRONOUS;
        }
        return CANNOT_ENHANCE;
    }

    @Activate
    @Override
    protected void activate(ComponentContext ce) throws ConfigurationException {
        super.activate(ce);

        engineConfiguration = new EntitiesFilterEngineConfiguration();

        Object value = ce.getProperties().get(EntitiesFilterEngineConfiguration.MINIMAL_ENTITY_HUB_RANK_PROPERTY);
        if (value != null && value instanceof Number) {
            engineConfiguration.setMinEntityHubRank(((Number) value).floatValue());
        } else if (value != null) {
            engineConfiguration.setMinEntityHubRank(Float.valueOf(value.toString()));
        }

        value = ce.getProperties().get(EntitiesFilterEngineConfiguration.MINIMAL_CONFIDENCE_PROPERTY);
        if (value != null && value instanceof Number) {
            engineConfiguration.setMinConfidence(((Number) value).floatValue());
        } else if (value != null) {
            engineConfiguration.setMinConfidence(Float.valueOf(value.toString()));
        }
    }

    @Override
    public void computeEnhancements(ContentItem contentItem) throws EngineException {
        filterEntitiesWithSmallEntityRank(contentItem);
        filterEntitiesWithSmallConfidence(contentItem);
    }

    private void filterEntitiesWithSmallConfidence(ContentItem contentItem) {
        final LiteralFactory literalFactory = LiteralFactory.getInstance();
        final Set<Resource> subjectsToRemove = new HashSet<>();

        contentItem.getLock().writeLock().lock();
        final MGraph graph = contentItem.getMetadata();
        try {
            final Iterator<Triple> enhancementConfidencies = graph.filter(null, ENHANCER_CONFIDENCE, null);
            enhancementConfidencies.forEachRemaining(enhancementConfidenceTriple -> {
                final Double enhancementConfidence = EnhancementEngineHelper.get(graph, enhancementConfidenceTriple.getSubject(),
                        ENHANCER_CONFIDENCE, Double.class, literalFactory);
                if (enhancementConfidence < engineConfiguration.getMinConfidence()) {
                    subjectsToRemove.add(enhancementConfidenceTriple.getSubject());
                }
            });

            graph.removeIf(triple -> subjectsToRemove.contains(triple.getSubject()) ||
                    subjectsToRemove.contains(triple.getObject()));
        } finally {
            contentItem.getLock().writeLock().unlock();
        }
    }

    private void filterEntitiesWithSmallEntityRank(ContentItem contentItem) {
        final LiteralFactory literalFactory = LiteralFactory.getInstance();
        final Set<Triple> entityReferencesToRemove = new HashSet<>();
        final Set<Resource> subjectsToRemove = new HashSet<>();

        contentItem.getLock().writeLock().lock();
        final MGraph graph = contentItem.getMetadata();
        try {
            final Iterator<Triple> entityReferences = graph.filter(null, ENHANCER_ENTITY_REFERENCE, null);
            entityReferences.forEachRemaining(entityReference -> {
                final UriRef referencedEntity = (UriRef) entityReference.getObject();
                final Float entityHubRank = EnhancementEngineHelper.get(graph, referencedEntity, entityHubRanking,
                        Float.class, literalFactory);
                if (entityHubRank < engineConfiguration.getMinEntityHubRank()) {
                    entityReferencesToRemove.add(entityReference);
                    subjectsToRemove.add(referencedEntity);
                }
            });

            graph.removeIf(triple -> entityReferencesToRemove.contains(triple) ||
                    subjectsToRemove.contains(triple.getSubject()));
        } finally {
            contentItem.getLock().writeLock().unlock();
        }
    }
}

/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.stanbol.enhancer.servicesapi.rdf;

import org.apache.clerezza.rdf.core.PlainLiteral;
import org.apache.clerezza.rdf.core.UriRef;

/**
 * Namespace of standard properties to be used as typed metadata by
 * EnhancementEngine.
 *
 * Copy and paste the URLs in a browser to access the official definitions (RDF
 * schema) of those properties to.
 *
 * @author ogrisel
 *
 */
public final class Properties {

    /**
     * Restrict instantiation
     */
    private Properties() {}

    /**
     * The canonical way to give the type of a resource. It is very common that
     * the target of this property is an owl:Class such as the ones defined is
     * {@link OntologyClass}
     */
    public static final UriRef RDF_TYPE = new UriRef(NamespaceEnum.rdf + "type");

    /**
     * A label for resources of any type.
     */
    public static final UriRef RDFS_LABEL = new UriRef(NamespaceEnum.rdfs
            + "label");

    /**
     * Used to relate a content item to another named resource such as a person,
     * a location, an organization, ...
     *
     * @deprecated use ENHANCER_ENTITY instead
     */
    @Deprecated
    public static final UriRef DC_REFERENCES = new UriRef(NamespaceEnum.dc
            + "references");

    /**
     * Creation date of a resource. Used by Stanbol Enhancer to annotate the creation date
     * of the enhancement by the enhancement engine
     */
    public static final UriRef DC_CREATED = new UriRef(NamespaceEnum.dc
            + "created");

    /**
     * Modification date of a resource. Used by Stanbol Enhancer to annotate the 
     * modification date of the enhancement if it was changed by an other
     * enhancement engine as the one creating it. Multiple changes of the
     * creating enhancement engines are not considered as modifications.
     */
    public static final UriRef DC_MODIFIED = new UriRef(NamespaceEnum.dc
            + "modified");

    /**
     * The entity responsible for the creation of a resource. Used by Stanbol Enhancer to
     * annotate the enhancement engine that created an enhancement
     */
    public static final UriRef DC_CREATOR = new UriRef(NamespaceEnum.dc
            + "creator");
    /**
     * The entity contributed to a resource. Used by Stanbol Enhancer to
     * annotate the enhancement engine that changed an enhancement originally
     * created by an other enhancemetn engine
     */
    public static final UriRef DC_CONTRIBUTOR = new UriRef(NamespaceEnum.dc
            + "contributor");

    /**
     * The nature or genre of the resource. Stanbol Enhancer uses this property to refer to
     * the type of the enhancement. Values should be URIs defined in some
     * controlled vocabulary
     */
    public static final UriRef DC_TYPE = new UriRef(NamespaceEnum.dc + "type");

    /**
     * A related resource that is required by the described resource to support
     * its function, delivery, or coherence. Stanbol Enhancer uses this property to refer to
     * other enhancements an enhancement depends on.
     */
    public static final UriRef DC_REQUIRES = new UriRef(NamespaceEnum.dc
            + "requires");

    /**
     * A related resource. Stanbol Enhancer uses this property to define enhancements that
     * are referred by the actual one
     */
    public static final UriRef DC_RELATION = new UriRef(NamespaceEnum.dc
            + "relation");

    /**
     * A point on the surface of the earth given by two signed floats (latitude
     * and longitude) concatenated as a string literal using a whitespace as
     * separator.
     */
    @Deprecated
    public static final UriRef GEORSS_POINT = new UriRef(NamespaceEnum.georss
            + "point");

    @Deprecated
    public static final UriRef GEO_LAT = new UriRef(NamespaceEnum.geo + "lat");

    @Deprecated
    public static final UriRef GEO_LONG = new UriRef(NamespaceEnum.geo + "long");

    public static final UriRef SKOS_BROADER = new UriRef(NamespaceEnum.skos + "broader");
    
    public static final UriRef SKOS_NARROWER = new UriRef(NamespaceEnum.skos + "narrower");
    
    /**
     * Refers to the content item the enhancement was extracted form
     */
    public static final UriRef ENHANCER_EXTRACTED_FROM = new UriRef(
            NamespaceEnum.fise + "extracted-from");

    /**
     * the character position of the start of a text selection.
     */
    public static final UriRef ENHANCER_START = new UriRef(NamespaceEnum.fise
            + "start");

    /**
     * the character position of the end of a text selection.
     */
    public static final UriRef ENHANCER_END = new UriRef(NamespaceEnum.fise + "end");

    /**
     * The text selected by the text annotation. This is an optional property
     */
    public static final UriRef ENHANCER_SELECTED_TEXT = new UriRef(
            NamespaceEnum.fise + "selected-text");

    /**
     * The context (surroundings) of the text selected. (e.g. the sentence
     * containing a person selected by a NLP enhancer)
     */
    public static final UriRef ENHANCER_SELECTION_CONTEXT = new UriRef(
            NamespaceEnum.fise + "selection-context");
    /**
     * The prefix of the {@link #ENHANCER_SELECTED_TEXT}. Intended to be used
     * to find the exact position within the text if char indexes can not be used
     * @since 0.11.0
     */
    public final static UriRef ENHANCER_SELECTION_PREFIX = new UriRef(
        NamespaceEnum.fise + "selection-prefix");
    /**
     * The first few chars of the {@link #ENHANCER_SELECTED_TEXT}. To be used if
     * the selected text is to long to be included as a {@link PlainLiteral} (
     * e.g. when selection sentences or whole sections of the text).
     * @since 0.11.0
     */
    public final static UriRef ENHANCER_SELECTION_HEAD = new UriRef(
        NamespaceEnum.fise + "selection-head");
    /**
     * The last few chars of the {@link #ENHANCER_SELECTED_TEXT}. To be used if
     * the selected text is to long to be included as a {@link PlainLiteral} (
     * e.g. when selection sentences or whole sections of the text).
     * @since 0.11.0
     */
    public final static UriRef ENHANCER_SELECTION_TAIL = new UriRef(
        NamespaceEnum.fise + "selection-tail");
    /**
     * The suffix of the {@link #ENHANCER_SELECTED_TEXT}. Intended to be used
     * to find the exact position within the text if char indexes can not be used
     * @since 0.11.0
     */
    public final static UriRef ENHANCER_SELECTION_SUFFIX = new UriRef(
        NamespaceEnum.fise + "selection-suffix");

    /**
     * A positive double value to rank extractions according to the algorithm
     * confidence in the accuracy of the extraction.
     */
    public static final UriRef ENHANCER_CONFIDENCE = new UriRef(NamespaceEnum.fise
            + "confidence");

    /**
     * This refers to the URI identifying the referred named entity
     */
    public static final UriRef ENHANCER_ENTITY_REFERENCE = new UriRef(
            NamespaceEnum.fise + "entity-reference");

    /**
     * This property can be used to specify the type of the entity (Optional)
     */
    public static final UriRef ENHANCER_ENTITY_TYPE = new UriRef(NamespaceEnum.fise
            + "entity-type");

    /**
     * The label(s) of the referred entity
     */
    public static final UriRef ENHANCER_ENTITY_LABEL = new UriRef(
            NamespaceEnum.fise + "entity-label");
    /**
     * The confidence level (introducdes by
     * <a herf="https://issues.apache.org/jira/browse/STANBOL-631">STANBOL-631</a>)
     */
    public static final UriRef ENHANCER_CONFIDENCE_LEVEL = new UriRef(
            NamespaceEnum.fise + "confidence-level");

    /**
     * The origin can be used to reference the vocabulary (dataset, thesaurus, 
     * ontology, ...) the Entity {@link #ENHANCER_ENTITY_REFERENCE referenced}
     * by a <code>{@link TechnicalClasses#ENHANCER_ENTITYANNOTATION fise:EntiyAnnotation}</code>
     * originates from.
     * @since 0.12.1 (STANBOL-1391)
     */
    public static final UriRef ENHANCER_ORIGIN = new UriRef(
            NamespaceEnum.fise + "origin");
    
    /**
     * Internet Media Type of a content item.
     * 
     * @deprecated dc:FileFormat does not exist
     */
    @Deprecated
    public static final UriRef DC_FILEFORMAT = new UriRef(NamespaceEnum.dc
            + "FileFormat");

    /**
     * Language of the content item text.
     */
    public static final UriRef DC_LANGUAGE = new UriRef(NamespaceEnum.dc
            + "language");


    /**
     * The topic of the resource. Used to relate a content item to a
     * skos:Concept modelling one of the overall topic of the content.
     *
     * @deprecated rwesten: To my knowledge no longer used by Stanbol Enhancer enhancement
     *             specification
     */
    @Deprecated
    public static final UriRef DC_SUBJECT = new UriRef(NamespaceEnum.dc
            + "subject");

    /**
     * The sha1 hexadecimal digest of a content item.
     */
    @Deprecated
    public static final UriRef FOAF_SHA1 = new UriRef(NamespaceEnum.foaf
            + "sha1");

    /**
     * Link an semantic extraction or a manual annotation to a content item.
     */
    @Deprecated
    public static final UriRef ENHANCER_RELATED_CONTENT_ITEM = new UriRef(
            "http://iksproject.eu/ns/extraction/source-content-item");

    @Deprecated
    public static final UriRef ENHANCER_RELATED_TOPIC = new UriRef(
            "http://iksproject.eu/ns/extraction/related-topic");

    @Deprecated
    public static final UriRef ENHANCER_RELATED_TOPIC_LABEL = new UriRef(
            "http://iksproject.eu/ns/extraction/related-topic-label");

    @Deprecated
    public static final UriRef ENHANCER_MENTIONED_ENTITY_POSITION_START = new UriRef(
            "http://iksproject.eu/ns/extraction/mention/position-start");

    @Deprecated
    public static final UriRef ENHANCER_MENTIONED_ENTITY_POSITION_END = new UriRef(
            "http://iksproject.eu/ns/extraction/mention/position-end");

    @Deprecated
    public static final UriRef ENHANCER_MENTIONED_ENTITY_CONTEXT = new UriRef(
            "http://iksproject.eu/ns/extraction/mention/context");

    @Deprecated
    public static final UriRef ENHANCER_MENTIONED_ENTITY_OCCURENCE = new UriRef(
            "http://iksproject.eu/ns/extraction/mention/occurence");

}

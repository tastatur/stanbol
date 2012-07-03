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
package org.apache.stanbol.ontologymanager.ontonet.impl.clerezza;

import static org.apache.clerezza.rdf.core.serializedform.SupportedFormat.RDF_XML;
import static org.apache.stanbol.ontologymanager.ontonet.MockOsgiContext.parser;
import static org.apache.stanbol.ontologymanager.ontonet.MockOsgiContext.reset;
import static org.apache.stanbol.ontologymanager.ontonet.MockOsgiContext.tcManager;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.apache.clerezza.rdf.core.access.TcProvider;
import org.apache.stanbol.ontologymanager.ontonet.api.OfflineConfiguration;
import org.apache.stanbol.ontologymanager.ontonet.api.ontology.OntologyProvider;
import org.apache.stanbol.ontologymanager.ontonet.impl.OfflineConfigurationImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the correct behaviour of the Clerezza-based implementation of {@link OntologyProvider}, regardless of
 * virtual ontology network setups.
 * 
 * @author alexdma
 * 
 */
public class TestClerezzaProvider {

    private static Logger log = LoggerFactory.getLogger(TestClerezzaProvider.class);

    @BeforeClass
    public static void setup() throws Exception {
        reset();
    }

    private String fn1 = "/ontologies/versiontest_v1.owl", fn2 = "/ontologies/versiontest_v2.owl";

    private String oiri = "http://stanbol.apache.org/ontologies/versiontest";

    private OntologyProvider<TcProvider> ontologyProvider;

    private OfflineConfiguration offline = new OfflineConfigurationImpl(new Hashtable<String,Object>());

    @After
    public void cleanup() {
        reset();
    }

    @Before
    public void setupTest() throws Exception {
        // Must be reset due to the internal key mapper.
        ontologyProvider = new ClerezzaOntologyProvider(tcManager, offline, parser);
    }

    @Test
    public void testVersionIRISplit() throws Exception {

        // Check the first version
        InputStream data = getClass().getResourceAsStream(fn1);
        String key1 = ontologyProvider.loadInStore(data, RDF_XML, true);
        assertNotNull(key1);
        assertFalse(key1.isEmpty());

        // Check the second version
        data = getClass().getResourceAsStream(fn2);
        String key2 = ontologyProvider.loadInStore(data, RDF_XML, true);
        assertNotNull(key2);
        assertFalse(key2.isEmpty());

        // Must be 2 different graphs
        assertFalse(key1.equals(key2));
        assertEquals(2, ontologyProvider.getKeys().size());

        // Ontologies must not be tainting each other
        OWLOntology o1 = ontologyProvider.getStoredOntology(key1, OWLOntology.class, true);
        OWLOntology o2 = ontologyProvider.getStoredOntology(key2, OWLOntology.class, true);
        Set<OWLOntology> oAll = new HashSet<OWLOntology>(Arrays.asList(new OWLOntology[] {o1, o2}));
        for (OWLNamedIndividual i : o1.getIndividualsInSignature()) {
            Set<OWLClassExpression> tAll = i.getTypes(oAll), t1 = i.getTypes(o1), t2 = i.getTypes(o2);
            assertTrue(tAll.containsAll(t1)); // Should be obvious from the OWL API
            assertTrue(tAll.containsAll(t2)); // Should be obvious from the OWL API
            assertFalse(t1.containsAll(t2));
            assertFalse(t2.containsAll(t1));
        }

        log.info("{}", ontologyProvider.getKey(IRI.create(oiri)));
    }
}
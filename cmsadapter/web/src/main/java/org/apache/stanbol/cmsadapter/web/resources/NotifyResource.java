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
package org.apache.stanbol.cmsadapter.web.resources;

import static org.apache.stanbol.commons.web.base.CorsHelper.addCORSOrigin;
import static org.apache.stanbol.commons.web.base.CorsHelper.enableCORS;

import java.util.Hashtable;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.clerezza.rdf.core.access.TcManager;
import org.apache.stanbol.cmsadapter.core.helper.TcManagerClient;
import org.apache.stanbol.cmsadapter.core.mapping.MappingConfigurationImpl;
import org.apache.stanbol.cmsadapter.servicesapi.helper.OntologyResourceHelper;
import org.apache.stanbol.cmsadapter.servicesapi.mapping.MappingConfiguration;
import org.apache.stanbol.cmsadapter.servicesapi.mapping.MappingEngine;
import org.apache.stanbol.cmsadapter.servicesapi.model.web.CMSObject;
import org.apache.stanbol.cmsadapter.servicesapi.model.web.CMSObjects;
import org.apache.stanbol.cmsadapter.servicesapi.model.web.decorated.AdapterMode;
import org.apache.stanbol.commons.web.base.ContextHelper;
import org.apache.stanbol.commons.web.base.resource.BaseStanbolResource;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

@Path("/cmsadapter/{ontologyURI:.+}/notify")
public class NotifyResource extends BaseStanbolResource {
    private static final Logger logger = LoggerFactory.getLogger(NotifyResource.class);

    private MappingEngine engine;

    private TcManager tcManager;

    public NotifyResource(@Context ServletContext context) {
        try {
            BundleContext bundleContext = (BundleContext) context.getAttribute(BundleContext.class.getName());
            ServiceReference serviceReference = bundleContext.getServiceReferences((String)null,
                "(component.factory=org.apache.stanbol.cmsadapter.servicesapi.mapping.MappingEngineFactory)")[0];
            ComponentFactory componentFactory = (ComponentFactory) bundleContext.getService(serviceReference);
            ComponentInstance componentInstance = componentFactory
                    .newInstance(new Hashtable<Object,Object>());
            this.engine = (MappingEngine) componentInstance.getInstance();
            this.tcManager = (TcManager) ContextHelper.getServiceFromContext(TcManager.class, context);

        } catch (InvalidSyntaxException e) {
            logger.warn("Mapping engine instance could not be instantiated", e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @OPTIONS
    public Response handleCorsPreflight(@Context HttpHeaders headers) {
        ResponseBuilder res = Response.ok();
        enableCORS(servletContext, res, headers);
        return res.build();
    }

    @SuppressWarnings("unchecked")
    @POST
    public Response notifyCreate(@PathParam("ontologyURI") String ontologyURI,
                                 @FormParam("createdObjects") CMSObjects cmsObjects,
                                 @QueryParam("adapterMode") AdapterMode adapterMode,
                                 @DefaultValue("true") @QueryParam("considerBridges") boolean considerBridges,
                                 @Context HttpHeaders headers) {

        List<CMSObject> createdObjectList = cmsObjects.getClassificationObjectOrContentObject();
        TcManagerClient tcManagerClient = new TcManagerClient(tcManager);
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM,
            tcManagerClient.getModel(ontologyURI));
        MappingConfiguration conf = new MappingConfigurationImpl();
        conf.setOntModel(model);
        conf.setOntologyURI(ontologyURI);
        conf.setObjects((List<Object>) (List<?>) createdObjectList);
        conf.setAdapterMode(adapterMode);
        if (considerBridges) {
            conf.setBridgeDefinitions(OntologyResourceHelper.getBridgeDefinitions(model));
        }
        engine.createModel(conf);

        ResponseBuilder rb = Response.ok();
        addCORSOrigin(servletContext, rb, headers);
        return rb.build();
    }

    /**
     * Specified {@link CMSObject}s to this resource will be updated by executing previously defined bridges.
     * Bridge definitions are obtained from the ontology model that specified with <i>ontologyURI</i>
     * 
     * @param ontologyURI
     * @param cmsObjects
     * @return
     */
    @SuppressWarnings("unchecked")
    @PUT
    public Response notifyUpdate(@PathParam("ontologyURI") String ontologyURI,
                                 @FormParam("updatedObjects") CMSObjects cmsObjects,
                                 @QueryParam("adapterMode") AdapterMode adapterMode,
                                 @QueryParam("considerBridges") @DefaultValue("true") Boolean considerBridges,
                                 @Context HttpHeaders headers) {
        List<CMSObject> updatedObjectList = cmsObjects.getClassificationObjectOrContentObject();
        TcManagerClient tcManagerClient = new TcManagerClient(tcManager);
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM,
            tcManagerClient.getModel(ontologyURI));
        MappingConfiguration conf = new MappingConfigurationImpl();
        conf.setOntModel(model);
        conf.setOntologyURI(ontologyURI);
        conf.setObjects((List<Object>) (List<?>) updatedObjectList);
        conf.setAdapterMode(adapterMode);
        if (considerBridges) {
            conf.setBridgeDefinitions(OntologyResourceHelper.getBridgeDefinitions(model));
        }
        engine.updateModel(conf);

        ResponseBuilder rb = Response.ok();
        addCORSOrigin(servletContext, rb, headers);
        return rb.build();
    }

    /**
     * Specified {@link CMSObject}s to this resource will be deleted from the generated ontology.
     * 
     * @param ontologyURI
     * @param cmsObjects
     * @return
     */
    @SuppressWarnings("unchecked")
    @DELETE
    public Response notifyDelete(@PathParam("ontologyURI") String ontologyURI,
                                 @FormParam("deletedObjects") CMSObjects cmsObjects,
                                 @QueryParam("considerBridges") @DefaultValue("true") Boolean considerBridges,
                                 @Context HttpHeaders headers) {
        List<CMSObject> deletedObjectList = cmsObjects.getClassificationObjectOrContentObject();
        TcManagerClient tcManagerClient = new TcManagerClient(tcManager);
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM,
            tcManagerClient.getModel(ontologyURI));
        MappingConfiguration conf = new MappingConfigurationImpl();
        conf.setOntModel(model);
        conf.setOntologyURI(ontologyURI);
        conf.setObjects((List<Object>) (List<?>) deletedObjectList);
        conf.setAdapterMode(AdapterMode.STRICT_OFFLINE);

        if (considerBridges) {
            conf.setBridgeDefinitions(OntologyResourceHelper.getBridgeDefinitions(model));
        }
        engine.deleteModel(conf);

        ResponseBuilder rb = Response.ok();
        addCORSOrigin(servletContext, rb, headers);
        return rb.build();
    }
}

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
package org.apache.stanbol.enhancer.engines.entityhublinking;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.clerezza.rdf.core.Resource;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.core.impl.PlainLiteralImpl;
import org.apache.stanbol.enhancer.engines.entitylinking.EntitySearcher;
import org.apache.stanbol.entityhub.servicesapi.Entityhub;
import org.apache.stanbol.entityhub.servicesapi.EntityhubException;
import org.apache.stanbol.entityhub.servicesapi.model.Entity;
import org.apache.stanbol.entityhub.servicesapi.model.Representation;
import org.apache.stanbol.entityhub.servicesapi.model.rdf.RdfResourceEnum;
import org.apache.stanbol.entityhub.servicesapi.query.FieldQuery;
import org.apache.stanbol.entityhub.servicesapi.query.QueryResultList;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public final class EntityhubSearcher extends TrackingEntitySearcher<Entityhub> implements EntitySearcher {
    
    private final Integer limit;
    private Map<UriRef,Collection<Resource>> originInfo;

    public EntityhubSearcher(BundleContext context, Integer limit) {
        this(context,limit,null);
    }
    public EntityhubSearcher(BundleContext context, Integer limit,ServiceTrackerCustomizer customizer) {
        super(context,Entityhub.class,null,customizer);
        this.limit = limit != null && limit > 0 ? limit : null;
        this.originInfo = Collections.singletonMap(
            new UriRef(RdfResourceEnum.site.getUri()), 
            (Collection<Resource>)Collections.singleton(
                (Resource)new PlainLiteralImpl("entityhub")));
    }
    
    @Override
    public Representation get(String id,Set<String> includeFields) {
        if(id == null || id.isEmpty()){
            return null;
        }
        Entityhub entityhub = getSearchService();
        if(entityhub == null){
            throw new IllegalStateException("The Entityhub is currently not active");
        }
        Entity entity;
        try {
            entity = entityhub.getEntity(id);
        }  catch (EntityhubException e) {
            throw new IllegalStateException("Exception while getting "+id+
                " from the Entityhub",e);
        }
        return entity == null ? null : entity.getRepresentation();
    }
    @Override
    public Collection<? extends Representation> lookup(String field,
                                           Set<String> includeFields,
                                           List<String> search,
                                           String[] languages,
                                           Integer limit) throws IllegalStateException {
        Entityhub entityhub = getSearchService();
        if(entityhub == null){
            throw new IllegalStateException("The Entityhub is currently not active");
        }
        FieldQuery query = EntitySearcherUtils.createFieldQuery(entityhub.getQueryFactory(),
            field, includeFields, search, languages);
        if(limit != null && limit > 0){
            query.setLimit(limit);
        } else if(this.limit != null){
            query.setLimit(this.limit);
        }
        QueryResultList<Representation> results;
        try {
            results = entityhub.find(query);
        } catch (EntityhubException e) {
            throw new IllegalStateException("Exception while searchign for "+
                search+'@'+Arrays.toString(languages)+"in the Entityhub", e);
        }
        return results.results();
    }

    @Override
    public boolean supportsOfflineMode() {
        return true; //the entityhub is always offline
    }

    @Override
    public Integer getLimit() {
        return limit;
    }

    @Override
    public Map<UriRef,Collection<Resource>> getOriginInformation() {
        return originInfo;
    }
}
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
package org.apache.stanbol.entityhub.indexing.core;

import org.apache.stanbol.entityhub.servicesapi.model.Representation;

/**
 * Interface used to get the representation (data) for an entity based on the
 * id. This Interface is used for indexing in cases, where the list of entities
 * to index is known in advance and the data source provides the possibility to
 * retrieve the entity data based on the ID (e.g. a RDF triple store).
 * @see {@link EntityDataIterator}
 * @author Rupert Westenthaler
 *
 */
public interface EntityDataProvider extends IndexingComponent {
    
    Representation getEntityData(String id);

}

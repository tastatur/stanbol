<#--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<@namespace ont="http://example.org/service-description#" />
<@namespace ehub="http://stanbol.apache.org/ontology/entityhub/entityhub#" />
<@namespace cc="http://creativecommons.org/ns#" />
<@namespace dct="http://purl.org/dc/terms/" />

<html>
  <head>
    <title>Example Application - Apache Stanbol</title>
    <link type="text/css" rel="stylesheet" href="styles/resource-resolver.css" />
  </head>

  <body>
    <h1>Resolve resource</h1>
    
    <form action="<@ldpath path="."/>">
        IRI of entity to look up (e.g. http://dbpedia.org/resource/Paris) <br/>
        <label for="iri" /><input type="text" name="iri" 
                value="<@ldpath path="ont:describes"/>" size="90"/><br/>
        WARNING: all requests are logged, information about your request will 
        be shown to any user requesting the same resource<br/>
        <input type="submit" value="look up" />
    </form>

    <@ldpath path="ont:describes">
        <p>
        Note: you can also get an rdf representation of this description 
        by setting a respective Accept header, e.g.<br/>
        <code>curl -H "Accept: text/turtle" <@ldpath path="^ont:describes"/>?iri=<@ldpath path="."/></code>
        <#if evalLDPath("rdfs:label")??>
            <h2>Resource Description</h2>
            <h3>Labels:</h3>
            <ul>
            <@ldpath path="rdfs:label"><li><@ldpath path="."/></li></@ldpath>
            </ul>
            <h3>Comment</h3>
            <div><@ldpath path="rdfs:comment"/></div>
            <@ldpath path="^ehub:about">
                <h2>Resource Metadata</h2>
                <div>
                Is cached locally: <@ldpath path="ehub:isChached"/> 
                </div>
                <div>
                License: <@ldpath path="dct:license"/> 
                </div>
                <div>
                Attribution URL: <a href="<@ldpath path="cc:attributionURL"/>" >
                <@ldpath path="cc:attributionURL"/></a> 
                </div>
            </@ldpath>
        </#if>
        <h2>Logged Requests for this Entity</h2>
        <ol>
            <@ldpath path="^ont:requestedEntity">
                <li class="LoggedRequest">
                    Date: <@ldpath path="dc:date"/><br/>
                    Agent: <@ldpath path="ont:userAgent"/><br/>
                </li>
            </@ldpath>
        </ol>
        </p>
    </@ldpath>
    <#include "/html/includes/footer.ftl">
  </body>
</html>


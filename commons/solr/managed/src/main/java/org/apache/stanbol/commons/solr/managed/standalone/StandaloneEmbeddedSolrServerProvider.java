package org.apache.stanbol.commons.solr.managed.standalone;

import java.util.ServiceLoader;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
import org.apache.stanbol.commons.solr.IndexReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides {@link EmbeddedSolrServer} instances based on the
 * {@link StandaloneManagedSolrServer} implementation. Only intended to be used
 * outside of an OSGI environment. If running within an OSGI Environment
 * this functionality is provided by an {@link SolrServerProvider} implementation
 * that uses {@link CoreContainer}s and {@link SolrCore}s registered as
 * OSGI services.
 * @see StandaloneManagedSolrServer
 * @author Rupert Westenthaler
 *
 */
public class StandaloneEmbeddedSolrServerProvider {
    
    private static StandaloneEmbeddedSolrServerProvider instance;
    
    public static StandaloneEmbeddedSolrServerProvider getInstance(){
        if(instance == null){
            instance = new StandaloneEmbeddedSolrServerProvider();
        }
        return instance;
    }
    
    private final Logger log = LoggerFactory.getLogger(StandaloneEmbeddedSolrServerProvider.class);
    /**
     * Private constructor used to create the singleton.
     */
    private StandaloneEmbeddedSolrServerProvider() {}
    
    public SolrServer getSolrServer(IndexReference indexRef){
        if(indexRef == null){
            throw new IllegalArgumentException("The parsed InexReference MUST NOT be NULL!");
        }
        StandaloneManagedSolrServer server;
        log.debug("Create EmbeddedSolrServer for Server: {}, Index: {}",
            indexRef.getServer(),indexRef.getIndex());
        if(indexRef.getServer() == null){
            server = StandaloneManagedSolrServer.getManagedServer();
        } else {
            server = StandaloneManagedSolrServer.getManagedServer(indexRef.getServer());
        }
        if(server == null){
            log.debug("  > Managed Solr server with name {} not found -> return null",
                indexRef.getServer());
            return null;
        }
        log.debug("  > use managed Solr server with name {}",server.getServerName());

        String coreName;
        if(indexRef.getIndex() == null){
            coreName = server.getDefaultCore();
        } else if(indexRef.isPath()){
            coreName = server.getCoreForDirectory(indexRef.getIndex());
        } else {
            coreName = indexRef.getIndex();
        } 
        if(coreName != null){
            return new EmbeddedSolrServer(server.getCoreContainer(), coreName);
        } else {
            return null;
        }
    }
}

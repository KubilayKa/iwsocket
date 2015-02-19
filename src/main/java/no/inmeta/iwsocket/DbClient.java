package no.inmeta.iwsocket;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

/**
 * Created by kubkaray on 19.02.2015.
 */
public class DbClient {

   private CouchDbProperties properties = new CouchDbProperties().setDbName("iw-socket").setCreateDbIfNotExist(true).setProtocol("http")
            .setHost("127.0.0.1").setPort(5984);
    private CouchDbClient couchDbClient = new CouchDbClient(properties);

    public CouchDbClient getCouchDbClient(){
        return couchDbClient;
    }
}

package no.inmeta.iwsocket;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by K on 1/23/15.
 */
public class ISocketConnectionManager {
    private ISocketConnectionManager(){}
    private final Map<String,IClientFbo> list=new HashMap<>();

    public void addSession(String sId,IClientFbo iClientFbo){
        list.put(sId,iClientFbo);
    }

    private String generateRoomName( String gameType) {

        return null;
    }

    public IClientFbo getClientFboBySid(String sId) {
        return list.get(sId);
    }
    public void handleMessage(Session session,String msg) {
        //TODO: sjekk om mc eller bc rediger til om den har ikke rom dediser et rom
        //TODO: legg inn logikk f


    }


    private final static ISocketConnectionManager iSocketConnectionManager = new ISocketConnectionManager();
    public static ISocketConnectionManager getIstance(){
        return iSocketConnectionManager;

    }
}

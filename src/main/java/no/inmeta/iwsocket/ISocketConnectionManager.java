package no.inmeta.iwsocket;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by K on 1/23/15.
 */
public class ISocketConnectionManager {



    private Session mainClient;

    private ISocketConnectionManager(){}
    private final Map<String,IClientFbo> list=new HashMap<>();
    private final Map<String,String[]> roomList =new HashMap<>();

    public void addSession(String sId,String roomid){
        //list.put(sId,iClientFbo);
        roomList.put(roomid,new String[]{sId,null,null});
    }

    public void setMainClient(Session mainClient) {
        this.mainClient = mainClient;
    }

    public void removeRoom(){
        removeSessions();

    }
    public void addRoom(String roomName,String [] sessionIds){
        roomList.put(roomName,sessionIds);
    }
    public void updateRoom(String roomName,String sessionId,String type){
        if (type.equals("addMc")){

            roomList.get(roomName)[1]=sessionId;
        }else if (type.equals("addBc")){
            roomList.get(roomName)[2]=sessionId;
        }else if (type.equals("remove")){
            String[] clients = roomList.get(roomName);
            for (int i=0;i<clients.length;i++){
                if (clients[i].equals(sessionId))
                    clients[i]=null;

            }

        }

    }
    private void removeSessions(){}
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

    public Set<String> getRoomNames() {

        return roomList.keySet();
    }

    public Session getMainClient() {

        return mainClient;
    }
}

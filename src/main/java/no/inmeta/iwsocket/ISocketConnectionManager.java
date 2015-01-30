package no.inmeta.iwsocket;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by K on 1/23/15.
 */
public class ISocketConnectionManager {



    private final  Logger logger = Logger.getLogger("AISCLI");
    private ISocketConnectionManager(){}
    private final Map<String,IClientFbo> list=new HashMap<>();
    public void initRoom(String sIdf,  String roomid ){
        //list.put(sId,iClientFbo);

        roomList.put(roomid,new String[]{sIdf,null,null});
    }
    public static Map<String, String[]> getRoomList() {
        return roomList;
    }
    public  boolean isRoomExists(String playerName){
     for (String s:roomList.keySet()){
         if (s.contains(playerName))
             return true;
     }
       return false;
   }


    public  void updateRoom(String roomName,String sessionId,String type){
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
    public  Logger getLogger() {
    return logger;
}
    public void handleMessage(Session session,String msg) {
        //TODO: sjekk om mc eller bc rediger til om den har ikke rom dediser et rom
        //TODO: legg inn logikk f
    }
    private final static ISocketConnectionManager iSocketConnectionManager = new ISocketConnectionManager();
    private  final static Map<String,String[]> roomList =new HashMap<>();
    public static ISocketConnectionManager getIstance(){
        return iSocketConnectionManager;

    }
    public   Set<String> getRoomNames() {

        return roomList.keySet();
    }
    public   String[] getRoomById(String s) {
        return roomList.get(s);
    }
    public   boolean isRgstrdToRoom(String id) {
        for (String roomName:roomList.keySet()){
             String[] sessionArr=roomList.get(roomName);
             for(String sessionId:sessionArr){
                 if (sessionId.equals(id))
                     return true;
             }
        }
        return false;
    }


    public   void clearRoomList() {
         roomList.clear();
    }

    public static void deleteRoom(String rId) {
        roomList.remove(rId);
    }
}

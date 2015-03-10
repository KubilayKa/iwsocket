package no.inmeta.iwsocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by K on 1/23/15.
 */
public class ISocketConnectionManager {


    private final Logger logger = Logger.getLogger("AISCLI");
    private final static ISocketConnectionManager iSocketConnectionManager = new ISocketConnectionManager();

    private ISocketConnectionManager() {
    }

    public static ISocketConnectionManager getIstance() {
        return iSocketConnectionManager;

    }

    private final Map<String, IClientFbo> list = new HashMap<>();

    public void initRoom(String sIdf, String roomid) {
        //list.put(sId,iClientFbo);

        roomList.put(roomid, new String[]{sIdf, null, null});
    }

    public static Map<String, String[]> getRoomList() {
        Map<String,String[]> listCopy=roomList;
        return listCopy;
    }


    public String updateRoom(String roomName, String sessionId, String type) {
        if (type.equals("addMc")) {
            if (null == roomList.get(roomName)[1] || roomList.get(roomName)[1].equals("")) {
                roomList.get(roomName)[1] = sessionId;
                return "l:";
            } else {
                roomList.get(roomName)[2] = sessionId;
                return "r:";
            }
        } else if (type.equals("addBc")) {
            roomList.get(roomName)[0] = sessionId;
            return "bc added";
        } else if (type.equals("remove")) {
            String[] clients = roomList.get(roomName);
            for (int i = 0; i < clients.length; i++) {
                if (clients[i].equals(sessionId))
                    roomList.get(roomName)[i]=null;
            }
            return "list cleared";
        }
        return "unknown operation";
    }

    public Logger getLogger() {
        return logger;
    }

    private  static Map<String, String[]> roomList = new HashMap<>();




    public String[] getRoomById(String s) {
        return roomList.get(s);
    }

    public boolean isRgstrdToRoom(String id) {
        for (String roomName : roomList.keySet()) {
            String[] sessionArr = roomList.get(roomName);
            for (String sessionId : sessionArr) {
                if (sessionId.equals(id))
                    return true;
            }
        }
        return false;
    }


    public byte[] getPicBytes(String s) throws IOException {
        URL picUrl=getClass().getClassLoader().getResource("employeeimages" + File.separator + s.toLowerCase() + ".jpg");
        byte[] tmp;
        if (null != picUrl ) {
            File fnew = new File(picUrl.getFile());
            tmp = Files.readAllBytes(fnew.toPath());
        }else {
            File unkn = new File(getClass().getClassLoader().getResource("employeeimages/unknown_player.png").getFile());
            tmp = Files.readAllBytes(unkn.toPath());
        }
        return tmp;
    }
}

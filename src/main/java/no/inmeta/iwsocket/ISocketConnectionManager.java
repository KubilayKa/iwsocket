package no.inmeta.iwsocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
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
        logger.log(Level.INFO,"room init*******");
    }

    public static Map<String, String[]> getRoomList() {
        Map<String,String[]> listCopy=roomList;
        return listCopy;
    }


    public String updateRoom(String roomName, String sessionId, String type) {
        if (type.equals("addMc")) {
            if (null == roomList.get(roomName)[1] || roomList.get(roomName)[1].equals("")) {
                roomList.get(roomName)[1] = sessionId;
                logger.log(Level.INFO,"added to room place 1");
                return "l:";
            } else {
                roomList.get(roomName)[2] = sessionId;
                logger.log(Level.INFO,"added to room place 2");
                return "r:";
            }
        } else if (type.equals("addBc")) {
            roomList.get(roomName)[0] = sessionId;
            logger.log(Level.INFO,"added bc #########");
            return "bc added";
        } else if (type.equals("remove")) {
            String[] clients = roomList.get(roomName);
            roomList.remove("main");
            logger.log(Level.INFO,"removed room");
            initRoom(clients[0],"main");
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
        logger.log(Level.INFO,s+" pic search...");
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

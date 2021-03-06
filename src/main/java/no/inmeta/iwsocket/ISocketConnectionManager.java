package no.inmeta.iwsocket;

import javax.websocket.Session;
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
    private Session mainSession;

    public Session getMainSession() {
        return mainSession;
    }

    public void setMainSession(Session mainSession) {
        this.mainSession = mainSession;
    }

    private ISocketConnectionManager() {
    }

    public static ISocketConnectionManager getIstance() {
        return iSocketConnectionManager;

    }

    private final Map<String, IClientFbo> list = new HashMap<>();

    public void initRoom(String sIdf, String roomid,Session session) {
        mainSession=session;
        roomList.put(roomid, new String[]{sIdf, null, null});
        logger.log(Level.WARNING,"room init*******");
    }

    public static Map<String, String[]> getRoomList() {
        Map<String,String[]> listCopy=roomList;
        return listCopy;
    }

   public String getPlayer(int i) {
       return roomList.get("main")[i];
   }
    public String updateRoom(String roomName, String sessionId, String type,String place) {
        if (type.equals("addMc")) {
            if (null != place && "first".equals(place)) {
                roomList.get(roomName)[1] = sessionId;
                logger.log(Level.WARNING,"added to room place 1");
                return "l:";
            } else {
                roomList.get(roomName)[2] = sessionId;
                logger.log(Level.WARNING,"added to room place 2");
                return "r:";
            }
        } else if (type.equals("addBc")) {
            roomList.get(roomName)[0] = sessionId;
            logger.log(Level.WARNING,"added bc #########");
            return "bc added";
        } else if (type.equals("remove")) {
            roomList.remove("main");
            logger.log(Level.WARNING,"removed room");
            initRoom(mainSession.getId(),"main",mainSession);
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
        logger.log(Level.WARNING,s+" pic search...");
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


    public boolean isRoomReady() {
        String[] clients=roomList.get("main");

            if (null!=clients[1])
                return true;

        return false;
    }
    public boolean isTherePlace() {
        String[] clients=roomList.get("main");
        if (null == clients[1] || null == clients[2])
            return true;
        return false;
    }
}

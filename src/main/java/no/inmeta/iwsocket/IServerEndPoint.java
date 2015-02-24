package no.inmeta.iwsocket;


import com.google.gson.Gson;
import org.lightcouch.CouchDbClient;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by K on 1/16/15.
 */
@ServerEndpoint(value = "/inbound/{userAgent}/{first}/{second}")
public class IServerEndPoint {
    private ISocketConnectionManager iSocketConnectionManager = ISocketConnectionManager.getIstance();
    private Logger logger = iSocketConnectionManager.getLogger();
    private CouchDbClient dbClient = new DbClient().getCouchDbClient();
    @OnOpen
    public void onOpen(Session session) throws IOException {
        //  session.getUserProperties().put("chatroom","chatroom");
        Map<String, String> pathPrm = session.getPathParameters();
        String firstPlayer = pathPrm.get("first");
        String secondPlayer = pathPrm.get("second");
        String roomId = firstPlayer + "&" + secondPlayer;
        String sessionId = session.getId();
        String roomParticipant[] = iSocketConnectionManager.getRoomById("main");
        // Response response=dbClient.save(new JsonParser().parse("{\"udidit\":\"true\"}").getAsJsonObject());
        if (pathPrm.get("userAgent").contains("browserClient")) {
            if (null == roomParticipant || null == roomParticipant[0]) {
                session.getUserProperties().put("roomName", "main");
                session.getUserProperties().put("roomManager", "main");
                iSocketConnectionManager.initRoom(sessionId, firstPlayer);
                byte[] fpPic =iSocketConnectionManager.getPicBytes(firstPlayer);
                Gson gson = new Gson();

                try {
                    session.getBasicRemote().sendObject(gson.toJson(new PicFbo().setUserName("kubican").setPic(fpPic)));
                } catch (EncodeException e) {
                    e.printStackTrace();
                }
             //   session.getBasicRemote().sendText("db:"+response.toString());
            } else {
                session.close();
                return;
            }

        } else if (pathPrm.get("userAgent").contains("androidClient") &&
                (null != roomParticipant || null == roomParticipant[1] || null == roomParticipant[2])) {
            String post = iSocketConnectionManager.updateRoom("main", sessionId, "addMc");
            session.getUserProperties().put("pp", post);
            session.getUserProperties().put("roomName", "main");
            Set<Session> sessions = session.getOpenSessions();
            Session[] sesArr = sessions.toArray(new Session[3]);
            if (sessions.size() ==2) {
                sesArr[0].getBasicRemote().sendText("userName:"+ firstPlayer+":"+secondPlayer);
                byte[] fpPic =iSocketConnectionManager.getPicBytes(firstPlayer);
                byte[] spPic =iSocketConnectionManager.getPicBytes(secondPlayer);
                try {
                   sesArr[0].getBasicRemote().sendObject(fpPic);
                } catch (EncodeException e) {
                    e.printStackTrace();
                }
            }
        } else {
            session.getBasicRemote().sendText("popup:En annen spill er i gang, vent på din tur dude!");
            session.close();
            return;
        }
        session.getUserProperties().put("roomName", "main");
       /* for global play uncomment this
       if (firstPlayer.equals("main")) {
            session.getUserProperties().put("roomName", firstPlayer);
            return;
        }
        if (pathPrm.get("userAgent").contains("androidClient")) {
            if (iSocketConnectionManager.isRoomExists(firstPlayer)) {
                roomId = secondPlayer + "&" + firstPlayer;
                iSocketConnectionManager.updateRoom(roomId, sessionId, "addMc");
                session.getUserProperties().put("pp","r:");
            } else {
                iSocketConnectionManager.initRoom(sessionId, roomId);
                session.getUserProperties().put("pp", "l:");
            }
            session.getBasicRemote().sendText("popup:" + roomId);
        } else {
            String[] roomMembers = iSocketConnectionManager.getRoomById(roomId);
            if (null != roomMembers[0] || session.getId() != roomMembers[2]) {

                iSocketConnectionManager.updateRoom(roomId, sessionId, "addBc");
            } else {
                return;
            }
            Set<Session> openSessions = session.getOpenSessions();
            for (String ss : roomMembers) {
                for (Session s : openSessions) {
                    if (ss.equals(s.getId()) && !session.getId().equals(ss)) {
                        s.getBasicRemote().sendText("xpopup");
                    }

                }
            }
        }
        session.getUserProperties().put("roomName", roomId);
        logger.log(Level.WARNING, "opened");*/
    }

    @OnClose
    public void onClose(Session session) {
        String rId = (String) session.getUserProperties().get("roomName");
        String[] clients = iSocketConnectionManager.getRoomById(rId);

        for (Session s : session.getOpenSessions()) {
            if (s.isOpen()
                    && rId.equals(s.getUserProperties().get("roomName"))) {
                try {
                    iSocketConnectionManager.updateRoom(rId, s.getId(), "remove");
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
      /*
       for global play uncomment this
      if (rId.equals("main")) {
            return;
        } else if (!iSocketConnectionManager.isRgstrdToRoom(session.getId())) {
            return;
        }
        iSocketConnectionManager.deleteRoom(rId);
        for (Session s : session.getOpenSessions()) {
            if (s.isOpen()
                    && rId.equals(s.getUserProperties().get("roomName"))) {
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/
    }

    @OnMessage
    public void onMessaege(String string, Session session) throws IOException {

        if (string.equals("clear")) {
            String sessionRoomName = (String) session.getUserProperties().get("roomName");
            String[] sessionIds = iSocketConnectionManager.getRoomById(sessionRoomName);
            for (String sessionId : sessionIds) {
                Set<Session> sessions = session.getOpenSessions();
                for (Session s : sessions) {
                    if (s.isOpen() || s.getId().equals(sessionId)) {
                        s.close();
                    }
                }
            }
            iSocketConnectionManager.clearRoomList();


        } else if (string.equals("clients")) {
            Map<String, String[]> list = iSocketConnectionManager.getRoomList();
            StringBuilder stringBuilder = new StringBuilder("clist");
            for (String s : list.keySet()) {
                String arr[] = list.get(s);
                for (String ss : arr) {
                    stringBuilder.append(ss + "\n");
                }
            }
            session.getBasicRemote().sendText(stringBuilder.toString());
        } else {
            //registret client handle message
            String rId = (String) session.getUserProperties().get("roomName");
            try {
                for (Session s : session.getOpenSessions()) {
                    if (s.isOpen()
                            && rId.equals(s.getUserProperties().get("roomName"))
                            && !session.getId().equals(s.getId())) {
                        s.getBasicRemote().sendText(session.getUserProperties().get("pp") + string);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } /* */
        }


    }

}

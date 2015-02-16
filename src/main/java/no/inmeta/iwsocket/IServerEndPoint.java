package no.inmeta.iwsocket;

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

    @OnOpen
    public void onOpen(Session session) throws IOException {
        //  session.getUserProperties().put("chatroom","chatroom");
        Map<String, String> pathPrm = session.getPathParameters();
        String firstPlayer = pathPrm.get("first");
        String secondPlayer = pathPrm.get("second");
       // String roomId = firstPlayer + "&" + secondPlayer;
        String sessionId = session.getId();
        String roomParticipant[];

        if (pathPrm.get("userAgent").contains("browserClient") ) {
            session.getUserProperties().put("roomName", "main");
            session.getUserProperties().put("roomManager", "main");
            iSocketConnectionManager.initRoom(sessionId,firstPlayer);
        }else if(pathPrm.get("userAgent").contains("androidClient") &&
                (null != iSocketConnectionManager.getRoomById("main") || iSocketConnectionManager.getRoomById("main").length != 0) ) {
                session.getUserProperties().put("pp",iSocketConnectionManager.updateRoom("main",sessionId,"addMc"));
        }else {
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
                    iSocketConnectionManager.updateRoom(rId,s.getId(),"remove");
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }}
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
                        s.getBasicRemote().sendText(session.getUserProperties().get("pp")+string);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } /* */
        }


    }

}

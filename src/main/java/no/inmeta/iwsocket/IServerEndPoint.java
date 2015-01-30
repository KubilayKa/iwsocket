package no.inmeta.iwsocket;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
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
        String roomId = firstPlayer + "&" + secondPlayer;
        String sessionId = session.getId();
        if (firstPlayer.equals("main")) return;
        if (pathPrm.get("userAgent").contains("androidClient")) {
            if (iSocketConnectionManager.isRoomExists(firstPlayer)) {
                roomId= secondPlayer +"&"+ firstPlayer;
                iSocketConnectionManager.updateRoom(roomId, sessionId, "addMc");
            } else {
                iSocketConnectionManager.initRoom(sessionId, roomId);
            }
            session.getBasicRemote().sendText("popup:"+roomId);
        } else {
            iSocketConnectionManager.updateRoom(roomId, sessionId, "addBc");
            Set<Session> openSessions = session.getOpenSessions();
            String[] roomMembers = iSocketConnectionManager.getRoomById(roomId);
            for (String ss : roomMembers) {
                for (Session s : openSessions) {
                    if (ss.equals(s.getId())&& !session.getId().equals(ss)) {
                        s.getBasicRemote().sendText("xpopup");
                    }

                }
            }
        }
        session.getUserProperties().put("roomName", roomId);
        logger.log(Level.WARNING, "opened");
    }

    @OnClose
    public void onClose(Session session) {
        String rId = (String) session.getUserProperties().get("roomName");
        for (Session s : session.getOpenSessions()) {
            if (s.isOpen()
                    && rId.equals(s.getUserProperties().get("roomName"))) {
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @OnMessage
    public void onMessaege(String string, Session session) throws IOException {

        if (string.equals("clear")) {
            String sessionRoomName = (String) session.getUserProperties().get("roomName");
            String[] sessionIds=iSocketConnectionManager.getRoomById(sessionRoomName);
            for (String sessionId:sessionIds){
                Set<Session> sessions = session.getOpenSessions();
                for(Session s:sessions){
                   if (s.isOpen()|| s.getId().equals(sessionId)){
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
        }

//registret client handle message
        String rId = (String) session.getUserProperties().get("roomName");
        try {
            for (Session s : session.getOpenSessions()) {
                if (s.isOpen()
                        && rId.equals(s.getUserProperties().get("roomName"))
                        && !session.getId().equals(s.getId())) {
                    s.getBasicRemote().sendText(string);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } /* */
    }

}

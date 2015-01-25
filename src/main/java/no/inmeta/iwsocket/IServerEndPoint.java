package no.inmeta.iwsocket;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
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
    public void onOpen(Session session) {

        //  session.getUserProperties().put("chatroom","chatroom");

        Map<String, Object> upr = session.getUserProperties();
        Map<String, String> pathPrm = session.getPathParameters();
        String firstPlayer = pathPrm.get("first");
        String secondPlayer = pathPrm.get("second");

        String roomId = firstPlayer + secondPlayer  ;
        String sessionId = session.getId();
        if (firstPlayer.equals("main")) return;
        if (pathPrm.get("userAgent").contains("androidClient")) {
             if (iSocketConnectionManager.isRoomExists(firstPlayer)){
                 iSocketConnectionManager.updateRoom(roomId,sessionId,"addMc");
             }else {
                 iSocketConnectionManager.initRoom(firstPlayer,roomId);
             }
        } else {
                iSocketConnectionManager.updateRoom(roomId,sessionId,"addBc");
        }
        session.getUserProperties().put("roomName", roomId);
      /*  String rm = "";
        String[] splitedString = string.split(":");
        if (!  iSocketConnectionManager.isRgstrdToRoom(session.getId()))
        rm = splitedString[1] + splitedString[2];
*/
        logger.log(Level.WARNING, "opened");
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("closed :/");
    }

    @OnMessage
    public void onMessaege(String string, Session session) throws IOException {

        System.out.println(string + " recieved sending to client This is from server size : ");

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

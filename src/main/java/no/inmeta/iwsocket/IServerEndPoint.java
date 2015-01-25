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
@ServerEndpoint(value = "/inbound/{userAgent}/{room}" )
public class IServerEndPoint {
    private ISocketConnectionManager iSocketConnectionManager = ISocketConnectionManager.getIstance();
    private Logger logger = iSocketConnectionManager.getLogger();
    @OnOpen
    public void onOpen(Session session) {

        //  session.getUserProperties().put("chatroom","chatroom");

        Map<String, Object> upr = session.getUserProperties();
        Map<String, String> pathPrm = session.getPathParameters();

        logger.log(Level.WARNING, "opened" );
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("closed :/");
    }

    @OnMessage
    public void onMessaege(String string, Session session) throws IOException {

        System.out.println(string + " recieved sending to client This is from server size : ");

        //String room = (String) session.getUserProperties().get("chatroom");
        String rm = "";
        String[] splitedString = string.split(":");
        if (!  iSocketConnectionManager.isRgstrdToRoom(session.getId())&&splitedString.length<=2)
            return;
        rm = splitedString[1] + splitedString[2];


//registret client handle message
                 /* this is for sending message back to clients
       try {
            for (Session s : session.getOpenSessions()) {
                if (s.isOpen()
                        && room.equals(s.getUserProperties().get("chatroom"))) {
                    s.getBasicRemote().sendText(string);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        }

}

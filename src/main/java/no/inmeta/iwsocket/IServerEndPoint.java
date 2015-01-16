package no.inmeta.iwsocket;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * Created by K on 1/16/15.
 */
@ServerEndpoint("/inbound")
public class IServerEndPoint {
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("opened");
        session.getUserProperties().put("chatroom","chatroom");

    }
    @OnClose
    public void onClose(Session session) {

    }
    @OnMessage
    public void onMessaege(String string,Session session) {
        System.out.println(  string + " recieved sending to client This is from server size : "  );
        String room = (String) session.getUserProperties().get("chatroom");
        try {
            for (Session s : session.getOpenSessions()) {
                if (s.isOpen()
                        && room.equals(s.getUserProperties().get("chatroom"))) {
                    s.getBasicRemote().sendText(string);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
   }
}

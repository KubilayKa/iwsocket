package no.inmeta.iwsocket;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * Created by K on 1/16/15.
 */
@ServerEndpoint("/inbound")
public class IServerEndPoint {
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("opened");

    }
    @OnClose
    public void onClose(Session session) {

    }
    @OnMessage
    public String onMessaege(String string,Session session) {
        System.out.println(  string + " recieved sending to client This is from server");return "This is from server";}
}

package no.inmeta.iwsocket;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;

/**
 * Created by K on 2/22/15.
 */
@ServerEndpoint(value = "/minbound")
public class MServerEndpoint {


        @OnOpen
        public void onOpen(Session session) throws IOException {
            //  session.getUserProperties().put("chatroom","chatroom");
            Map<String, String> pathPrm = session.getPathParameters();
           // String firstPlayer = pathPrm.get("first");
            //String secondPlayer = pathPrm.get("second");
            //String roomId = firstPlayer + "&" + secondPlayer;
            //String sessionId = session.getId();

            session.getUserProperties().put("roomName", "main");

        }

        @OnClose
        public void onClose(Session session)  {

        }

        @OnMessage
        public void onMessaege(String string, Session session) throws IOException {
            try {
                for (Session s : session.getOpenSessions()) {
                    if (s.isOpen()
                            && !session.getId().equals(s.getId())) {
                        s.getBasicRemote().sendText(string);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } /* */
        }
    }


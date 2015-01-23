package no.inmeta.iwsocket;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Set;

/**
 * Created by K on 1/16/15.
 */
@ServerEndpoint("/inbound")
public class IServerEndPoint {
    private ISocketConnectionManager iSocketConnectionManager = ISocketConnectionManager.getIstance();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("opened");


        for (Session s : session.getOpenSessions()) {
            System.out.println(s.getId());
        }

        //  session.getUserProperties().put("chatroom","chatroom");


    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("closed :/");
    }

    @OnMessage
    public void onMessaege(String string, Session session) {

        System.out.println(string + " recieved sending to client This is from server size : ");
        //String room = (String) session.getUserProperties().get("chatroom");
        String rm = "";
        String[] roomName = string.split(":");

        prime:
        if (null == iSocketConnectionManager.getClientFboBySid(session.getId())) {
            if (string.contains("mc")) {

                Set<String> rgstrddRooms = iSocketConnectionManager.getRoomNames();
                for (String s : rgstrddRooms) {
                    if (s.contains(roomName[1])) {
                        iSocketConnectionManager.updateRoom(s, session.getId(), "addMc"

                        );
                        break prime;
                    }

                }
                if (null != roomName  ) {
                    rm = roomName[1] + roomName[2];
                    iSocketConnectionManager.addSession( session.getId(),rm
                    );
                }
                session.getUserProperties().put(rm, rm);
            } else {
                //browser client
                Set<String> rgstrddRooms = iSocketConnectionManager.getRoomNames();
                for (String s : rgstrddRooms) {
                    if (s.contains(roomName[1])) {
                        iSocketConnectionManager.updateRoom(s,session.getId(),"addBc");
                    }

                }
            }
            //  iSocketConnectionManager.addSession(session.getId(), new IClientFbo().setSession(session).setRoomId(rm));
        } else {
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


}

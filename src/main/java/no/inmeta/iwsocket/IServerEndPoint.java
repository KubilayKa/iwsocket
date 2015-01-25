package no.inmeta.iwsocket;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by K on 1/16/15.
 */
@ServerEndpoint(value = "/inbound/{userAgent}/{first}/{second}" )
public class IServerEndPoint {
    private ISocketConnectionManager iSocketConnectionManager = ISocketConnectionManager.getIstance();
    private Logger logger = iSocketConnectionManager.getLogger();
    @OnOpen
    public void onOpen(Session session) {

        //  session.getUserProperties().put("chatroom","chatroom");

        Map<String, Object> upr = session.getUserProperties();
        Map<String, String> pathPrm = session.getPathParameters();
        String firstPlayer= pathPrm.get("first");
        String secondPlayer = pathPrm.get("second");
        String time=String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND));
        String salt = time.substring(0,3);
        String roomId=firstPlayer+secondPlayer+salt;
        String sessionId=session.getId();
        if (firstPlayer.equals("main"))return;
                if (pathPrm.get("userAgent").contains("androidClient")){
                    if (!iSocketConnectionManager.isRgstrdToRoom(firstPlayer)){
                        iSocketConnectionManager.initRoom(sessionId,roomId);
                    }else {
                        iSocketConnectionManager.updateRoom(roomId,sessionId,"addMc");
                    }
                }else{
                     iSocketConnectionManager.updateRoom(roomId,sessionId,"addBc");
                }
        session.getUserProperties().put("roomName",roomId);
      /*  String rm = "";
        String[] splitedString = string.split(":");
        if (!  iSocketConnectionManager.isRgstrdToRoom(session.getId()))
        rm = splitedString[1] + splitedString[2];
*/
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

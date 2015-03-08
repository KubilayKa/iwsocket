package no.inmeta.iwsocket;


import org.json.simple.JSONObject;
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
    private IWMessageEcoder iwMessageEcoder = new IWMessageEcoder();

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
                iSocketConnectionManager.initRoom(sessionId, "main");
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
            if (sessions.size() == 3) {
                byte[] fpPic = iSocketConnectionManager.getPicBytes(firstPlayer);
                byte[] spPic = iSocketConnectionManager.getPicBytes(secondPlayer);
                try {
                    PicFbo picFboF = new PicFbo().setUserName(firstPlayer).setB64(iwMessageEcoder.toB64(fpPic)).setPos("f");
                    PicFbo picFboS = new PicFbo().setUserName(secondPlayer).setB64(iwMessageEcoder.toB64(spPic)).setPos("s");
                    sesArr[0].getBasicRemote().sendObject(iwMessageEcoder.jsonify(picFboF));
                    sesArr[0].getBasicRemote().sendObject(iwMessageEcoder.jsonify(picFboS));
                    sesArr[1].getUserProperties().put("userName", firstPlayer);
                    sesArr[2].getUserProperties().put("userName", secondPlayer);
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
                    if (null == s.getUserProperties().get("roomManager")) {
                        s.close();
                    }

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
        } else if (string.contains("results")) {
               registerResults(string,session);
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

    private void registerResults(String string, Session session) {
        String[] results= string.split(":");
        String person1=results[1];
        String person2=results[3];

        JSONObject jsonObjectf = new JSONObject();
        JSONObject jsonObjects = new JSONObject();
        jsonObjectf.put("_id",person1);
        jsonObjectf.put("score",results[2]);
        jsonObjectf.put("win",results[2]);
        jsonObjectf.put("lost",results[2]);
        jsonObjectf.put("draw",results[2]);
        jsonObjects.put("_id",person2);
        jsonObjects.put("score",results[4]);
        jsonObjects.put("win",results[4]);
        jsonObjects.put("lost",results[4]);
        jsonObjects.put("draw",results[4]);

        JSONObject[] objects={jsonObjectf,jsonObjects};
        for(int i=0;i<objects.length;i++){
            if (dbClient.contains((String)objects[i].get("_id"))) {
                dbClient.update(objects[i]);
            }else {
                dbClient.save(objects[i]);
            }
        }

       // Response response=dbClient.save( );
    }

}

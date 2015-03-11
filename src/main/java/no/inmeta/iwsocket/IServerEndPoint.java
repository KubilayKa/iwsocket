package no.inmeta.iwsocket;


import org.json.simple.JSONObject;
import org.lightcouch.CouchDbClient;

import javax.websocket.*;
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
            if (null == roomParticipant || null == roomParticipant[0] || "".equals(roomParticipant[0])) {
                session.getUserProperties().put("roomName", "main");
                session.getUserProperties().put("roomManager", "yes");
                iSocketConnectionManager.initRoom(sessionId, "main");

            } else {
                session.close();
                return;
            }
        } else if (pathPrm.get("userAgent").contains("androidClient") && null != roomParticipant[0] &&
                (  null == roomParticipant[1] || null == roomParticipant[2])) {
            String post = iSocketConnectionManager.updateRoom("main", sessionId, "addMc");
            session.getUserProperties().put("pp", post);
            session.getUserProperties().put("roomName", "main");
            Set<Session> sessions = session.getOpenSessions();
            Session[] sesArr = sessions.toArray(new Session[3]);
            sesArr[1].getUserProperties().put("userName", firstPlayer);
            sesArr[2].getUserProperties().put("userName", secondPlayer);
            sesArr[1].getUserProperties().put("roomManager", "no");
            sesArr[2].getUserProperties().put("roomManager","no");
            if (null != sesArr[1] && null != sesArr[2]) {
                byte[] fpPic = iSocketConnectionManager.getPicBytes(firstPlayer);
                byte[] spPic = iSocketConnectionManager.getPicBytes(secondPlayer);
                try {
                    JSONObject  firsJSObject= new JSONObject();
                    JSONObject  secondJSObject= new JSONObject();
                    if (dbClient.contains(firstPlayer)) {
                         firsJSObject = dbClient.find(JSONObject.class, firstPlayer);
                    }
                    if (dbClient.contains(secondPlayer)) {
                          secondJSObject = dbClient.find(JSONObject.class, firstPlayer);
                    }
                    PicFbo picFboF = new PicFbo().setUserName(firstPlayer).setB64(iwMessageEcoder.toB64(fpPic)).setPos("f").setStat(firsJSObject.toString());
                    PicFbo picFboS = new PicFbo().setUserName(secondPlayer).setB64(iwMessageEcoder.toB64(spPic)).setPos("s").setStat(secondJSObject.toString());
                    for (Session s:sesArr) {
                        String str= (String) s.getUserProperties().get("roomManager");
                        if (null != str && str.equals("yes")) {
                            s.getBasicRemote().sendObject(iwMessageEcoder.jsonify(picFboF));
                            s.getBasicRemote().sendObject(iwMessageEcoder.jsonify(picFboS));
                        }
                    }


                } catch (EncodeException e) {
                    e.printStackTrace();
                }
            }
        } else {
            session.getBasicRemote().sendText("popup:En annen spill er i gang, vent på din tur dude!");
            session.close();
            return;
        }

    }

    @OnClose
    public void onClose(Session session) {

    /*    try {
            killCon(session);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    @OnMessage
    public void onMessaege(String string, Session session) throws IOException {

        if (string.equals("clear")) {
            killCon(session);

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
            registerResults(string, session);
        } else if (string.equals("gameover")) {
            killCon(session);
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
            }
        }


    }

    private void killCon(Session session) throws IOException {
        for (Session s : session.getOpenSessions()) {
            String isManager = (String) s.getUserProperties().get("roomManager");
            if (s.isOpen() && isManager.equals("no")) {
                s.close();
                iSocketConnectionManager.updateRoom("main", s.getId(), "remove");
            }
        }
        Session[] sessArr= (Session[]) session.getOpenSessions().toArray(new Session[1]);
        sessArr[0].getBasicRemote().sendText("maintain");
    }

    private void registerResults(String string, Session session) {
        String[] results = string.split(":");
        String person1 = results[1];
        String person2 = results[3];

        JSONObject jsonObjectf = new JSONObject();
        JSONObject jsonObjects = new JSONObject();
        jsonObjectf.put("_id", person1);
        jsonObjectf.put("score", Integer.parseInt(results[2]));
        jsonObjectf.put("win", 0);
        jsonObjectf.put("lost", 0);
        jsonObjectf.put("draw", 0);
        jsonObjects.put("_id", person2);
        jsonObjects.put("score", Integer.parseInt(results[4]));
        jsonObjects.put("win", 0);
        jsonObjects.put("lost", 0);
        jsonObjects.put("draw", 0);

        JSONObject[] objects = {jsonObjectf, jsonObjects};
        generateStats(objects);
        for (int i = 0; i < objects.length; i++) {
            String id = (String) objects[i].get("_id");
            if (dbClient.contains(id)) {
                dbClient.update(updateStats(objects[i], dbClient.find(JSONObject.class, id)));
            } else {
                dbClient.save(objects[i]);
            }
        }

        // Response response=dbClient.save( );
    }

    private JSONObject updateStats(JSONObject object, JSONObject oldRecord) {
        JSONObject tmp = oldRecord;
        double oScore = (double) oldRecord.get("score");
        int nScore = (int) object.get("score");
        oldRecord.put("score", oScore + nScore);
        int oWin = (int) object.get("win");
        double nWin = (double) tmp.get("win");
        oldRecord.put("win", oWin + nWin);
        int oLost = (int) object.get("lost");
        double nLost = (double) tmp.get("lost");
        oldRecord.put("lost", oLost + nLost);
        int oDraw = (int) object.get("draw");
        double nDraw = (double) tmp.get("draw");
        oldRecord.put("draw", oDraw + nDraw);
        return oldRecord;

    }

    private void generateStats(JSONObject[] objects) {
        int fScore = (int) objects[0].get("score");
        int sScore = (int) objects[1].get("score");
        if (fScore > sScore) {
            objects[0].put("win", 1);
            objects[1].put("lost", 1);
        } else if (fScore < sScore) {
            objects[0].put("lost", 1);
            objects[1].put("win", 1);
        } else {
            objects[0].put("draw", 1);
            objects[1].put("draw", 1);
        }
    }

}

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
import java.util.logging.Level;
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
        Map<String, String> pathPrm = session.getPathParameters();
        String firstPlayer = pathPrm.get("first");
        String secondPlayer = pathPrm.get("second");
        String roomParticipant[] = iSocketConnectionManager.getRoomById("main");
        if (pathPrm.get("userAgent").contains("browserClient")) {
            if (null == roomParticipant || null == roomParticipant[0] || "".equals(roomParticipant[0])) {
                String sessionId = session.getId();
                session.getUserProperties().put("roomName", "main");
                session.getUserProperties().put("roomManager", "yes");
                iSocketConnectionManager.initRoom(sessionId, "main", session);
                logger.log(Level.WARNING, "browser connection opened " + sessionId + ": user properties " + session.getUserProperties().get("roomManager"));
            } else {
                session.close();
                return;
            }
        } else if (pathPrm.get("userAgent").contains("androidClient") && !iSocketConnectionManager.isRoomReady()) {
            String sessionId = session.getId();
            String post;
            logger.log(Level.WARNING, "mobile client connected: " + sessionId + "names:" + firstPlayer + ":" + secondPlayer);
            if (secondPlayer.equals("UnknownUser")) {
                post = iSocketConnectionManager.updateRoom("main", sessionId, "addMc", "second");
            } else {
                post = iSocketConnectionManager.updateRoom("main", sessionId, "addMc", "first");
            }
            session.getUserProperties().put("pp", post);
            session.getUserProperties().put("roomName", "main");

            if (iSocketConnectionManager.isRoomReady()) {
                logger.log(Level.WARNING, "ses arr 1 2 is not null");
                byte[] fpPic = iSocketConnectionManager.getPicBytes(firstPlayer);
                byte[] spPic = iSocketConnectionManager.getPicBytes(secondPlayer);
                try {
                    JSONObject firsJSObject = new JSONObject();
                    JSONObject secondJSObject = new JSONObject();
                    if (dbClient.contains(firstPlayer)) {
                        firsJSObject = dbClient.find(JSONObject.class, firstPlayer);
                    }
                    if (dbClient.contains(secondPlayer)) {
                        secondJSObject = dbClient.find(JSONObject.class, secondPlayer);
                    }
                    PicFbo picFboF = new PicFbo().setUserName(firstPlayer).setB64(iwMessageEcoder.toB64(fpPic)).setPos("f").setStat(firsJSObject.toString());
                    PicFbo picFboS = new PicFbo().setUserName(secondPlayer).setB64(iwMessageEcoder.toB64(spPic)).setPos("s").setStat(secondJSObject.toString());
                    logger.log(Level.WARNING, "before pic send");
                    iSocketConnectionManager.getMainSession().getBasicRemote().sendObject(iwMessageEcoder.jsonify(picFboF));
                    iSocketConnectionManager.getMainSession().getBasicRemote().sendObject(iwMessageEcoder.jsonify(picFboS));
                    logger.log(Level.WARNING, " pic sended");
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
        logger.log(Level.WARNING, "onclose" + session.getId());
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
            if (s.isOpen() &&  s.getId() != iSocketConnectionManager.getMainSession().getId()) {
                s.close();
                iSocketConnectionManager.updateRoom("main", s.getId(), "remove", null);
            }
        }
        Session[] sessArr = (Session[]) session.getOpenSessions().toArray(new Session[1]);
        iSocketConnectionManager.getMainSession().getBasicRemote().sendText("maintain");
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

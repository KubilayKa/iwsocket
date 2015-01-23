package no.inmeta.iwsocket;

import javax.websocket.Session;

/**
 * Created by K on 1/23/15.
 */
public class IClientFbo {
    private String userName;
    private String sessionId;
    private Session session;
    private String clientManager;
    private String roomId;
    private String gameType;

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getRoomId() {
        return roomId;
    }

    public IClientFbo setRoomId(String roomId) {
        this.roomId = roomId;
        return this;
    }

    public Session getSession() {
        return session;
    }

    public IClientFbo setSession(Session session) {
        this.session = session;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public IClientFbo setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getSessionId() {
        return sessionId;
    }

    public IClientFbo setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public String getClientManager() {
        return clientManager;
    }

    public IClientFbo setClientManager(String clientManager) {
        this.clientManager = clientManager;
    return this;}
}

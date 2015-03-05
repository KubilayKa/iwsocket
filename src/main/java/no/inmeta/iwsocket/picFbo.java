package no.inmeta.iwsocket;

import java.io.Serializable;

/**
 * Created by kubkaray on 24.02.2015.
 */
public class PicFbo implements Serializable {
    private static final long serialVersionUID = 1420672609912364060L;
    private String userName;
    private String b64;
    private String pos;

    public String getB64() {
        return b64;
    }

    public PicFbo setB64(String b64) {
        this.b64 = b64;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public PicFbo setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public PicFbo setPos(String pos) {
        this.pos = pos;return this;
    }

    public String getPos() {
        return pos;
    }
}

package no.inmeta.iwsocket;

import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

/**
 * Created by kubkaray on 03.03.2015.
 */
public class IWMessageEcoder implements Encoder.Binary {

    public String jsonify(Object object){
        String s=new Gson().toJson(object);
        return s;
    }
    public byte[] encode(PicFbo object)   {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = null;
        try {
            o = new ObjectOutputStream(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            o.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b.toByteArray();
    }
    public String toB64(byte[] bytes) {
        StringBuilder sb= new StringBuilder();
        sb.append("data:image/jpeg;base64,");
        sb.append(StringUtils.newStringUtf8(Base64.encodeBase64(bytes,false)));
        return sb.toString();
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public ByteBuffer encode(Object object) throws EncodeException {
        return null;
    }
}

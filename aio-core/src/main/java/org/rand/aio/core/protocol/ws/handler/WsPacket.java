package org.rand.aio.core.protocol.ws.handler;

import org.rand.aio.core.decoder.AioPacket;

/**
 * Created by fengliang on 2021/4/8.
 */
public class WsPacket implements AioPacket {

    private byte[] body;
    private String id;
    public WsPacket(String id){
        this.id=id;
    }

    @Override
    public byte[] getBytes() {
        return body;
    }

    public void setBytes(byte[] body) {
        this.body=body;
    }

    @Override
    public String getId() {
        return id;
    }

}

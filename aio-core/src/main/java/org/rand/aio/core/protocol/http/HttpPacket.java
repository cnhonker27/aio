package org.rand.aio.core.protocol.http;

import org.rand.aio.core.decoder.AioPacket;

public class HttpPacket implements AioPacket {

    private byte[] body;

    private String id;

    public HttpPacket(String id){
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

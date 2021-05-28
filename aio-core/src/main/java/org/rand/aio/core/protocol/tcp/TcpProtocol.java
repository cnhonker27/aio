package org.rand.aio.core.protocol.tcp;

import org.rand.aio.core.decoder.AioPacket;
import org.rand.aio.core.decoder.AioRequest;
import org.rand.aio.core.decoder.AioResponse;
import org.rand.aio.core.protocol.AioServerProtocolProcessor;

import java.nio.ByteBuffer;

public class TcpProtocol extends AioServerProtocolProcessor {
    @Override
    public AioRequest decoder(ByteBuffer byteBuffer) {
        return null;
    }

    @Override
    public AioResponse handler(AioRequest aioRequest) {
        return null;
    }

    @Override
    public AioPacket encoder(AioResponse aioResponse) {
        return null;
    }

    @Override
    public boolean isSupportProtocol(ByteBuffer byteBuffer) {
        return false;
    }

    @Override
    public String getProtocolName() {
        return null;
    }

    @Override
    public ByteBuffer getResponseByteBuffer() {
        return null;
    }
    @Override
    public AioRequest getAioRequest() {
        return null;
    }

}

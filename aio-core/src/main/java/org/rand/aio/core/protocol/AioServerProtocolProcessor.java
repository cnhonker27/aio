package org.rand.aio.core.protocol;

import org.rand.aio.core.decoder.AioPacket;
import org.rand.aio.core.decoder.AioRequest;
import org.rand.aio.core.decoder.AioResponse;
import org.rand.aio.core.decoder.http.AioHttpRequestDecoder;
import org.rand.aio.core.handler.call.ReadWriteContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * Created by fengliang on 2021/4/9.
 */
public abstract class AioServerProtocolProcessor extends AioHttpRequestDecoder implements AioServerProtocolDispatcherProcessor,AioProtocol {

    private static Logger logger= LoggerFactory.getLogger(AioServerProtocolProcessor.class);

    private ReadWriteContext readWriteContext;

    @Override
    public void invoke(ByteBuffer byteBuffer) {
        AioRequest decoder = decoder(byteBuffer);
        AioResponse handler = handler(decoder);
        AioPacket encoder = encoder(handler);
        readWriteContext.write(encoder);
        readWriteContext.getAioServerSession().setHandshake(true);
    }

    @Override
    public AioProtocol clone() throws CloneNotSupportedException{
        return (AioServerProtocolProcessor)super.clone();
    }

    public void setReadWriteContext(ReadWriteContext readWriteContext) {
        this.readWriteContext = readWriteContext;
    }

    public ReadWriteContext getReadWriteContext() {
        return readWriteContext;
    }
}

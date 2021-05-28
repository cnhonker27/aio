package org.rand.aio.core.protocol;

import org.rand.aio.core.decoder.AioPacket;
import org.rand.aio.core.decoder.AioRequest;
import org.rand.aio.core.decoder.AioResponse;

import java.nio.ByteBuffer;

public interface AioProtocol extends AioServerProtocolDispatcherProcessor, Cloneable{
    /**
     * 解码
     * @param byteBuffer
     * @return
     */
    AioRequest decoder(ByteBuffer byteBuffer);

    /**
     * 业务处理在这里扩展
     * @param aioRequest
     * @return
     */
    AioResponse handler(AioRequest aioRequest);

    /**
     * 编码
     * @param aioResponse
     * @return
     */
    AioPacket encoder(AioResponse aioResponse);

    boolean isSupportProtocol(ByteBuffer byteBuffer);

    String getProtocolName();

    ByteBuffer getResponseByteBuffer();

    AioRequest getAioRequest();

    AioProtocol clone() throws CloneNotSupportedException;;
}

package org.rand.aio.core.protocol;

import java.nio.ByteBuffer;

public interface AioServerProtocolDispatcherProcessor {

    void invoke(ByteBuffer byteBuffer);

}

package org.rand.aio.core.decoder;

import java.nio.ByteBuffer;

public interface AioRequestDecoder {

    AioRequest decoderRequest(ByteBuffer byteBuffer);

    void encoderRequest(ByteBuffer byteBuffer);

    boolean support();

}

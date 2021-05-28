package org.rand.aio.core.protocol.ws.processor;

import org.rand.aio.core.decoder.AioPacket;
import org.rand.aio.core.decoder.AioRequest;
import org.rand.aio.core.handler.call.ReadWriteContext;

public interface WsProcessor {

    String getSessionId();

    default boolean onConnect(AioRequest request){
        return true;
    }

    void onReceiver(AioPacket packet, ReadWriteContext readWriteContext);

    default String onPing(){
        return "pong";
    }

    default String onPong(){
        return "ping";
    }

    default void onClose(){}

    default byte[] onBinary(){
        return new byte[0];
    }

    default void onError(Exception e){}

    default void onExtend(){}

    byte[] getBytes();

    ReadWriteContext getReadWriteContext();

}

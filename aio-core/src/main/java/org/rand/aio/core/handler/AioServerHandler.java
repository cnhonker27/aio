package org.rand.aio.core.handler;

import org.rand.aio.core.handler.call.ReadWriteContext;
import org.rand.aio.session.AioServerSession;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * Created by fengliang on 2021/3/31.
 */
public interface AioServerHandler {
    void read(ReadWriteContext readWriteContext);

    void close(ReadWriteContext readWriteContext);

    void close(AsynchronousSocketChannel channel);

    void close(AsynchronousSocketChannel channel,AioServerSession aioServerSession);


}

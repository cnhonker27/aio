package org.rand.aio.core.handler.call;

import org.rand.aio.core.decoder.AioPacket;
import org.rand.aio.core.handler.ReadCompletionHandler;
import org.rand.aio.core.handler.WriteCompletionHandler;
import org.rand.aio.core.task.AioTask;
import org.rand.aio.session.AioServerSession;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * Created by fengliang on 2021/3/31.
 */
public interface ReadWriteContext{

    ByteBuffer getReadByteBuffer();

    AioServerSession getAioServerSession();

    AsynchronousSocketChannel getAsynchronousSocketChannel();

    void read();

    void write(AioPacket aioPacket);

    boolean isClose();

    void close();

    void await(AioTask task);

    void signal(AioTask aioTask);

}

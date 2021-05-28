package org.rand.aio.core.handler;

import org.rand.aio.core.decoder.AioPacket;
import org.rand.aio.core.handler.call.DefaultReadWriteContext;
import org.rand.aio.core.handler.call.ReadWriteContext;
import org.rand.aio.context.AioServerContext;
import org.rand.aio.session.AioServerSession;
import org.rand.aio.session.DefaultAioServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *  {@link AcceptCompletionHandler}接受请求连接控制器
 *  在客户端与服务端断开连接前，该控制器只会执行一次
 *  在保持连接的情况下，请求会直接走{@link ReadCompletionHandler} 进行通讯
 *  注：该类下不应该有全局变量，否则会有未知错误，这种错误可能是全局变量被修改引起的问题,
 *  如果要写就一定要办证变量地址不会被修改
 */

public class AcceptCompletionHandler extends AioCommonServerHandler implements CompletionHandler<AsynchronousSocketChannel, AioServerContext>{

    private Logger logger= LoggerFactory.getLogger(AcceptCompletionHandler.class);

    @Override
    public void completed(AsynchronousSocketChannel channel, AioServerContext context) {
        AioServerSession aioServerSession;
        ReadWriteContext readWriteContext;
        try {
            InetSocketAddress remoteAddress = (InetSocketAddress)channel.getRemoteAddress();
            aioServerSession=new DefaultAioServerSession(remoteAddress);
            readWriteContext= createReadWriteContext(aioServerSession,channel,context);
        } catch (IOException e) {
            logger.error("获取远程地址异常",e);
            close(channel);
            return;
        }
        context.getAioServerListener().before(aioServerSession);
        read(readWriteContext);
        context.accept(context,this);
    }

    @Override
    public void failed(Throwable exc, AioServerContext context) {
        logger.error("接收请求出现错误",exc);
    }

    private ReadWriteContext createReadWriteContext(AioServerSession aioServerSession,AsynchronousSocketChannel channel,AioServerContext context){
        ByteBuffer allocate = ByteBuffer.allocate(context.getAioConfiguration().getByteBufferSize());
        return new DefaultReadWriteContext(allocate, aioServerSession, channel,context);
    }
}

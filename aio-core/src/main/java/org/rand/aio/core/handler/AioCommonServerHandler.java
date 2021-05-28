package org.rand.aio.core.handler;

import org.rand.aio.core.handler.call.ReadWriteContext;
import org.rand.aio.core.handler.dispatcher.ProtocolDispatcher;
import org.rand.aio.core.handler.dispatcher.ProtocolDispatcherImpl;
import org.rand.aio.session.AioServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * Created by fengliang on 2021/3/31.
 */
public abstract class AioCommonServerHandler implements ProtocolDispatcher,AioServerHandler {

    private final static Logger logger= LoggerFactory.getLogger(AioCommonServerHandler.class);

    private ProtocolDispatcher protocolDispatcher;

    public AioCommonServerHandler(){

    }

    public AioCommonServerHandler(ProtocolDispatcher protocolDispatcher){
        this.protocolDispatcher=protocolDispatcher;
    }

    @Override
    public void read(ReadWriteContext readWriteContext) {
        readWriteContext.read();
    }

    @Override
    public void doProtocolReceiver(ReadWriteContext readWriteContext) throws Throwable{
        this.protocolDispatcher.doProtocolReceiver(readWriteContext);
    }

    @Override
    public void close(ReadWriteContext readWriteContext) {
        close(readWriteContext.getAsynchronousSocketChannel(),readWriteContext.getAioServerSession());
    }

    @Override
    public void close(AsynchronousSocketChannel channel){
        close(channel,null);
    }

    @Override
    public void close(AsynchronousSocketChannel channel,AioServerSession aioServerSession) {
        try {
            // 关闭输入（即：关闭接收客户端数据的进程）
            channel.shutdownInput();
            // 关闭输出（即：向客户端返回数据的进程）
            channel.shutdownOutput();
            // 关闭所有
            channel.close();
        } catch (IOException e) {
            logger.error("关闭异常",e);
        }finally {
           if(aioServerSession!=null){
               logger.info(aioServerSession.toString()+" 已关闭");
           }else{
               logger.info(channel.toString()+" 已关闭");
           }
        }
    }
}

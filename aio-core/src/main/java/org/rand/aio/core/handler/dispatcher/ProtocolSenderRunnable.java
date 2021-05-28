package org.rand.aio.core.handler.dispatcher;

import org.rand.aio.core.decoder.AioResponse;
import org.rand.aio.core.handler.call.ReadWriteContext;
import org.rand.aio.core.protocol.AioProtocol;

import java.nio.ByteBuffer;

public class ProtocolSenderRunnable implements Runnable {

    ReadWriteContext readWriteContext;
    /**
     *         抛出异常    特殊值        阻塞         超时
     * 插入    add(e)      offer(e)      put(e)    offer(e, time, unit)
     * 移除    remove()    poll()        take()    poll(time, unit)
     * 检查    element()   peek()        不可用    不可用
     */

    @Override
    public void run() {
       /* WriteCompletionHandler writeCompletionHandler = aioServerContext.getWriteCompletionHandler();
        ByteBuffer responseByteBuffer =aioServerContext.getAioProtocol().encoder(this.aioResponse,aioServerContext);
        if(responseByteBuffer!=null&&responseByteBuffer.hasRemaining()){
            aioServerContext.getAsynchronousSocketChannel().write(responseByteBuffer,responseByteBuffer,writeCompletionHandler);
        }
        aioServerContext.setHandshake(true);*/
    }

   public ProtocolSenderRunnable(ReadWriteContext readWriteContext){
        this.readWriteContext=readWriteContext;
    }

}

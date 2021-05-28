package org.rand.aio.core.handler.dispatcher;

import org.rand.aio.core.decoder.AioRequest;
import org.rand.aio.core.decoder.AioResponse;
import org.rand.aio.core.protocol.AioProtocol;

import java.nio.ByteBuffer;

public class ProtocolReceiverRunnable implements Runnable {

    private AioProtocol aioProtocol;

    private ByteBuffer receiverByteBuffer;

    private AioRequest request;

    /**
     *         抛出异常    特殊值        阻塞         超时
     * 插入    add(e)      offer(e)      put(e)    offer(e, time, unit)
     * 移除    remove()    poll()        take()    poll(time, unit)
     * 检查    element()   peek()        不可用    不可用
     */

    @Override
    public void run() {
        /*this.aioProtocol.decoder(receiverByteBuffer,aioServerContext);*/
        /*ProtocolSenderRunnable protocolSenderRunnable = new ProtocolSenderRunnable(aioServerContext, aioProtocol);
        aioServerContext.getAioServer().getExecutorServiceSender().execute(protocolSenderRunnable);*/
        //aioServerContext.getProtocolDispatcher().doProtocolSender(aioServerContext,this.aioProtocol);
       /* AioResponse aioResponse = aioProtocol.handler(this.request, aioServerContext);
        ProtocolSenderRunnable protocolSenderRunnable = new ProtocolSenderRunnable(aioServerContext,aioResponse);
        aioServerContext.getAioServer().getExecutorServiceSender().submit(protocolSenderRunnable);*/
    }

    public ProtocolReceiverRunnable(AioProtocol aioProtocol,ByteBuffer receiverByteBuffer){
        this.aioProtocol=aioProtocol;
        this.receiverByteBuffer=receiverByteBuffer;
    }

}

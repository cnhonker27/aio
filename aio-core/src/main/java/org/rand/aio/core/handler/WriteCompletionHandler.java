package org.rand.aio.core.handler;

import org.rand.aio.core.handler.call.ReadWriteContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Semaphore;

/**
 * Created by fengliang on 2021/3/31.
 */
public class WriteCompletionHandler extends AioCommonServerHandler implements CompletionHandler<Integer ,ByteBuffer> {

    Logger logger = LoggerFactory.getLogger(WriteCompletionHandler.class);

    private ReadWriteContext readWriteContext;

    public WriteCompletionHandler(ReadWriteContext readWriteContext) {
        this.readWriteContext = readWriteContext;
    }


    @Override
    public void completed(Integer result, ByteBuffer byteBuffer) {
        if(result>0){
           readWriteContext.getAsynchronousSocketChannel().write(byteBuffer,byteBuffer,this);
        }else{
            // 唤醒线程
            this.readWriteContext.signal(()->logger.info("{}被唤醒了{}",readWriteContext.getAioServerSession().toString(),readWriteContext.getAioServerSession().getAioProtocol()));
        }

    }

    @Override
    public void failed(Throwable exc, ByteBuffer byteBuffer) {
        logger.error("写出数据错误",exc);
        super.close(readWriteContext);
    }

}

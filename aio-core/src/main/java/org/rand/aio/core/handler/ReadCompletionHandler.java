package org.rand.aio.core.handler;

import org.rand.aio.core.handler.call.ReadWriteContext;
import org.rand.aio.core.handler.dispatcher.ProtocolDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Created by fengliang on 2021/3/31.
 */
public class ReadCompletionHandler extends AioCommonServerHandler implements CompletionHandler<Integer ,ByteBuffer> {

    Logger logger = LoggerFactory.getLogger(ReadCompletionHandler.class);

    private ReadWriteContext readWriteContext;

    public ReadCompletionHandler(ReadWriteContext readWriteContext, ProtocolDispatcher protocolDispatcher){
        super(protocolDispatcher);
        this.readWriteContext=readWriteContext;
    }

    @Override
    public void completed(Integer result, ByteBuffer byteBuffer) {
        if(logger.isDebugEnabled()){
            logger.info("接收{}个字节",result);
        }
        if(result>0){
            try {
                super.doProtocolReceiver(readWriteContext);
            } catch (Throwable e) {
                logger.info("数据读写异常",e);
                super.close(readWriteContext);
            }
        }else{
            super.close(readWriteContext);
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer byteBuffer) {
        logger.error("读取数据错误",exc);
        super.close(readWriteContext);
    }

}

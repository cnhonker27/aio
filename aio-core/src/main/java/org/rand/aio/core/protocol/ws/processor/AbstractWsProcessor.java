package org.rand.aio.core.protocol.ws.processor;

import org.rand.aio.core.handler.call.ReadWriteContext;
import org.rand.aio.util.AioKit;
import org.rand.aio.util.StringKit;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public abstract class AbstractWsProcessor implements WsProcessor {

    private ReadWriteContext readWriteContext;

    private byte[] message;

    private Charset charset=StandardCharsets.UTF_8;

    private Integer limit=2048*10;

    public void write(String message){
        StringKit.hasText(message,"写出消息不能为空");
        write(message.getBytes(getCharset()));
    }

    public void write(byte[] message){
        write(this.readWriteContext,message);
    }

    public void write(ReadWriteContext readWriteContext,byte[] message){
        if(message.length>this.limit){
            throw new RuntimeException("发送内容超出最大限制:"+limit);
        }
        if(this.readWriteContext.hashCode()!=readWriteContext.hashCode()){
            AioKit.send(readWriteContext,message);
        }else{
            this.message=message;
        }
    }

    public Charset getCharset() {
        return this.charset;
    }

    public final byte[] getBytes() {
        return message;
    }


    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public final void setReadWriteContext(ReadWriteContext readWriteContext) {
        this.readWriteContext = readWriteContext;
       /* if(this.readWriteContext==null){
            synchronized (this){
                if(this.readWriteContext==null){
                    this.readWriteContext = readWriteContext;
                }
            }
        }*/
    }

    @Override
    public String getSessionId() {
        return this.readWriteContext.getAioServerSession().getId();
    }

    @Override
    public ReadWriteContext getReadWriteContext() {
        return readWriteContext;
    }
}

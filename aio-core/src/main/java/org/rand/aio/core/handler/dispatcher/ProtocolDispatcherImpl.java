package org.rand.aio.core.handler.dispatcher;

import org.rand.aio.core.handler.call.ReadWriteContext;
import org.rand.aio.core.protocol.AioProtocol;
import org.rand.aio.core.protocol.AioServerProtocolProcessor;
import org.rand.aio.session.AioServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Collection;

public class ProtocolDispatcherImpl implements ProtocolDispatcher {

    private final static Logger logger= LoggerFactory.getLogger(ProtocolDispatcherImpl.class);

    private DefaultProtocolManager defaultProtocolManager;

    public ProtocolDispatcherImpl(DefaultProtocolManager defaultProtocolManager){
        this.defaultProtocolManager=defaultProtocolManager;
    }

    @Override
    public void doProtocolReceiver(ReadWriteContext readWriteContext) throws Throwable {
        ByteBuffer requestBuffer = readWriteContext.getReadByteBuffer();
        AioServerSession aioServerSession = readWriteContext.getAioServerSession();
        Collection<AioProtocol> protocols = defaultProtocolManager.getProtocols();
        boolean supportRequest=false;
        if(aioServerSession.getHandshake()){
            handler(requestBuffer,readWriteContext);
            supportRequest=true;
        }else{
            for(AioProtocol aioProtocol:protocols){
                // 多线程下存在对象引用同一个的问题，克隆一个新的对象
                // TODO 等待找到更好的思路解决这个问题
                AioProtocol cloneProtocol = aioProtocol.clone();
                if(supportRequest=doSupportProtocol(cloneProtocol,requestBuffer)){
                    aioServerSession = readWriteContext.getAioServerSession();
                    aioServerSession.setAioProtocol(cloneProtocol);
                    handler(requestBuffer,readWriteContext);
                    break;
                }
            }
        }
        if(!supportRequest){
            readWriteContext.close();
            return;
        }
        readWriteContext.read();
    }

    private boolean doSupportProtocol(AioProtocol aioProtocol,ByteBuffer requestBuffer){
        if(aioProtocol.isSupportProtocol(requestBuffer)){
            return true;
        }
        return false;

    }

    private void handler(ByteBuffer requestBuffer,ReadWriteContext readWriteContext) {
        AioServerProtocolProcessor aioProtocol =(AioServerProtocolProcessor) readWriteContext.getAioServerSession().getAioProtocol();
        try {
            aioProtocol.setReadWriteContext(readWriteContext);
            aioProtocol.invoke(requestBuffer);
        } catch (Exception e) {
            logger.error("解码或编码异常",e);
            readWriteContext.close();
        }
    }
}
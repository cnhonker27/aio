package org.rand.aio.util;

import org.rand.aio.core.handler.call.ReadWriteContext;
import org.rand.aio.core.protocol.ws.WsOpCode;
import org.rand.aio.core.protocol.ws.handler.WsPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AioKit {

    private final static Logger logger= LoggerFactory.getLogger(AioKit.class);

    private final static ConcurrentHashMap<String, ReadWriteContext> CONTEXT=new ConcurrentHashMap<>();

    private final static ConcurrentHashMap<String, List<String>> GROUP=new ConcurrentHashMap<>();


    public static void close(ReadWriteContext aioServerContext){
        try {
            logger.info("{}链接关闭了", aioServerContext.getAsynchronousSocketChannel().getRemoteAddress());
            aioServerContext.getAsynchronousSocketChannel().shutdownInput();
            aioServerContext.getAsynchronousSocketChannel().shutdownOutput();
            aioServerContext.getAsynchronousSocketChannel().close();
        } catch (IOException e) {
            logger.error("关闭异常",e);
        } finally {
            logger.info("关闭链接");
        }
    }

    public static boolean addContext(ReadWriteContext context){
        String contextId = context.getAioServerSession().getId();
        StringKit.hasText(contextId,"contextId不能为空");
        ReadWriteContext aioServerContext = CONTEXT.get(contextId);
        if(aioServerContext==null){
            CONTEXT.put(contextId,context);
            return true;
        }
        if(aioServerContext.equals(context)){
            logger.warn("请勿重复添加");
        }else{
            logger.error("同一个contextId存在多个不同的Context");
        }
        return false;
    }

    public static void delContext(String id){
        CONTEXT.remove(id);
    }

    public static void bindGroup(String groupId, String userId){
        List<String> userIds = GROUP.get(groupId);
        if(userIds==null){
            userIds=new LinkedList<>();
            GROUP.put(groupId,userIds);
        }
        if(!userIds.contains(userId)){
            userIds.add(userId);
        }
    }

    public static boolean unbindGroup(String groupId, String userId){
        List<String> groups = GROUP.get(groupId);
        if(groups==null){
           return false;
        }
        if(groups.contains(userId)){
          return groups.remove(userId);
        }
        return false;
    }

    public static void sendToGroup(String groupId,String msg){
        List<String> userIds = GROUP.get(groupId);
        for (String userId : userIds) {
            if(CONTEXT.containsKey(userId)){
                ReadWriteContext readWriteContext = CONTEXT.get(userId);
                if(readWriteContext!=null){
                    send(readWriteContext,msg.getBytes());
                }
            }
        }
    }


    public static ByteBuffer encoder(byte[] msg){
        int rep_length=msg==null?0:msg.length;
        byte header0=(byte)(0x8f&(WsOpCode.TEXT.getCode()|0xf0));
        ByteBuffer response_buf=getWsResponseByteBuffer(rep_length, header0);
        if(msg!=null){
            response_buf.put(msg);
        }
        response_buf.flip();
        return response_buf;
    }

    public static ByteBuffer getWsResponseByteBuffer(int rep_length, byte header0) {
        ByteBuffer response_buf;
        if (rep_length < 0x7e) {
            response_buf = ByteBuffer.allocate(rep_length + 2);
            response_buf.put(header0);
            response_buf.put((byte)rep_length);
        }else if(rep_length < (1<<0x10)-1){
            response_buf = ByteBuffer.allocate(rep_length + 4);
            response_buf.put(header0);
            response_buf.put((byte) 126);
            response_buf.put((byte) (rep_length >>> 8));
            response_buf.put((byte) (rep_length & 0xff));
        }else {
            response_buf = ByteBuffer.allocate(rep_length + 0xa);
            response_buf.put(header0);
            response_buf.put((byte) 127);
            response_buf.put(new byte[] { 0, 0, 0, 0 });
            response_buf.put((byte) (rep_length >>> 24));
            response_buf.put((byte) (rep_length >>> 16));
            response_buf.put((byte) (rep_length >>> 8));
            response_buf.put((byte) (rep_length & 0xff));
        }
        return response_buf;
    }

    public static void send(ReadWriteContext readWriteContext, String msg) {
        StringKit.hasText(msg,"不能写出空消息");
        send(readWriteContext,msg.getBytes());
    }

    public static void send(ReadWriteContext readWriteContext, byte[] msg) {
        WsPacket wsPacket = new WsPacket(readWriteContext.getAioServerSession().getId());
        wsPacket.setBytes(encoder(msg).array());
        readWriteContext.write(wsPacket);
    }
}

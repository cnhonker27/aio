package org.rand.aio.core.protocol.ws;

import org.rand.aio.core.annotation.mvc.AioAutowired;
import org.rand.aio.core.decoder.AioPacket;
import org.rand.aio.core.decoder.AioRequest;
import org.rand.aio.core.decoder.AioResponse;
import org.rand.aio.core.decoder.http.AioHttpMethod;
import org.rand.aio.core.decoder.http.AioHttpRequest;
import org.rand.aio.core.decoder.http.AioHttpVersion;
import org.rand.aio.core.protocol.AioServerProtocolProcessor;
import org.rand.aio.core.protocol.http.HttpProtocol;
import org.rand.aio.core.protocol.ws.annotation.AioWsProtocol;
import org.rand.aio.core.protocol.ws.handler.WsPacket;
import org.rand.aio.core.protocol.ws.processor.AbstractWsProcessor;
import org.rand.aio.core.protocol.ws.processor.WsProcessor;
import org.rand.aio.util.SHAKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

@AioWsProtocol
public class WsProtocol extends AioServerProtocolProcessor {

    private final static Logger logger= LoggerFactory.getLogger(HttpProtocol.class);

    private AioRequest aioRequest;

    private Boolean hasHandshake=false;

    @AioAutowired
    private WsProcessor wsProcessor;

    private byte[] body;

    private WsOpCode wsOpCode;

    public WsProtocol(){
    }

    public WsProtocol(WsProcessor wsProcessor){
        this.wsProcessor=wsProcessor;
    }
    /**
     *  0                   1                   2                   3
     *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-------+-+-------------+-------------------------------+
     * |F|R|R|R| opcode|M| Payload len |    Extended payload length    |
     * |I|S|S|S|  (4)  |A|     (7)     |             (16/64)           |
     * |N|V|V|V|       |S|             |   (if payload len==126/127)   |
     * | |1|2|3|       |K|             |                               |
     * +-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
     * |     Extended payload length continued, if payload len == 127  |
     * + - - - - - - - - - - - - - - - +-------------------------------+
     * |                               |Masking-key, if MASK set to 1  |
     * +-------------------------------+-------------------------------+
     * | Masking-key (continued)       |          Payload Data         |
     * +-------------------------------- - - - - - - - - - - - - - - - +
     * :                     Payload Data continued ...                :
     * + - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
     * |                     Payload Data continued ...                |
     * +---------------------------------------------------------------+
     **/
    @Override
    public AioRequest decoder(ByteBuffer byteBuffer) {
        logger.info("ws:"+getReadWriteContext());
        // ???????????????????????????
        if(!this.hasHandshake){
            return this.aioRequest= decoderRequest(byteBuffer);
        }else{
            // ??????????????????????????????????????????1
            // ?????????????????????
            byteBuffer.flip();
            // ???????????????
            // ???????????????8???
            byte first = byteBuffer.get();
            // ??????8?????????:first&10000000,fin=1
            // 10000000=128
            // 10000000=0x80
            // ???????????????1????????????????????????
            boolean fin = (first & 0x80) >>7> 0;
            // ??????567?????????:first&1110000,rsv=0
            // 1110000=112
            // 1110000=0x70
            // ??????5?????????:first&10000>>4 ??????5?????????:first&100000>>5 ??????5?????????:first&1000000>>6
            // ??????rsv1=0 rsv2=0 rsv3=0
            int rsv = (first & 0x70) >>> 4;
            // ??????1234?????????:first&1111,opcode=1???2???8???9???10 ?????? text???binary???close???ping???pong
            // 1111=15
            // 1111=0x0F
            byte opCodeByte = (byte) (first & 0x0F);
            this.wsOpCode=WsOpCode.formatValue(opCodeByte);
            // ???????????????
            byte second = byteBuffer.get();
            boolean hasMask = (second & 0xFF) >> 7 == 1;
            if (!hasMask) { //???9??????mask,?????????1
                throw new IllegalArgumentException("websocket client data must be masked");
            }
            // ?????? Payload length ???7???:1111111
            int payloadLength = second & 0x7F;
            // ???????????????http://www.voidcn.com/article/p-maatzgyu-c.html
            // ??????1?????????9-15??? (??????9???15?????????)?????????????????????????????????????????????????????????125???????????????????????????????????? 126?????????????????? 2??????????????? 127?????????????????? 3???
            // ??????2????????????????????? 16 ???????????????????????????????????????????????????
            // ??????3????????????????????? 64 ???????????????????????????????????????????????????
            // 16 64??? ???2????????????8?????????
            if(payloadLength==126){
                int ret = (byteBuffer.get() & 0xff) << 8;
                ret |= byteBuffer.get() & 0xff;
                payloadLength=ret;
            }
            if(payloadLength==127){
                payloadLength = (int) byteBuffer.getLong();
            }
            if(payloadLength!=0){
                byte[] mask_key = new byte[4];
                // ?????????
                byteBuffer.get(mask_key);
                byte[] mask_payload_data = new byte[payloadLength];
                // payload?????????
                byteBuffer.get(mask_payload_data);
                // ??????payload??????
                // ??????????????????i????????????i%4???i???4??????????????????????????????????????????ascii
                for (int i = 0; i < mask_payload_data.length; i++) {
                    mask_payload_data[i]=(byte)(mask_payload_data[i]^mask_key[i%4]);
                }
                if(logger.isDebugEnabled()){
                    StringBuilder tmp= new StringBuilder();
                    for (byte b : mask_key) {
                        tmp.append(b).append(",");
                    }
                    logger.debug("mask="+tmp);
                }
                this.body=mask_payload_data;
            }else{
                this.body=null;
            }
        }
        return this.aioRequest=new AioHttpRequest(AioHttpMethod.GET,AioHttpVersion.HTTP_1_1,"/index",null,null);
    }

    @Override
    public AioResponse handler(AioRequest aioRequest) {
        if(wsProcessor==null){
            throw new RuntimeException("?????????AbstractWsProcessor");
        }
        ((AbstractWsProcessor)wsProcessor).setReadWriteContext(getReadWriteContext());
        try {
            if(this.hasHandshake){
                WsPacket wsPacket = new WsPacket(getReadWriteContext().getAioServerSession().getId());
                wsPacket.setBytes(this.body);
                switch (this.wsOpCode) {
                    case TEXT:
                        wsProcessor.onReceiver(wsPacket, getReadWriteContext());
                        break;
                    case PING:
                        this.wsProcessor.onPing();
                        break;
                    case PONG:
                        this.wsProcessor.onPong();
                        break;
                    case CLOSE:
                        this.wsProcessor.onClose();
                        break;
                    case BINARY:
                        this.wsProcessor.onBinary();
                        break;
                }

            }else{
                if (!this.wsProcessor.onConnect(aioRequest)) {
                    getReadWriteContext().close();
                }else{
                    this.body=wsProcessor.getBytes();
                }
            }
        } catch (Exception e) {
            wsProcessor.onError(e);
        }
        return null;
    }

    @Override
    public AioPacket encoder(AioResponse aioResponse) {
        WsPacket wsPacket = new WsPacket(getReadWriteContext().getAioServerSession().getId());
        if(this.wsOpCode==WsOpCode.CLOSE){
            return wsPacket;
        }
        if(this.hasHandshake){
            int rep_length=this.body==null?0:this.body.length;
            // 241|1 &129
            // 128 64 32 16 8 4 2 1
            //  1  0  0  0  1 1 1 1
            // 11110000=0xf0
            // 00000001=1
            // 11110001=241
            // 01111
            byte header0=(byte)(0x8f&(this.wsOpCode.getCode()|0xf0));
            ByteBuffer response_buf;
            if (rep_length < 126) {
                response_buf = ByteBuffer.allocate(rep_length + 2);
                response_buf.put(header0);
                response_buf.put((byte)rep_length);
            }else if(rep_length < (1<<16)-1){
                response_buf = ByteBuffer.allocate(rep_length + 4);
                response_buf.put(header0); 
                response_buf.put((byte) 126);
                response_buf.put((byte) (rep_length >>> 8));
                response_buf.put((byte) (rep_length & 0xff));
            }else {
                response_buf = ByteBuffer.allocate(rep_length + 10);
                response_buf.put(header0);
                response_buf.put((byte) 127);
                response_buf.put(new byte[] { 0, 0, 0, 0 });
                response_buf.put((byte) (rep_length >>> 24));
                response_buf.put((byte) (rep_length >>> 16));
                response_buf.put((byte) (rep_length >>> 8));
                response_buf.put((byte) (rep_length & 0xff));
            }
            if(this.body!=null){
                response_buf.put(this.body);
            }
            response_buf.flip();
            wsPacket.setBytes(response_buf.array());
        }else{
            String header = this.aioRequest.getHeader("Sec-WebSocket-Key");
            this.hasHandshake=true;
            wsPacket.setBytes(SHAKit.getHandShakeResponse(header).getBytes());
        }
        return wsPacket;
    }

    @Override
    public boolean isSupportProtocol(ByteBuffer byteBuffer) {
        boolean flag=false;
        try {
            this.aioRequest=decoderRequest(byteBuffer);
            AioHttpVersion version = aioRequest.getVersion();
            switch (version) {
                case HTTP_1_0:
                case HTTP_1_1:
                    flag=true;
                    break;
            }
            String upgrade = aioRequest.getHeader("Upgrade");
            if(!"websocket".equals(upgrade)){
                flag=false;
                logger.warn("?????????ws??????");
            }
        } catch (Exception e) {
           logger.warn("?????????ws??????",e);
        }
        return flag;
    }

    @Override
    public String getProtocolName() {
        return null;
    }

    @Override
    public ByteBuffer getResponseByteBuffer() {
        return null;
    }

    @Override
    public AioRequest getAioRequest() {
        return  this.aioRequest;
    }

    public void setWsProcessor(AbstractWsProcessor wsProcessor) {
        this.wsProcessor = wsProcessor;
    }
}

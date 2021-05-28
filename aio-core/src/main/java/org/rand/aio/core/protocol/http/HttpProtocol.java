package org.rand.aio.core.protocol.http;

import org.rand.aio.core.decoder.AioPacket;
import org.rand.aio.core.decoder.AioRequest;
import org.rand.aio.core.decoder.AioResponse;
import org.rand.aio.core.decoder.http.AioHttpResponse;
import org.rand.aio.core.decoder.http.AioHttpVersion;
import org.rand.aio.core.protocol.AioServerProtocolProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class HttpProtocol extends AioServerProtocolProcessor {

    private final static Logger logger= LoggerFactory.getLogger(HttpProtocol.class);

    private HttpRenderHandler httpRenderHandler;

    public HttpProtocol(HttpRenderHandler httpRenderHandler){
        this.httpRenderHandler=httpRenderHandler;
    }
    // 回车换行
    private static final String CRLF = "\r\n";

    // 空格
    private static final String BLANK = " ";

    private ByteBuffer responseByteBuffer;

    private AioRequest aioRequest;

    @Override
    public AioRequest decoder(ByteBuffer byteBuffer) {
        return this.aioRequest=decoderRequest(byteBuffer);
    }

    @Override
    public AioResponse handler(AioRequest aioRequest) {
        AioHttpResponse aioHttpResponse = new AioHttpResponse();
        httpRenderHandler.invoke(aioRequest,aioHttpResponse,null);
        return aioHttpResponse;
    }
    /**
     *  HTTP/1.1 200 OK
     *  Connection: keep-alive
     *  Content-Language: zh-CN
     *  Content-Type: text/html;charset=UTF-8
     *  Date: Thu, 05 Nov 2020 16:36:42 GMT
     *  Server: AioServer
     *  Content-Length: 54
     *
     *  Content
     *
     *  -----------------------------------------------------
     *  Http 协议响应头构造方式 每一行结尾需要回车加换行 \r\n
     *  每个类型key value 之间的冒号有空格
     *  Content 与响应头间隔了一个回车加换行
     *  -----------------------------------------------------
     *  CR:carriage return 回车
     *  LF:line feed 换行
     */
    @Override
    public AioPacket encoder(AioResponse aioResponse) {
        // 读取renderBody
        byte[] renderBody = aioResponse.getRenderBody();
        int bodyLength=0;
        if(renderBody!=null){
            bodyLength=renderBody.length;
        }
        StringBuffer headerBuffer = new StringBuffer(256);
        headerBuffer.append("HTTP/1.1").append(BLANK).append(aioResponse.getStatus()).append(BLANK).append(CRLF);
        headerBuffer.append("Connection:").append(BLANK).append("keep-alive").append(CRLF);
        headerBuffer.append("Content-Language:").append(BLANK).append("zh-CN").append(CRLF);
        headerBuffer.append("Content-Type:").append(BLANK).append(aioResponse.getContentType()+";charset=UTF-8").append(CRLF);
        headerBuffer.append("Date:").append(BLANK).append(new Date()).append(CRLF);
        headerBuffer.append("Server:").append(BLANK).append("AioServer").append(CRLF);
        headerBuffer.append("Content-Length:").append(BLANK).append(bodyLength).append(CRLF);
        headerBuffer.append("Cache-control:max-age=100").append(CRLF);
        headerBuffer.append("SessionId:").append(BLANK).append(getReadWriteContext().getAioServerSession().getId()).append(CRLF);
        headerBuffer.append(CRLF);
        // 转化为字节数组
        String header = headerBuffer.toString();
        if(logger.isDebugEnabled()){
            logger.debug("响应头内容：\n{}",header);
        }
        byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);
        ByteBuffer responseBuffer = ByteBuffer.allocate(headerBytes.length + bodyLength);
        responseBuffer.put(headerBytes);
        if(bodyLength>0){
            // 将请求头和Content组合在一起
            responseBuffer.put(renderBody);
        }
        // 翻转
        responseBuffer.flip();
        HttpPacket http = new HttpPacket("http");
        http.setBytes(responseBuffer.array());
        return http;
    }

    @Override
    public boolean isSupportProtocol(ByteBuffer requestBuffer) {
        boolean flag=false;
        try {
            this.aioRequest = decoderRequest(requestBuffer);
            AioHttpVersion version = this.aioRequest.getVersion();
            if(version!=null){
                switch(version){
                    case HTTP_1_0:
                    case HTTP_1_1:
                        flag=true;
                        break;
                    default:
                        flag=false;
                        break;
                }
                String upgrade = this.aioRequest.getHeader("Upgrade");
                if("websocket".equals(upgrade)){
                    flag=false;
                    logger.warn("不支持http协议");
                }
            }else{
                logger.warn("不支持http协议");
            }
        } catch (Exception e) {
            logger.error("不支持http协议",e);
        }
        return flag;
    }

    @Override
    public String getProtocolName() {
        return null;
    }

    @Override
    public ByteBuffer getResponseByteBuffer() {
        return this.responseByteBuffer;
    }

    @Override
    public AioRequest getAioRequest() {
        return  this.aioRequest;
    }
}


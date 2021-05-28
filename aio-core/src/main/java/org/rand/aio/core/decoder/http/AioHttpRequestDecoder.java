package org.rand.aio.core.decoder.http;

import org.rand.aio.core.decoder.AioRequestDecoder;
import org.rand.aio.common.AscII;
import org.rand.aio.core.decoder.AioRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class AioHttpRequestDecoder implements AioRequestDecoder {

    private final static Logger logger= LoggerFactory.getLogger(AioHttpRequestDecoder.class);

    private  HashMap<String, String> headerMap;

    private HashMap<String, String> paramMap;

    private AioRequest request;

    @Override
    public AioRequest decoderRequest(ByteBuffer byteBuffer) {
     return readHttp(byteBuffer);
    }

    @Override
    public void encoderRequest(ByteBuffer byteBuffer) {

    }

    @Override
    public boolean support() {
        return false;
    }

    AioRequest readHttp(ByteBuffer byteBuffer){
        String line = readFistLine(byteBuffer);
        String[] lines = splitFistLine(line);
        String methodStr = lines[0];
        AioHttpMethod method = AioHttpMethod.getHttpMethod(methodStr);
        if(method==null){
            throw new RuntimeException(AioHttpResponseStatus.METHOD_NOT_ALLOWED+ "Unsupported HTTP Method "+line);
        }
        String uri=lines[1];
        String versionStr=lines[2];
        AioHttpVersion version;
        if (versionStr.equals("HTTP/1.1")) {
            version= AioHttpVersion.HTTP_1_1;
        }else if (versionStr.equals("HTTP/1.0")) {
            version= AioHttpVersion.HTTP_1_0;
        }else{
            throw new RuntimeException(AioHttpResponseStatus.BAD_REQUEST+"Unsupported HTTP Protocol "+line);
        }
        Map<String, String> paramMap = decoderParam(uri);
        Map<String, String> header = readHeader(byteBuffer);
        this.request=new AioHttpRequest(method,version,uri,new AioHttpHeader(header),paramMap);
        if(logger.isDebugEnabled()){
            logger.info("请求头：\n{}",header);
        }
        return request;
    }

    String  readFistLine(ByteBuffer byteBuffer){
       byteBuffer.flip();
       String line="";
        byte[] bytes = byteBuffer.array();
        while (byteBuffer.hasRemaining()){
           byte b = byteBuffer.get();
            int nowPosition=byteBuffer.position();
            if(nowPosition>2048){
                throw new RuntimeException("Request-URI Too Long An HTTP line is larger than 2048 bytes.");
            }
           if (b == AscII.LF) { //13 ,10  换行  回车符
               byte lastByte = byteBuffer.get(byteBuffer.position() - 2);
               int len = byteBuffer.position() - 1;
               if (lastByte == AscII.CR) {
                   len = byteBuffer.position() - 2;
               }
               line = new String(bytes, 0, len);
               break; //终止读取
           }
       }
       return line;
    }

    private  Map<String, String> decoderParam(String uri){
        Map<String, String> paramMap = new HashMap<>();
        int i = uri.indexOf("?")+1;
        String params=uri.substring(i);
        byte[] bytes = params.getBytes();
        int position=0;
        int end=bytes.length;
        while (position<end){
            int nowNamePositionStart=position;
            int nowNamePositionEnd=-1;
            int nowValuePositionStart=-1;
            int nowValuePositionEnd=-1;
            boolean paramCompleted=false;
            do{
                byte aByte = bytes[position];
                switch(aByte){
                    case AscII.DENGYUH :
                        nowNamePositionEnd=position;
                        nowValuePositionStart=++position;
                        break;
                    case AscII.HH:
                        nowValuePositionEnd=position;
                        position++;
                        paramCompleted=true;
                        break;
                    default:
                        position++;
                        break;
                }
            }while (!paramCompleted&&position<end);
            // 无参数时nowNamePositionEnd=-1 nowNamePositionStart必然大于-1
            if(nowNamePositionEnd<nowNamePositionStart){
                continue;
            }
            String key=new String(bytes,nowNamePositionStart,nowNamePositionEnd-nowNamePositionStart);
            if(nowValuePositionEnd==-1){
                nowValuePositionEnd=position;
            }
            String value=new String(bytes,nowValuePositionStart,nowValuePositionEnd-nowValuePositionStart);
            paramMap.put(key,value);
        }
        return paramMap;
    }

    private String[] splitFistLine(String line){
        int aStart;
        int aEnd;
        int bStart;
        int bEnd;
        int cStart;
        int cEnd;

        aStart = findNonWhitespace(line, 0);
        aEnd = findWhitespace(line, aStart);

        bStart = findNonWhitespace(line, aEnd);
        bEnd = findWhitespace(line, bStart);

        cStart = findNonWhitespace(line, bEnd);
        cEnd = findEndOfString(line);

        return new String[] {
                line.substring(aStart, aEnd),
                line.substring(bStart, bEnd),
                cStart < cEnd? line.substring(cStart, cEnd) : "" };
    }

    /**
     *  查找没有空白字符的位置
     * @param line
     * @param offset
     * @return
     */
    private static int findNonWhitespace(String line, int offset) {
        int result;
        for (result = offset; result < line.length(); result ++) {
            if (!Character.isWhitespace(line.charAt(result))) {
                break;
            }
        }
        return result;
    }
    /**
     *  查找有空白字符的位置
     * @param line
     * @param offset
     * @return
     */
    private static int findWhitespace(String line, int offset) {
        int result;
        for (result = offset; result < line.length(); result ++) {
            if (Character.isWhitespace(line.charAt(result))) {
                break;
            }
        }
        return result;
    }
    /**
     *  查找字符串结束的位置
     * @param line
     * @return
     */
    private static int findEndOfString(String line) {
        int result;
        for (result = line.length(); result > 0; result --) {
            if (!Character.isWhitespace(line.charAt(result - 1))) {
                break;
            }
        }
        return result;
    }

    Map<String, String> readHeader(ByteBuffer byteBuffer){
        byte[] bytes = byteBuffer.array();
        int len=byteBuffer.limit();
        int position=byteBuffer.position();
        Map<String, String> header = new HashMap<>();
        while (position<len){
            boolean paramCompleted=false;
            boolean parseHeader=true;
            int nowKeyStart=position;
            int nowKeyEnd=-1;
            int nowValueStart=-1;
            int nowValueEnd=-1;
            do{
                byte aByte=byteBuffer.get();
                position=byteBuffer.position();
                switch(aByte){
                    case AscII.MAOH:
                        if(parseHeader){
                            parseHeader=false;
                            nowKeyEnd=position-1;
                            nowValueStart=position+1;
                        }
                        break;
                    case AscII.LF:
                        aByte=byteBuffer.get(position-2);
                        nowValueEnd=position-1;
                        if(aByte==AscII.CR){
                            nowValueEnd=byteBuffer.position()-2;
                        }
                        paramCompleted=true;
                        break;
                    default:
                        position=byteBuffer.position();
                        break;
                }
            }while (!paramCompleted&&position<len);
            // 最后一次循环
            if(nowKeyEnd<nowKeyStart){
                continue;
            }
            String key=new String(bytes,nowKeyStart,nowKeyEnd-nowKeyStart);
            String value=new String(bytes,nowValueStart,nowValueEnd-nowValueStart);
            header.put(key,value);
        }

        return header;
    }
}

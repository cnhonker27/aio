package org.rand.aio.util;


import org.rand.aio.common.AscII;

import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class StringKit {

 //   private static Logger log=LoggerFactory.getLogger(MySysUtil.class);

    public static String getUUID(){
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        return uuid;
    }

    public static boolean isNotEmpty(String value){
        return !isEmpty(value);
    }

    public static boolean isEmpty(String value){
        if(value==""){
            return true;
        }else{
            if(value!=null){
                if(trim(value)=="")
                    return true;
            }else
                return true;
        }
        return false;
    }

    public static String trim(String value){
        if(value!=null){
            String temp="";
            byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            int position = buffer.position();
            int last=position;
            while (buffer.hasRemaining()){
                byte b = buffer.get();
                if(AscII.SPACE!=b){
                    int len=buffer.position()-last;
                    temp+=new String(bytes,last,len);
                    last=buffer.position();
                }else{
                    last=buffer.position();
                }
            }
            value=temp;
        }
        return value;
    }

    private static final byte[] SER_KEY="123456".getBytes(StandardCharsets.UTF_8);


    public static void json(){

    }


	/**
	 * 
	 * @param 字符串
	 * @param 插入的字符串
	 * @param 插入的下标
	 * @return
	 */
	public static String insert(String str,String insert,int indexof){
		byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
		int len=bytes.length;
		if(len==indexof){
			str+=insert;
			return str;
		}
		if(len<indexof){
			throw new IllegalArgumentException("下标异常,字符串长度为"+len+"，下标为"+indexof);
		}
		str="";
		ByteBuffer wrap = ByteBuffer.wrap(bytes);
		int position=0;
		while(wrap.hasRemaining()){
			 wrap.get();
			if(position==indexof){
				str+=insert;
			}
			str += new String(bytes, position, 1);
			position++;
		}
		return str;
	}
	
	public static void hasText(String text,String msg){
		if(isEmpty(text)){
			 throw new IllegalArgumentException(msg);
		}
	}

	public static String[] separator(String str,String separator){
	    if(str==null){
	        return new String[0];
        }
        List<String> result=new ArrayList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(str, separator);
        while (stringTokenizer.hasMoreElements()){
            result.add(stringTokenizer.nextToken());
        }
        return result.toArray(new String[0]);
    }
    
}

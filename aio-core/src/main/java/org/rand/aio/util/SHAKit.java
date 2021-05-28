package org.rand.aio.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SHAKit {

	static char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	final static String CHAR_SET="UTF-8";
	
	private static MessageDigest getDigest(String alg) {
		MessageDigest instance = null;
		try {
			instance = MessageDigest.getInstance(alg);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return instance;
	}

	public static byte[] getSHA(String alg, String serkey) {
		byte[] byt=null;
		if ("".equals(serkey)||null==serkey) {
			throw new RuntimeException("serKey不能为空");
		} else {
			MessageDigest digest = getDigest(alg);
			try {
				digest.update(serkey.getBytes(CHAR_SET));
			} catch (UnsupportedEncodingException e) {
				
			}
			 byt = digest.digest();
		}
		return byt;
	}

	public static byte[] getSHA1(String serkey) {
		return getSHA("SHA-1", serkey);
	}

	public static byte[] getSHA512(String serkey) {
		return getSHA("SHA-512", serkey);
	}

	public static String getBase64(byte[] bytes) {
		byte[] encode = Base64.getEncoder().encode(bytes);
		String string = new String(encode);
		return string;
	}

	public static void main(String[] args) {

		getHandShakeResponse("sN9cRrP/n9NdMgdcy2VJFQ==");
		byte[] sha12 = getSHA1("sN9cRrP/n9NdMgdcy2VJFQ==258EAFA5-E914-47DA-95CA-C5AB0DC85B11");
		String base64 = getBase64(sha12);
		System.out.println(base64);
	}

	public static String getHandShakeResponse(String receiveKey) {
		String keyOrigin = receiveKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
		String accept = getBase64(getSHA1(keyOrigin));
		String echoHeader = "";
		echoHeader += "HTTP/1.1 101 Switching Protocols\r\n";
		echoHeader += "Upgrade: websocket\r\n";
		echoHeader += "Connection: Upgrade\r\n";
		echoHeader += "Sec-WebSocket-Accept: " + accept + "\r\n";
		echoHeader += "\r\n";
		return echoHeader;
	}

}

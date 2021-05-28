package org.rand.aio.common;

public enum MsgCode {
	
	SUCCESS_CODE(1),// 成功
	FAILS_CODE(0)   // 失败
	; 
	private int code;
	
	MsgCode(int code){
		this.code=code;
	}
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
}

package org.rand.aio.common;

/**
 * 
 * @ClassName: MessageEnum
 * @Description: 返回参数枚举，0：失败，1：成功
 * @author rand
 * @date 2020年4月24日
 * @version V1.0
 */
public enum MsgEnum {
	/**
	 * 操作成功
	 */
	SUCCESS(MsgCode.SUCCESS_CODE,"操作成功"),
	
	/**
	 * 操作失败
	 */
	FAILS(MsgCode.FAILS_CODE,"操作失败"),
	/**
	 * 添加成功
	 */
	ADDSUCCESS(MsgCode.SUCCESS_CODE,"添加成功"),
	/**
	 * 添加失败
	 */
	ADDFAILS(MsgCode.FAILS_CODE,"添加失败")
	;
	
	private MsgCode code;
	
	private String msg;
	
	private MsgEnum(MsgCode code,String msg){
		this.code=code;
		this.msg=msg;
	}
	
	public MsgCode getCode() {
		return code;
	}
	
	public void setCode(MsgCode code) {
		this.code = code;
	}
	
	public String getMsg() {
		return msg;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}

package org.rand.aio.common;

import java.io.Serializable;
import java.util.Collection;

public class MsgResult<T> implements Serializable {

	private static final long serialVersionUID = 2004295419083449920L;

	private String  msg;

	private boolean success;

	private int code = 2000;

	private T data;
	
	// 状态码
	private int status = 200;
	
	public MsgResult() {
	}
	/**
	 * 该方法默认为返回结果
	 * @param data
	 */
	public MsgResult(T data) {
		buidmsg(data);
		this.data = data;
	}

	public MsgResult(MsgEnum msg, boolean success) {
		this(msg, success, null);
	}

	public MsgResult(MsgEnum msg, boolean success, T data) {
		this(msg, success, MsgCode.SUCCESS_CODE, data);
	}

	public MsgResult(MsgEnum msg, boolean success, MsgCode code, T data) {
		this(msg, success, code.getCode(), data, 2000);
	}

	public MsgResult(MsgEnum msg, boolean success, T data, int status) {
		this(msg, success, 2000, data, status);
	}

	public MsgResult(MsgEnum msg, boolean success, int code, T data, int status) {
		if (!success && code == 2000) {
			this.code = 5000;
		} else {
			this.code = code;
		}
		this.msg = msg.getMsg();
		this.success = success;
		this.data = data;
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getCode() {
		return code;
	}
	/**
	 * 自定义状态码
	 */
	public void setCode(int code) {
		this.code = code;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public int getStatus() {
		return status;
	}

	/**
	 * 状态码
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	public static <T> MsgResult<T> buid(T t) {
		MsgResult<T> messageEntity = new MsgResult<T>(t);
		return messageEntity;

	}

	private void buidmsg(T data) {
		if (data != null) {
			if (data instanceof Collection){
				if (((Collection<?>) data).size() > 0) {
					this.msg = MsgEnum.SUCCESS.getMsg();
					this.success = true;
				} else {
					this.msg = MsgEnum.FAILS.getMsg();
					this.success = false;
				}
			}else{
				this.msg = MsgEnum.SUCCESS.getMsg();
				this.success = true;
			}
		}  else {
			this.msg =MsgEnum.FAILS.getMsg();
			this.success = false;
		}
	}
	
	
}

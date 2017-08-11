package com.xiaotu.makeplays.mobile.common;

/**
 * 服务端返回给移动端的数据封装
 * 
 * @author xuchangjian
 * @param <T>
 */
public class ServerResponse<T> {

	private String message = "";

	private boolean success = true;

	private T data;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}

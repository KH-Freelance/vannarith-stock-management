package com.hfsolution.app.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppException extends RuntimeException {
	
	private static final long serialVersionUID = 5531221281437904797L;
	private String code=""; // Error Code to app
	private String msg=""; // Message write log for developer
	private String info="";
	private String custom = "N";
	private boolean notify = false;

	public AppException(String code) {
		this.code = code;
	}
	public AppException(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	public AppException(String code, String msg,String custom) {
		this.code = code;
		this.msg = msg;
		this.custom = custom;
	}

	public AppException(String code, String msg,boolean notify) {
		this.code = code;
		this.msg = msg;
		this.notify=notify;
	}

	public AppException(String code, String msg,String info,boolean notify) {
		this.code = code;
		this.msg = msg;
		this.info = info;
		this.notify=notify;
	}

	public AppException(String code, String msg,String info,String custom,boolean notify) {
		this.code = code;
		this.msg = msg;
		this.info = info;
		this.custom = custom;
		this.notify=notify;
	}
}

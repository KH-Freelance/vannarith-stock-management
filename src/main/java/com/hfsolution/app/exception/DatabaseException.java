package com.hfsolution.app.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatabaseException extends RuntimeException {
	
	private static final long serialVersionUID = 5531221281437904797L;
	private String code="";
	private String msg=""; 
	private String info="";

	public DatabaseException(String code) {
		this.code = code;
	}
	
	public DatabaseException(String code,String msg) {

		this.code = code;
		this.msg = msg;

	}

	public DatabaseException(String code,String msg,String info) {

		this.code = code;
		this.msg = msg;
		this.info = info;

	}
}

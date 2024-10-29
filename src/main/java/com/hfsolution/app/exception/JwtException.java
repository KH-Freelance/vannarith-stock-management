package com.hfsolution.app.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtException extends RuntimeException{
    private static final long serialVersionUID = 5531221281437904797L;
	private String code=""; // Error Code to app
	private String msg=""; // Message write log for developer

	public JwtException(String code) {
		this.code = code;
	}
	
	public JwtException(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}
}

package com.hfsolution.app.config.restclient;

import java.util.Base64;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class RestClientRequestInterceptor implements RequestInterceptor {
	
	@Override
	public void apply(RequestTemplate requestTemplate) {
		requestTemplate.header("Content-Type", "application/json");
    	requestTemplate.header("Cache-Control", "no-cache");
	}

	// @Override
	// public void apply(RequestTemplate requestTemplate) {

	// 	//normal
	// 	requestTemplate.header("Content-Type", "multipart/form-data");
    // 	requestTemplate.header("Cache-Control", "no-cache");

	// }
	
	protected static String base64Encode(final byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}
}
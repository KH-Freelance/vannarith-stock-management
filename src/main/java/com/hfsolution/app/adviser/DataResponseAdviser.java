package com.hfsolution.app.adviser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import static com.hfsolution.app.constant.AppConstant.*;
import com.hfsolution.app.util.AppLog;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class DataResponseAdviser implements ResponseBodyAdvice<Object>{

    @Autowired
    private HttpServletRequest httpServletRequest;
    //write response 
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter arg1, MediaType arg2,
            Class<? extends HttpMessageConverter<?>> arg3, ServerHttpRequest arg4, ServerHttpResponse arg5) {

        String reqId = httpServletRequest.getAttribute(REQ_ID) != null
        ? httpServletRequest.getAttribute(REQ_ID).toString()
        : "";
        String uri = httpServletRequest.getRequestURI();
        String action = httpServletRequest.getAttribute(ACTION) != null
        ? httpServletRequest.getAttribute(ACTION).toString()
        : "";
        String requestInfo = httpServletRequest.getAttribute(REQ_INFO) != null
        ? httpServletRequest.getAttribute(REQ_INFO).toString()
        : "";
        
            
        var appLog = new AppLog<>();
        appLog.setReqId(reqId);
        appLog.setUri(uri);
        appLog.setAction(action);
        appLog.setRequest(requestInfo);
        appLog.setResponse(body);
        appLog.writeToLog();
        return body;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }
}

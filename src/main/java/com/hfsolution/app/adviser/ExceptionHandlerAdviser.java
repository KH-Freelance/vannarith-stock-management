package com.hfsolution.app.adviser;


import static com.hfsolution.app.constant.AppResponseCode.*;
import static com.hfsolution.app.constant.AppConstant.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hfsolution.app.dto.ExceptionResponse;
import com.hfsolution.app.exception.AppException;
import com.hfsolution.app.exception.DatabaseException;
import com.hfsolution.app.exception.JwtException;
import com.hfsolution.app.util.AppTools;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;


@RestControllerAdvice
public class ExceptionHandlerAdviser extends ResponseEntityExceptionHandler {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @ExceptionHandler(value = {
            SignatureException.class,
            Exception.class,
            AppException.class,
            DatabaseException.class,
            AccessDeniedException.class,
            JwtException.class,
            BadCredentialsException.class,
            ExpiredJwtException.class
    })
    protected ResponseEntity<ExceptionResponse> handleException(RuntimeException ex, WebRequest request) throws JsonProcessingException {

        ExceptionResponse exceptionResponse = new ExceptionResponse();
        HttpStatus status;
        // Determine the status code based on the exception type
        if (ex instanceof AppException) {
            AppException appException = (AppException) ex;
            String code = appException.getCode();
            String msg = appException.getMsg();
            String devMsg = appException.getMsg();
            if(appException.getCustom().equals("N")){
                msg = AppTools.appGetMessage(code);
            }
            if(appException.isNotify()){
                //telegram alert
            }
            exceptionResponse.setCode(code);
            exceptionResponse.setMsg(msg);
            exceptionResponse.setDevMsg(devMsg);
            status = HttpStatus.OK;
            

        }else if (ex instanceof DatabaseException) {

            DatabaseException dbException = (DatabaseException) ex;
            String code = dbException.getCode();
            String msg = AppTools.appGetMessage(FAIL_CODE);
            String devMsg = dbException.getMsg();
            exceptionResponse.setCode(code);
            exceptionResponse.setMsg(msg);
            exceptionResponse.setDevMsg(devMsg);
            status = HttpStatus.OK;

        } else if (ex instanceof BadCredentialsException) {
            exceptionResponse.setCode(UNAUTH);
            exceptionResponse.setMsg(AppTools.appGetMessage(UNAUTH));
            status = HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof AccessDeniedException) {
            exceptionResponse.setCode(FORBID);
            exceptionResponse.setMsg(AppTools.appGetMessage(FORBID));
            status = HttpStatus.FORBIDDEN;
        } 
        else if (ex instanceof SignatureException) {
            exceptionResponse.setCode(FORBID);
            exceptionResponse.setMsg(ex.getMessage());
            status = HttpStatus.FORBIDDEN;
        } 
        else if (ex instanceof ExpiredJwtException) {
            exceptionResponse.setCode(FORBID);
            exceptionResponse.setMsg(ex.getMessage());
            status = HttpStatus.FORBIDDEN;
        }
        else {
            exceptionResponse.setCode(INTERNAL_SERVER_ERROR);
            exceptionResponse.setMsg(ex.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }


        return ResponseEntity.status(status).body(exceptionResponse);
    }
   
    //  @ExceptionHandler(MethodArgumentNotValidException.class)
    // public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    //     Map<String, String> errors = new HashMap<>();
    //     ex.getBindingResult().getFieldErrors().forEach(error -> 
    //         errors.put(error.getField(), error.getDefaultMessage())
    //     );
    //     return ResponseEntity.badRequest().body(errors);
    // }

   
}

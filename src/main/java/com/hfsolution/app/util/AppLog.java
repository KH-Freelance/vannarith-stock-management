package com.hfsolution.app.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.gson.Gson;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(Include.NON_NULL)
public class AppLog<T> {

    @JsonIgnore
    private static Logger logger = LogManager.getLogger("api.log");
    
    // @JsonIgnore
    // private static Logger sysLogger = LogManager.getLogger("error.log");

 
    String reqId;
	String uri;
	String action;
	String info;
    T request;
	T response;



    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public void writeToLog() {
        CompletableFuture.runAsync(() -> logger.info(this));
    }
    
    // public void writeErrorLog() {
    //     CompletableFuture.runAsync(() -> sysLogger.error(this));
    // }
}

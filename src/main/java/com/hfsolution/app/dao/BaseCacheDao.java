package com.hfsolution.app.dao;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;

import com.hfsolution.app.dto.BaseCache;
// import com.hfsolution.app.util.AppLog;


public abstract class BaseCacheDao extends BaseCache{

 private static final long serialVersionUID = -6325900493488845855L;
 
 @Autowired
 protected RedisTemplate<String, String> writeCacheDataRedisTemplate;

    // public BaseCacheDao(String hasKeyName, long timeToLive) {
    // this.hasKeyName = hasKeyName;
    //     this.timeToLiveAsSecond = timeToLive;
    // }

  @Async("jpaExecutor")
  public CompletableFuture<Void> saveCacheAsync(String id,String jsonCache) {
    try {
      writeCacheDataRedisTemplate.opsForHash().put(hasKeyName, id, jsonCache);
      if (timeToLiveAsSecond > 0)
        writeCacheDataRedisTemplate.expire(hasKeyName, timeToLiveAsSecond, TimeUnit.SECONDS);
    } catch (Exception | Error e) {
    //   String currentClassName = this.getClass().getSimpleName();
    //   String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    //   var apiLog = new AppLog<>();
    //   apiLog.setRegId(hasKeyName);
    //   apiLog.setAction(currentClassName + " => " +currentMethodName);
    //   apiLog.setError(e.getMessage());
    //   apiLog.writeSysLog();
    }   
    return CompletableFuture.completedFuture(null);
  }



  public void saveCache(String id,String jsonCache,long timeToLive) {
    try {
      writeCacheDataRedisTemplate.opsForHash().put(hasKeyName, id, jsonCache);
      if (timeToLive > 0)
        writeCacheDataRedisTemplate.expire(hasKeyName, timeToLive, TimeUnit.SECONDS);
    } catch (Exception | Error e) {
    //   String currentClassName = this.getClass().getSimpleName();
    //   String currentMethodName = new Object() {
    //   }.getClass().getEnclosingMethod().getName();
    //   var apiLog = new AppServiceLog<>();
    //   apiLog.setRegId(hasKeyName);
    //   apiLog.setAction(currentClassName + " => " +currentMethodName);
    //   apiLog.setError(e.getMessage());
    //   apiLog.writeSysLog();
    }
  }

  @Async("jpaExecutor")
  public CompletableFuture<Void> SaveCacheByIndividuel(String id,String jsonCache,long timeToLive){
   try {
    String haseKey = hasKeyName + "-" + id;
    writeCacheDataRedisTemplate.opsForHash().put(haseKey , id, jsonCache);
    writeCacheDataRedisTemplate.expire(haseKey, timeToLive, TimeUnit.SECONDS);
   }catch (Exception | Error e) {
    //    String currentClassName = this.getClass().getSimpleName();
    //    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    //    var apiLog = new AppServiceLog<>();
    //    apiLog.setRegId(hasKeyName);
    //    apiLog.setAction(currentClassName + " => " +currentMethodName);
    //    apiLog.setError(e.getMessage());
    //    apiLog.writeSysLog();
   }
   return CompletableFuture.allOf();
  }
  
  @Async("jpaExecutor")
  public CompletableFuture<Void> saveCacheAsync(String id,String jsonCache,long timeToLive) {
    
    try {
     
      writeCacheDataRedisTemplate.opsForHash().put(hasKeyName , id, jsonCache);
      if (timeToLive > 0)
        writeCacheDataRedisTemplate.expire(hasKeyName, timeToLive, TimeUnit.SECONDS);
    
      } catch (Exception | Error e) {
    //   String currentClassName = this.getClass().getSimpleName();
    //   String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    //   var apiLog = new AppServiceLog<>();
    //   apiLog.setRegId(hasKeyName);
    //   apiLog.setAction(currentClassName + " => " +currentMethodName);
    //   apiLog.setError(e.getMessage());
    //   apiLog.writeSysLog();
    }
    return CompletableFuture.completedFuture(null);
  }

 public String getCacheByIdIndividuel(String id) {
  try {
      String haseKey = hasKeyName + "-" + id;
      var result = writeCacheDataRedisTemplate.opsForHash().get(haseKey, id);
      if(result != null){
       return result.toString();
      }      
     } catch (Exception | Error e) {
    //   String currentClassName = this.getClass().getSimpleName();
    //   String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    //   var apiLog = new AppServiceLog<>();
    //   apiLog.setRegId(hasKeyName);
    //   apiLog.setAction(currentClassName + " => " +currentMethodName);
    //   apiLog.setError(e.getMessage());
    //   apiLog.writeSysLog();

     }
      return null;
 }
  
  public String getCacheById(String id) {
    try {
     
      var result = writeCacheDataRedisTemplate.opsForHash().get(hasKeyName, id);
      if(result != null){
        return result.toString();
      }      
    } catch (Exception | Error e) {
    //   String currentClassName = this.getClass().getSimpleName();
    //   String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    //   var apiLog = new AppServiceLog<>();
    //   apiLog.setRegId(hasKeyName);
    //   apiLog.setAction(currentClassName + " => " +currentMethodName);
    //   apiLog.setError(e.getMessage());
    //   apiLog.writeSysLog();

    }
    return null;
  }

  public boolean deleteCacheByKey(String cacheKey) {
    try {

      boolean deleted = writeCacheDataRedisTemplate.delete(cacheKey);
      return deleted;

    } catch (Exception | Error e) {
    //   String currentClassName = this.getClass().getSimpleName();
    //   String currentMethodName = new Object() {
    //   }.getClass().getEnclosingMethod().getName();
    //   var apiLog = new AppServiceLog<>();
    //   apiLog.setRegId(hasKeyName);
    //   apiLog.setAction(currentClassName + " => " +currentMethodName);
    //   apiLog.setError(e.getMessage());
    //   apiLog.writeSysLog();
    }
    return false;
  }

}

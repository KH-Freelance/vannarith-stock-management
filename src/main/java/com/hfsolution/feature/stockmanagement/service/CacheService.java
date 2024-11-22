package com.hfsolution.feature.stockmanagement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hfsolution.app.dao.BaseCacheDao;

@Service
public class CacheService extends BaseCacheDao{
     
    public CacheService(@Value("${cache.stock.name}") String hasKeyName,@Value("${cache.stock.ttl}") long timeToLive) {
        super(hasKeyName, timeToLive);
        }
  
//    public String sampleGetKey(){
  
//     // String getCache = this.getCacheByIdIndividuel(//key);.....
  
    
//    }
  
//   
}

package com.hfsolution.feature.stockmanagement.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.hfsolution.app.dao.BaseCacheDao;
import com.hfsolution.app.dto.BaseEntityResponseDto;
import com.hfsolution.app.exception.DatabaseException;
import com.hfsolution.app.util.JsonUtil;
import com.hfsolution.feature.stockmanagement.dto.cache.CustomerCacheDto;
import com.hfsolution.feature.stockmanagement.entity.Customer;

@Service
public class CustomerCacheDao extends BaseCacheDao{

  private @Autowired CustomerDao customerDao;

  public CustomerCacheDao(@Value("${cache.customer.name}") String hasKeyName, @Value("${cache.customer.ttl}") long timeToLive){
    super(hasKeyName, timeToLive);
  }

  public Customer findCustomer(long customerId){
    Customer customer = new Customer();
    CustomerCacheDto customerCacheDto = new CustomerCacheDto();

    try{

      //NOT EXISTS KEY CALL LOAD CACHE AGAIN
      if(!this.getCacheByKeyOnly(hasKeyName)){
        customer = findCustomerFromDB(customerId);
        return customer;
      }
      
      String customerCache = this.getCacheById(String.valueOf(customerId));
      if(customerCache != null){
        customerCacheDto = JsonUtil.parseJson(customerCache, CustomerCacheDto.class);
        customer = customerCacheDto.getCustomer();
      }
    }catch(DatabaseException e){
      return null;

    }catch(Exception e){
      return null;
    }
    return customer;
  }

   private Customer findCustomerFromDB(long customerId){
    Customer customer = new Customer();
    try{

      BaseEntityResponseDto<Customer> entityLIst = customerDao.findAll();
      if(entityLIst.getEntityList() != null && !entityLIst.getEntityList().isEmpty()){
        //SAVE TO CACHE
        for (Customer cust : entityLIst.getEntityList()) {
          CustomerCacheDto customerCacheDto = new CustomerCacheDto();
          customerCacheDto.setId(String.valueOf(cust.getId()));
          customerCacheDto.setTimeToLiveAsSecond(this.timeToLiveAsSecond);
          customerCacheDto.setCustomer(cust);
          this.saveCacheAsync(String.valueOf(cust.getId()), JsonUtil.convertToJson(customerCacheDto),this.timeToLiveAsSecond);
          if(cust.getId() == customerId){
            customer = cust;
          }
        }
      }

    }catch(Exception e){
      return null;
    }

    return customer;
  }

}

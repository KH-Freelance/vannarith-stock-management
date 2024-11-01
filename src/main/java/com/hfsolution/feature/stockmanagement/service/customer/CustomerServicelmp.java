package com.hfsolution.feature.stockmanagement.service.customer;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;
import java.sql.Timestamp;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.hfsolution.app.dto.BaseEntityResponseDto;
import com.hfsolution.app.dto.PageRequestDto;
import com.hfsolution.app.dto.SearchRequestDTO;
import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.app.exception.AppException;
import com.hfsolution.app.exception.DatabaseException;
import com.hfsolution.app.services.SearchFilter;
import com.hfsolution.app.util.AppTools;
import com.hfsolution.feature.stockmanagement.dao.CustomerDao;
import com.hfsolution.feature.stockmanagement.dto.request.customer.CustomerRequest;
import com.hfsolution.feature.stockmanagement.dto.request.customer.CustomerUpdateRequest;
import com.hfsolution.feature.stockmanagement.entity.Customer;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import static com.hfsolution.app.constant.AppConstant.*;

@Service
@RequiredArgsConstructor
public class CustomerServicelmp implements CustomerService {

    
    private final CustomerDao CustomerDao;
    private final HttpServletRequest httpServletRequest;
    private final SearchFilter<Customer> searchFilter;

    @Override
    public Object searchCustomer(SearchRequestDTO request) {

        httpServletRequest.setAttribute(ACTION,"SEARCH Customer");
        SuccessResponse<Page<Customer>> response = new SuccessResponse<>();
        try {

            Specification<Customer> Customers = searchFilter.getSearchSpecification(request.getSearchRequest(), request.getGlobalOperator());
            Pageable pageable = new PageRequestDto().getPageable(request.getPageRequestDto());
            BaseEntityResponseDto<Customer> productResult = CustomerDao.search(Customers,pageable);
            if(!productResult.getStatus().equals(SUCCESS) || productResult.getPage()==null){
                String msg = AppTools.appGetMessage("015");
                throw new AppException("015",msg);
            }
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setData(productResult.getPage());
            return response;

        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        }
        
    }

    @Override
    public Object addCustomer(CustomerRequest CustomerRequest) {

        httpServletRequest.setAttribute(ACTION,"ADD CUSTOMER");
        SuccessResponse<Customer> response = new SuccessResponse<>();
        try {

            BaseEntityResponseDto<Customer> CustomerResult = CustomerDao.findByCustomerName(CustomerRequest.getCustomerName());
            if(CustomerResult.getEntity()!=null){
                String msg = AppTools.appGetMessage("014");
                throw new AppException("014",msg);
            }

            Customer Customer = new Customer();
            Customer.setCustomerName(CustomerRequest.getCustomerName());
            Customer.setEmail(CustomerRequest.getEmail());
            Customer.setPhone(CustomerRequest.getPhone());
            Customer.setAddress(CustomerRequest.getAddress());
            Customer.setDiscount(CustomerRequest.getDiscount());
            Customer.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            Customer.setUpdatedDate(new Timestamp(System.currentTimeMillis()));

            CustomerDao.saveEntity(Customer);
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setMsg(AppTools.appGetMessage("012"));
            return response;

        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        }
    }

    @Override
    public Object deleteCustomerById(Long id) {

        httpServletRequest.setAttribute(ACTION,"DELETE CUSTOMER BY ID");
        SuccessResponse<Customer> response = new SuccessResponse<>();
        try {
    
            CustomerDao.deleteByCustomerID(id);
            String msg = AppTools.appGetMessage("011");
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setMsg(msg);
            return response;

        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        }

    }


    @Override
    public Object updateCustomer(Long id, CustomerUpdateRequest CustomerUpdateRequest) {

        httpServletRequest.setAttribute(ACTION,"UPDATE CUSTOMER BY ID");
        SuccessResponse<Customer> response = new SuccessResponse<>();
        try {

            BaseEntityResponseDto<Customer> CustomerResult = CustomerDao.findById(id);
            Customer Customer = CustomerResult.getEntity();
            Optional.ofNullable(CustomerUpdateRequest.getCustomerName()).ifPresent(Customer::setCustomerName);
            Optional.ofNullable(CustomerUpdateRequest.getEmail()).ifPresent(Customer::setEmail);
            Optional.ofNullable(CustomerUpdateRequest.getAddress()).ifPresent(Customer::setAddress);
            Optional.ofNullable(CustomerUpdateRequest.getPhone()).ifPresent(Customer::setPhone);
            Optional.ofNullable(CustomerUpdateRequest.getCredit()).ifPresent(Customer::setCredit);
            Optional.ofNullable(CustomerUpdateRequest.getDiscount()).ifPresent(Customer::setDiscount);
            Customer.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
            CustomerDao.saveEntity(Customer);
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setMsg(AppTools.appGetMessage("013"));
            return response;

        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        }

    }
   
    
}

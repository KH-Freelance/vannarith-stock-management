package com.hfsolution.feature.stockmanagement.service.customer;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.hfsolution.app.dto.BaseEntityResponseDto;
import com.hfsolution.app.dto.PageRequestDto;

import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.app.exception.AppException;
import com.hfsolution.app.exception.DatabaseException;
import com.hfsolution.app.services.CustomSpecification;

import com.hfsolution.app.util.AppTools;
import com.hfsolution.app.util.CSVHelper;
import com.hfsolution.feature.stockmanagement.dao.CustomerDao;
import com.hfsolution.feature.stockmanagement.dto.csvrepresentation.CustomerCsv;
import com.hfsolution.feature.stockmanagement.dto.request.customer.CustomerRequest;
import com.hfsolution.feature.stockmanagement.dto.request.customer.CustomerUpdateRequest;
import com.hfsolution.feature.stockmanagement.entity.Customer;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static com.hfsolution.app.constant.AppConstant.*;

@Service
@RequiredArgsConstructor
public class CustomerServicelmp implements CustomerService {

    
    private final CustomerDao customerDao;
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;
    private final String CSV_FILENAME= "customer";

    @Override
    public Object search(String q, int pageNo, int pageSize, Direction sort, String sortByColum) {

        httpServletRequest.setAttribute(ACTION,"SEARCH CUSTOMER");
        SuccessResponse<Page<Customer>> response = new SuccessResponse<>();
        try {

            Specification<Customer> customers = new CustomSpecification<>(q);
            PageRequestDto pageRequestDto = new PageRequestDto();
            pageRequestDto.setPageNo(pageNo);
            pageRequestDto.setPageSize(pageSize);
            pageRequestDto.setSort(sort);
            pageRequestDto.setSortByColumn(sortByColum);
            Pageable pageable = new PageRequestDto().getPageable(pageRequestDto);
            BaseEntityResponseDto<Customer> customerResult = customerDao.search(customers,pageable);
            if(!customerResult.getStatus().equals(SUCCESS) || customerResult.getPage()==null){
                String msg = AppTools.appGetMessage("015");
            
                throw new AppException("015",msg);
            }
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setData(customerResult.getPage());
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
    public void export(String q) {
        httpServletRequest.setAttribute(ACTION, "EXPORT CUSTOMER");
        try {
            CSVHelper<CustomerCsv> csvService = new CSVHelper<>(CustomerCsv.class,httpServletResponse);
            Specification<Customer> customers = new CustomSpecification<>(q);
            BaseEntityResponseDto<Customer> customerResult = customerDao.search(customers);
            List<CustomerCsv> customerCsvs = new ArrayList<>();
            customerResult.getEntityList().stream().forEach(customer->{
                CustomerCsv customerCsv = new CustomerCsv();
                BeanUtils.copyProperties(customer, customerCsv);
                customerCsvs.add(customerCsv);
            });
            csvService.export(customerCsvs, CSV_FILENAME+AppTools.getCurrentDateWithFormatString("YYYY-MM-dd-HH-mm-ss")+".csv");

        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw new AppException("015",e.getMessage(),true); 
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        }
    }

    @Override
    public Object importData(MultipartFile file) {
        httpServletRequest.setAttribute(ACTION, "IMPORT CUSTOMER");
        try {
            CSVHelper<CustomerCsv> csvService = new CSVHelper<>(CustomerCsv.class);
            List<CustomerCsv> customerCsvList = csvService.parseCsv(file);
            List<Customer> customers = new ArrayList<>();
            customerCsvList.stream().forEach(customerCsv->{
                Customer customer= new Customer();
                BeanUtils.copyProperties(customerCsv, customer);
                customers.add(customer);
            });
            customerDao.saveEntities(customers);
            SuccessResponse<?> response = new SuccessResponse<>();
            response.setStatus(SUCCESS);
            response.setMsg(AppTools.appGetMessage("023"));
            response.setCode("023");
            return response;
        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw new AppException("022",e.getMessage(),true);
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        
        }
    
    }

    

    @Override
    public Object addCustomer(CustomerRequest CustomerRequest) {

        httpServletRequest.setAttribute(ACTION,"ADD CUSTOMER");
        SuccessResponse<Customer> response = new SuccessResponse<>();
        try {

            BaseEntityResponseDto<Customer> CustomerResult = customerDao.findByCustomerName(CustomerRequest.getCustomerName());
            if(CustomerResult.getEntity()!=null){
                String msg = AppTools.appGetMessage("019");
                throw new AppException("019",msg);
            }

            Customer Customer = new Customer();
            Customer.setId(customerDao.getCustomerId());
            Customer.setCustomerName(CustomerRequest.getCustomerName());
            Customer.setEmail(CustomerRequest.getEmail());
            Customer.setPhone(CustomerRequest.getPhone());
            Customer.setAddress(CustomerRequest.getAddress());
            Customer.setDiscount(CustomerRequest.getDiscount());
            Customer.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            Customer.setUpdatedDate(new Timestamp(System.currentTimeMillis()));

            customerDao.saveEntity(Customer);
            response.setStatus(SUCCESS);
            response.setCode("017");
            response.setMsg(AppTools.appGetMessage("017"));
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
    
            customerDao.deleteByCustomerID(id);
            String msg = AppTools.appGetMessage("016");
            response.setStatus(SUCCESS);
            response.setCode("016");
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

            BaseEntityResponseDto<Customer> CustomerResult = customerDao.findById(id);
            Customer Customer = CustomerResult.getEntity();
            Optional.ofNullable(CustomerUpdateRequest.getCustomerName()).ifPresent(Customer::setCustomerName);
            Optional.ofNullable(CustomerUpdateRequest.getEmail()).ifPresent(Customer::setEmail);
            Optional.ofNullable(CustomerUpdateRequest.getAddress()).ifPresent(Customer::setAddress);
            Optional.ofNullable(CustomerUpdateRequest.getPhone()).ifPresent(Customer::setPhone);
            Optional.ofNullable(CustomerUpdateRequest.getCredit()).ifPresent(Customer::setCredit);
            Optional.ofNullable(CustomerUpdateRequest.getDiscount()).ifPresent(Customer::setDiscount);
            Customer.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
            customerDao.saveEntity(Customer);
            response.setStatus(SUCCESS);
            response.setCode("018");
            response.setMsg(AppTools.appGetMessage("018"));
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

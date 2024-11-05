package com.hfsolution.feature.stockmanagement.service.customer;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hfsolution.app.dto.BaseEntityResponseDto;
import com.hfsolution.app.dto.PageRequestDto;
import com.hfsolution.app.dto.SearchRequestDTO;
import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.app.exception.AppException;
import com.hfsolution.app.exception.CsvException;
import com.hfsolution.app.exception.DatabaseException;
import com.hfsolution.app.services.CSVService;
import com.hfsolution.app.services.CustomSpecification;
import com.hfsolution.app.services.SearchFilter;
import com.hfsolution.app.util.AppTools;
import com.hfsolution.feature.stockmanagement.dao.CustomerDao;
import com.hfsolution.feature.stockmanagement.dto.request.customer.CustomerRequest;
import com.hfsolution.feature.stockmanagement.dto.request.customer.CustomerUpdateRequest;
import com.hfsolution.feature.stockmanagement.entity.Customer;
import com.hfsolution.feature.stockmanagement.entity.Stock;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import static com.hfsolution.app.constant.AppConstant.*;

@Service
@RequiredArgsConstructor
public class CustomerServicelmp implements CustomerService {

    
    private final CustomerDao customerDao;
    private final HttpServletRequest httpServletRequest;
    private final SearchFilter<Customer> searchFilter;
    private final String CSV_FILENAME= "customer";
    private final CSVService<Customer> csvService;
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
    public void export() {
        httpServletRequest.setAttribute(ACTION, "EXPORT CUSTOMER");
        try {

            csvService.export(customerDao.findAll().getEntityList(),Customer.class, CSV_FILENAME+AppTools.getCurrentDateWithFormatString("YYYY-MM-dd-HH-mm-ss")+".csv");

        }catch (DatabaseException e) {
            throw e;   
        }catch (CsvException e) {
            throw new AppException("027",e.getMessage(),true); 
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        }
    }

    @Override
    public Object importData(MultipartFile file) {
        httpServletRequest.setAttribute(ACTION, "IMPORT CUSTOMER");
        try {

            List<Customer> customerList = csvService.parseCsv(file, Customer.class);
            customerDao.saveEntities(customerList);
            SuccessResponse<?> response = new SuccessResponse<>();
            response.setStatus(SUCCESS);
            response.setMsg(AppTools.appGetMessage("030"));
            response.setCode(SUCCESS_CODE);
            return response;
        }catch (DatabaseException e) {
            throw e;   
        }catch (CsvException e) {
            throw new AppException("029",e.getMessage(),true); 
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        
        }
    
    }

    @Override
    public Object searchCustomer(SearchRequestDTO request) {

        httpServletRequest.setAttribute(ACTION,"SEARCH CUSTOMER");
        SuccessResponse<Page<Customer>> response = new SuccessResponse<>();
        try {

            Specification<Customer> Customers = searchFilter.getSearchSpecification(request.getSearchRequest(), request.getGlobalOperator());
            Pageable pageable = new PageRequestDto().getPageable(request.getPageRequestDto());
            BaseEntityResponseDto<Customer> productResult = customerDao.search(Customers,pageable);
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

            BaseEntityResponseDto<Customer> CustomerResult = customerDao.findByCustomerName(CustomerRequest.getCustomerName());
            if(CustomerResult.getEntity()!=null){
                String msg = AppTools.appGetMessage("014");
                throw new AppException("014",msg);
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
    
            customerDao.deleteByCustomerID(id);
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

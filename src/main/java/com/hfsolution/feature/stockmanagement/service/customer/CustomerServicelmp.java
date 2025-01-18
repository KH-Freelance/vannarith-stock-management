package com.hfsolution.feature.stockmanagement.service.customer;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import com.hfsolution.app.util.InfoGenerator;
import com.hfsolution.feature.stockmanagement.dao.CustomerDao;
import com.hfsolution.feature.stockmanagement.dao.PurchaseDao;
import com.hfsolution.feature.stockmanagement.dto.CsvRepresentation.CustomerCsv;
import com.hfsolution.feature.stockmanagement.dto.customer.CustomerDto;
import com.hfsolution.feature.stockmanagement.dto.request.customer.CustomerRequest;
import com.hfsolution.feature.stockmanagement.dto.request.customer.CustomerUpdateRequest;
import com.hfsolution.feature.stockmanagement.dto.stock.StockDto;
import com.hfsolution.feature.stockmanagement.entity.Customer;
import com.hfsolution.feature.stockmanagement.entity.Payment;
import com.hfsolution.feature.stockmanagement.entity.Product;
import com.hfsolution.feature.stockmanagement.entity.Purchase;
import com.hfsolution.feature.stockmanagement.enums.PaymentStatus;
import com.hfsolution.feature.stockmanagement.enums.PaymentType;
import com.hfsolution.feature.stockmanagement.util.customer.ExcelUtil;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

import static com.hfsolution.app.constant.AppConstant.*;

@Service
@RequiredArgsConstructor
public class CustomerServicelmp implements CustomerService {

    
    private final CustomerDao customerDao;
    private final PurchaseDao purchaseDao;
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;

    @Override
    @Transactional
    public Object search(String q, int pageNo, int pageSize, Direction sort, String sortByColum) {
    httpServletRequest.setAttribute(ACTION, "SEARCH CUSTOMER");
    SuccessResponse<Object> response = new SuccessResponse<>();
    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();
    try {

        Specification<Customer> customers = new CustomSpecification<>(this.removeCreditKey(q));
        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPageNo(pageNo);
        pageRequestDto.setPageSize(pageSize);
        pageRequestDto.setSort(sort);
        pageRequestDto.setSortByColumn(sortByColum);
        Pageable pageable = new PageRequestDto().getPageable(pageRequestDto);
        BaseEntityResponseDto<Customer> customerResult = customerDao.search(customers, pageable);
        if (!customerResult.getStatus().equals(SUCCESS) || customerResult.getPage() == null) {
            String msg = AppTools.appGetMessage("015");
            throw new AppException("015", msg);
        }

        // Calculate credit for each customer
        for (Customer customer : customerResult.getPage().getContent()) {
            BigDecimal credit = BigDecimal.ZERO;
            for (Purchase purchase : customer.getPurchases()) {
                if (purchase.getPaymentType().compareTo(PaymentType.CASH) == 0 || purchase.getPaymentStatus().compareTo(PaymentStatus.PAID) == 0) continue;
                BigDecimal totalAmountPaid = BigDecimal.ZERO;
                for (Payment payment : purchase.getPayments()) {
                    totalAmountPaid = totalAmountPaid.add(payment.getAmount());
                }
                credit = credit.add(purchase.getTotal().subtract(totalAmountPaid));
            }
            customer.setCredit(credit);
        }

        // Extract query parameters and apply filtering logic
        List<Customer> finalResult = customerResult.getPage().getContent();
        Map<String, String> resultParseQuery = this.parseQuery(q);
        for (Map.Entry<String, String> entry : resultParseQuery.entrySet()) {
            String value = entry.getValue();
            if (entry.getKey().equalsIgnoreCase("credit")) {
                if (value.matches("^\\[.*~.*\\]$")) {
                    String[] range = value.substring(1, value.length() - 1).split("~");
                    if (range.length == 2) {
                        BigDecimal minCredit = new BigDecimal(range[0]);
                        BigDecimal maxCredit = new BigDecimal(range[1]);
                        finalResult = finalResult.stream()
                                .filter(c -> c.getCredit() != null && c.getCredit().compareTo(minCredit) >= 0 && c.getCredit().compareTo(maxCredit) <= 0)
                                .collect(Collectors.toList());
                    }
                }else if (value.startsWith(">=")) {
                    BigDecimal minCredit = new BigDecimal(value.substring(2).trim());
                    finalResult = finalResult.stream()
                                .filter(c -> c.getCredit() != null && c.getCredit().compareTo(minCredit) >= 0)
                                .collect(Collectors.toList());
                } else if (value.startsWith("<=")) {
                    BigDecimal minCredit = new BigDecimal(value.substring(2).trim());
                    finalResult = finalResult.stream()
                                .filter(c -> c.getCredit() != null &&  c.getCredit().compareTo(minCredit) <= 0)
                                .collect(Collectors.toList());
                } else if (value.startsWith(">")) {
                    BigDecimal minCredit = new BigDecimal(value.substring(1).trim());
                    finalResult = finalResult.stream()
                                .filter(c -> c.getCredit() != null && c.getCredit().compareTo(minCredit) > 0 )
                                .collect(Collectors.toList());
                } else if (value.startsWith("<")) {
                    BigDecimal minCredit = new BigDecimal(value.substring(1).trim());
                    finalResult = finalResult.stream()
                                .filter(c -> c.getCredit() != null && c.getCredit().compareTo(minCredit) < 0)
                                .collect(Collectors.toList());
                }
            }
        }

        // Pagination on the filtered result
        int fromIndex = (pageNo - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, finalResult.size());
        if (fromIndex >= finalResult.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, finalResult.size());
        }

        List<Customer> paginatedResult = finalResult.subList(fromIndex, toIndex);
        Page<CustomerDto> customerDtoPage = new PageImpl<>(paginatedResult.stream()
                .map(customer -> {
                    CustomerDto customerDto = new CustomerDto();
                    BeanUtils.copyProperties(customer, customerDto);
                    return customerDto;
                })
                .collect(Collectors.toList()), pageable, finalResult.size());

        response.setStatus(SUCCESS);
        response.setCode(SUCCESS_CODE);
        response.setData(customerDtoPage);
        return response;

    } catch (DatabaseException e) {
        throw e;
    } catch (AppException e) {
        throw e;
    } catch (Exception e) {
        throw new AppException(FAIL_CODE, e.getMessage(), InfoGenerator.generateInfo(currentMethodName, startTime), true);
    }
}


    @Override
    public void export(String q) {
        httpServletRequest.setAttribute(ACTION, "EXPORT CUSTOMER");
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {
            Specification<Customer> customers = new CustomSpecification<>(q);
            BaseEntityResponseDto<Customer> customerResult = customerDao.search(customers);
            httpServletResponse.setContentType("application/octet-stream");
            // httpServletResponse.setContentType("text/csv");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=Customers.xlsx";
            httpServletResponse.setHeader(headerKey, headerValue);
            ExcelUtil customer = new ExcelUtil(customerResult.getEntityList());
            customer.exportDataToExcel(httpServletResponse);
        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw new AppException("015",e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true); 
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }
    }

    @Override
    public Object importData(MultipartFile file) {
        httpServletRequest.setAttribute(ACTION, "IMPORT CUSTOMER");
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {
            if(ExcelUtil.isValidExcelFile(file)){
                try {
                    List<Customer> customers = ExcelUtil.getCustomersDataFromExcel(file.getInputStream());
                    customerDao.saveEntities(customers);
                } catch (IOException e) {
                    throw new IllegalArgumentException("The file is not a valid excel file");
                }
            }
            SuccessResponse<?> response = new SuccessResponse<>();
            response.setStatus(SUCCESS);
            response.setMsg(AppTools.appGetMessage("023"));
            response.setCode("023");
            return response;
        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw new AppException("022",e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        
        }
    
    }

    

    @Override
    public Object addCustomer(CustomerRequest CustomerRequest) {

        httpServletRequest.setAttribute(ACTION,"ADD CUSTOMER");
        SuccessResponse<Customer> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
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
            //Customer.setDiscount(CustomerRequest.getDiscount());
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
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }
    }

    @Override
    public Object deleteCustomerById(Long id) {

        httpServletRequest.setAttribute(ACTION,"DELETE CUSTOMER BY ID");
        SuccessResponse<Customer> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {
    
            BaseEntityResponseDto<Purchase>  purchaseResult =  purchaseDao.findPurchaseByCustomerId(id);
            if(purchaseResult.getEntityList()!=null && purchaseResult.getEntityList().size() > 0){
                String msg = AppTools.appGetMessage("0160").replace("[purchaseCodes]", String.join(", ", purchaseResult.getEntityList().stream().limit(3).map(purchase->purchase.getPurchaseCode()).toArray(String[]::new)) + (purchaseResult.getEntityList().size() > 3 ? "..." : ""));
                throw new AppException("0160",msg, "Y");
            }
            
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
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }

    }


    @Override
    public Object updateCustomer(Long id, CustomerUpdateRequest CustomerUpdateRequest) {

        httpServletRequest.setAttribute(ACTION,"UPDATE CUSTOMER BY ID");
        SuccessResponse<Customer> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
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
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }

    }


    private Map<String, String> parseQuery(String queryString) {
        Map<String, String> map = new HashMap<>();
       if(queryString!=null&&!queryString.isEmpty()){
        String[] queries = queryString.split(",");
        for (String query : queries) {
            String[] keyValue = query.split("=", 2);
            if (keyValue.length == 2) {
                map.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
       }
        return map;
    }

    private Object parseValue(String value) {
        // Attempt to parse as Integer
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e1) {
            // Attempt to parse as BigDecimal
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException e2) {
                // Attempt to parse as Boolean
                if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                    return Boolean.parseBoolean(value);
                }
                // Attempt to parse as LocalDateTime
                try {
                    return AppTools.formatDateStringToTimestamp(value,"yyyy-MM-dd HH:mm:ss.SSS");
                } catch (Exception e3) {
                    // Handle strings with quotes
                    return value;
                }
            }
        }
    }
    
    private  String removeCreditKey(String input) {
        // Regex to match "credit=..." with different formats, including `>=`, `<=`, `<`, `>`, and `[...]`
        return input.replaceAll("credit=(\\[.*?\\]|[<>]=?.*?),?\\s*", "").trim();
    }
   
    
}

package com.hfsolution.feature.stockmanagement.service.customer;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hfsolution.app.dto.SearchRequestDTO;
import com.hfsolution.feature.stockmanagement.dto.request.customer.CustomerRequest;
import com.hfsolution.feature.stockmanagement.dto.request.customer.CustomerUpdateRequest;

@Service
public interface CustomerService {
    
    public Object searchCustomer(SearchRequestDTO request);  
    public Object search(String  q, int pageNo, int pageSize, Sort.Direction sort, String sortByColum);
    public void export();
    public void importData(MultipartFile file);
    public Object addCustomer(CustomerRequest customerRequest);
    public Object updateCustomer(Long id , CustomerUpdateRequest customerRequest);   
    public Object deleteCustomerById(Long id);
    
} 

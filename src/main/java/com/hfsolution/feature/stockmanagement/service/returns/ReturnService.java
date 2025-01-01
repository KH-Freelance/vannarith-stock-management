package com.hfsolution.feature.stockmanagement.service.returns;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.hfsolution.feature.stockmanagement.dto.request.returns.ReturnCashRequest;
import com.hfsolution.feature.stockmanagement.dto.request.returns.ReturnSaleRequest;

@Service
public interface ReturnService {

    
    public Object search(String  q, int pageNo, int pageSize, Sort.Direction sort, String sortByColum);
    public Object searchV2(String  q, int pageNo, int pageSize, Sort.Direction sort, String sortByColum);
    public Object searchDetail(Long id);
    public Object returnSale(ReturnSaleRequest returnRequest);
    public Object returnCash(ReturnCashRequest returnCashRequest);
    
} 

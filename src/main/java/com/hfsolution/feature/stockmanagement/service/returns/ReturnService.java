package com.hfsolution.feature.stockmanagement.service.returns;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.hfsolution.feature.stockmanagement.dto.request.returns.ReturnCashRequest;
import com.hfsolution.feature.stockmanagement.dto.request.returns.ReturnRequest;

@Service
public interface ReturnService {

    
    public Object search(String  q, int pageNo, int pageSize, Sort.Direction sort, String sortByColum);
    public Object searchDetail(long id);
    public Object returnPurchase(ReturnRequest returnRequest);
    public Object returnCash(ReturnCashRequest returnCashRequest);
    
} 

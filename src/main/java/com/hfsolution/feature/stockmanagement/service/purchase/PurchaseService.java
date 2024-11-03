package com.hfsolution.feature.stockmanagement.service.purchase;

import org.springframework.stereotype.Service;

import com.hfsolution.app.dto.SearchRequestDTO;
import com.hfsolution.feature.stockmanagement.dto.request.purchase.PurchaseRequest;
import com.hfsolution.feature.stockmanagement.dto.request.purchase.PurchaseUpdateRequest;

@Service
public interface PurchaseService {

    
    public Object searchPurchase(SearchRequestDTO request);  
    public Object addPurchase(PurchaseRequest purchaseRequest);
    public Object deletePurchaseById(Long id);
    //public Object updatePurchase(Long id , PurchaseUpdateRequest purchaseUpdateRequest);   
    
} 

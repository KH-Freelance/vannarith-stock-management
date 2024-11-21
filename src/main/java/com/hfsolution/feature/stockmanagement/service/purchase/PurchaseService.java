package com.hfsolution.feature.stockmanagement.service.purchase;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hfsolution.app.dto.BaseEntityResponseDto;

import com.hfsolution.feature.stockmanagement.dto.request.purchase.PayRequest;
import com.hfsolution.feature.stockmanagement.dto.request.purchase.PurchaseRequest;
import com.hfsolution.feature.stockmanagement.dto.request.purchase.PurchaseUpdateRequest;

@Service
public interface PurchaseService {

    
    public Object search(String  q, int pageNo, int pageSize, Sort.Direction sort, String sortByColum);
    public void export(String q);
    public Object importData(MultipartFile file);
    public Object addPurchase(PurchaseRequest purchaseRequest);
    public Object pay(Long id, PayRequest payRequest);
    public Object deletePurchaseById(Long id);
    
} 

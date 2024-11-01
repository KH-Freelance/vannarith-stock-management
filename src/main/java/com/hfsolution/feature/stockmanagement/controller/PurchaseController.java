package com.hfsolution.feature.stockmanagement.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hfsolution.app.dto.SearchRequestDTO;
import com.hfsolution.feature.stockmanagement.dto.request.product.ProductRequest;
import com.hfsolution.feature.stockmanagement.dto.request.product.ProductUpdateRequest;
import com.hfsolution.feature.stockmanagement.service.product.ProductService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {

    @Autowired
    private ProductService productService;


    @PostMapping("/search")
    private Object search(@RequestBody SearchRequestDTO request){
        return productService.search(request);
    }

    @PostMapping("/purchase")
    private Object purchase(@Valid @RequestBody ProductRequest productRequest){
        return productService.addProduct(productRequest);
    }

    @DeleteMapping("/delete/{id}")
    private Object deletePurchaseyId( @PathVariable long id){
        return productService.deleteProductById(id);
    }

    @PutMapping("/update/{id}")
    private Object updatePurchaseById(@PathVariable long id,@Valid @RequestBody ProductUpdateRequest productUpdateRequest){
        return productService.updateProductById(id,productUpdateRequest);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    
}

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
import com.hfsolution.feature.stockmanagement.dto.request.customer.CustomerRequest;
import com.hfsolution.feature.stockmanagement.dto.request.customer.CustomerUpdateRequest;
import com.hfsolution.feature.stockmanagement.service.customer.CustomerService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;


    @PostMapping("/search")
    private Object search(@RequestBody SearchRequestDTO request){
        return customerService.searchCustomer(request);
    }

    @PostMapping("/add")
    private Object addCustomer(@Valid @RequestBody CustomerRequest CustomerRequest){
        return customerService.addCustomer(CustomerRequest);
    }

    @DeleteMapping("/delete/{id}")
    private Object deleteCustomerById( @PathVariable long id){
        return customerService.deleteCustomerById(id);
    }

    @PutMapping("/update/{id}")
    private Object updateCustomerById(@PathVariable long id,@Valid @RequestBody CustomerUpdateRequest CustomerUpdateRequest){
        return customerService.updateCustomer(id,CustomerUpdateRequest);
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

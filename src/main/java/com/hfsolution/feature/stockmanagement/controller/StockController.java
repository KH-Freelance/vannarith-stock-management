package com.hfsolution.feature.stockmanagement.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hfsolution.app.dto.SearchRequestDTO;
import com.hfsolution.feature.stockmanagement.dto.request.StockRequest;
import com.hfsolution.feature.stockmanagement.service.stock.StockService;
import jakarta.validation.Valid;



@RestController
@RequestMapping("/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    @PostMapping("/search")
    private Object search(@RequestBody SearchRequestDTO request){
        return stockService.searchStock(request);
    }

    @PostMapping("/add")
    private Object addStock(@Valid @RequestBody StockRequest StockRequest){
        return stockService.addStock(StockRequest);
    }

    @DeleteMapping("/delete/{id}")
    private Object deleteStockById( @PathVariable long id){
        return stockService.deleteStockByProductId(id);
    }


    @PutMapping("/update/{id}")
    private Object updateStockById(@PathVariable long id,@Valid @RequestBody StockRequest StockRequest){
        return stockService.updateStockByProductId(id,StockRequest);
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

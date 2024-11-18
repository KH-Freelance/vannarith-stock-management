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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hfsolution.app.dto.ExceptionResponse;
import com.hfsolution.app.dto.SearchRequestDTO;
import com.hfsolution.feature.stockmanagement.dto.request.purchase.PayRequest;
import com.hfsolution.feature.stockmanagement.dto.request.purchase.PurchaseRequest;
import com.hfsolution.feature.stockmanagement.service.purchase.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;


    @PostMapping("/search")
    private Object search(@RequestBody SearchRequestDTO request){
        return purchaseService.searchPurchase(request);
    }

    @GetMapping("/search")
    @Operation(summary = "List purchases")
    public Object getUsersInfo( 
            @Parameter(description = "Query string to query resources. Supported query patterns are \"exact match(k=v)\", \"fuzzy match(k=~v)\", \"range(k=[min~max])\", \"list with union releationship(k={v1 v2 v3})\" and \"list with intersetion relationship(k=(v1 v2 v3))\". The value of range and list can be string(enclosed by \" or '), integer or time(in format \"2020-04-09 02:36:00\"). All of these query patterns should be put in the query string \"q=xxx\" and splitted by \",\". e.g. q=k1=v1,k2=~v2,k3=[min~max], Note: q is empty mean query all result.")
            @RequestParam(required = false) String  q,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "ASC") Sort.Direction sort,
            @RequestParam(defaultValue = "id") String sortByColum

        ) {
        return purchaseService.search(q,pageNo,pageSize,sort,sortByColum);
    }   

    @GetMapping("/export")
    private void exportData(
        @Parameter(description = "Query string to query resources. Supported query patterns are \"exact match(k=v)\", \"fuzzy match(k=~v)\", \"range(k=[min~max])\", \"list with union releationship(k={v1 v2 v3})\" and \"list with intersetion relationship(k=(v1 v2 v3))\". The value of range and list can be string(enclosed by \" or '), integer or time(in format \"2020-04-09 02:36:00\"). All of these query patterns should be put in the query string \"q=xxx\" and splitted by \",\". e.g. q=k1=v1,k2=~v2,k3=[min~max], Note: q is empty mean query all result.")
        @RequestParam(required = false) String q
    ){
        purchaseService.export(q);
    }

    @PostMapping(value = "/import", consumes = {"multipart/form-data"})
    private Object importData(@RequestPart("file")MultipartFile file){
        return purchaseService.importData(file);
    }

    

    @PostMapping("/add")
    private Object purchase(@Valid @RequestBody PurchaseRequest productRequest){
        return purchaseService.addPurchase(productRequest);
    }

    @PostMapping("/pay/{id}")
    private Object pay(@PathVariable long id,@Valid @RequestBody PayRequest payRequest){
        return purchaseService.pay(id,payRequest);
    }

    @DeleteMapping("/delete/{id}")
    private Object deletePurchaseyId( @PathVariable long id){
        return purchaseService.deletePurchaseById(id);
    }

    // @ExceptionHandler(MethodArgumentNotValidException.class)
    // public ResponseEntity<ExceptionResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
    //     ExceptionResponse exceptionResponse = new ExceptionResponse();
        
    //     ex.getBindingResult().getFieldErrors().forEach(error -> {
    //         exceptionResponse.setCode("001");
    //         exceptionResponse.setMsg(error.getDefaultMessage());
    //         exceptionResponse.setDevMsg(error.getDefaultMessage());
    //     });
    //     return ResponseEntity.ok().body(exceptionResponse);
    // }

    
}

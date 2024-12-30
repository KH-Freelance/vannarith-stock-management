package com.hfsolution.feature.stockmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hfsolution.feature.stockmanagement.dto.request.returns.ReturnCashRequest;
import com.hfsolution.feature.stockmanagement.dto.request.returns.ReturnSaleRequest;
import com.hfsolution.feature.stockmanagement.service.returns.ReturnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/return")
public class ReturnController {

    @Autowired
    private ReturnService returnService;
    
    @GetMapping("/return/search")
    @Operation(summary = "List return infomation")
    public Object getUsersInfo( 
            @Parameter(description = "Query string to query resources. Supported query patterns are \"exact match(k=v)\", \"fuzzy match(k=~v)\", \"range(k=[min~max])\", \"list with union releationship(k={v1 v2 v3})\" and \"list with intersetion relationship(k=(v1 v2 v3))\". The value of range and list can be string(enclosed by \" or '), integer or time(in format \"2020-04-09 02:36:00\"). All of these query patterns should be put in the query string \"q=xxx\" and splitted by \",\". e.g. q=k1=v1,k2=~v2,k3=[min~max], Note: q is empty mean query all result.")
            @RequestParam(required = false) String  q,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "ASC") Sort.Direction sort,
            @RequestParam(defaultValue = "id") String sortByColum

        ) {
        return returnService.search(q,pageNo,pageSize,sort,sortByColum);
    }

    @GetMapping("/search-detail/{id}")
    public Object searchDetail( 
        @PathVariable long id
        ) {
        return returnService.searchDetail(id);
    }  

    @PostMapping("/sale")
    private Object returnInvoice(@Valid @RequestBody ReturnSaleRequest returnRequest){
        return returnService.returnSale(returnRequest);
    }

    @PostMapping("/cash")
    private Object returnCash(@Valid @RequestBody ReturnCashRequest returnCashRequest){
        return returnService.returnCash(returnCashRequest);
    }
    

    // @PutMapping(value = "/v2/update/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    // private Object updateProductById2(@PathVariable long id,
    //     @ModelAttribute ProductUpdateRequest productUpdateRequest
    // ){
    //     return productService.updateProductById(id,productUpdateRequest,productUpdateRequest.getFile());
    // }

  

    
}

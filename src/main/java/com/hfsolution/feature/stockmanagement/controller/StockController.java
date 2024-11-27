package com.hfsolution.feature.stockmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.hfsolution.feature.stockmanagement.dto.request.stock.StockRequest;
import com.hfsolution.feature.stockmanagement.dto.request.stock.StockUpdateRequest;
import com.hfsolution.feature.stockmanagement.service.stock.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;



@RestController
@RequestMapping("/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping("/search")
    @Operation(summary = "List stocks")
    public Object getUsersInfo( 
            @Parameter(description = "Query string to query resources. Supported query patterns are \"exact match(k=v)\", \"fuzzy match(k=~v)\", \"range(k=[min~max])\", \"list with union releationship(k={v1 v2 v3})\" and \"list with intersetion relationship(k=(v1 v2 v3))\". The value of range and list can be string(enclosed by \" or '), integer or time(in format \"2020-04-09 02:36:00\"). All of these query patterns should be put in the query string \"q=xxx\" and splitted by \",\". e.g. q=k1=v1,k2=~v2,k3=[min~max], Note: q is empty mean query all result.")
            @RequestParam(required = false) String  q,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "ASC") Sort.Direction sort,
            @RequestParam(defaultValue = "id") String sortByColum

        ) {
        return ResponseEntity.ok(stockService.search(q,pageNo,pageSize,sort,sortByColum));
    }

    @GetMapping("/export")
    private void exportData(
        @Parameter(description = "Query string to query resources. Supported query patterns are \"exact match(k=v)\", \"fuzzy match(k=~v)\", \"range(k=[min~max])\", \"list with union releationship(k={v1 v2 v3})\" and \"list with intersetion relationship(k=(v1 v2 v3))\". The value of range and list can be string(enclosed by \" or '), integer or time(in format \"2020-04-09 02:36:00\"). All of these query patterns should be put in the query string \"q=xxx\" and splitted by \",\". e.g. q=k1=v1,k2=~v2,k3=[min~max], Note: q is empty mean query all result.")
        @RequestParam(required = false) String q
    ){
        stockService.export(q);
    }

    @PostMapping(value = "/import", consumes = {"multipart/form-data"})
    private Object importData(@RequestPart("file")MultipartFile file){
        return stockService.importData(file);
    }

    @PostMapping("/add")
    private Object addStock(@Valid @RequestBody StockRequest StockRequest){
        return stockService.addStock(StockRequest);
    }

    @PutMapping("/add-quantity/{id}")
    private Object addQuantity(@PathVariable long id,@Valid @RequestBody StockUpdateRequest stockUpdateRequest){
        return stockService.addQuantity(id,stockUpdateRequest);
    }

    @PutMapping("/remove-quantity/{id}")
    private Object removeQuantity(@PathVariable long id,@Valid @RequestBody StockUpdateRequest stockUpdateRequest){
        return stockService.removeQuantity(id,stockUpdateRequest);
    }

    @GetMapping("/{id}/stock-history/search")
    @Operation(summary = "List stocks history")
    public Object searchStockHistory( 
            // @Parameter(description = "Query string to query resources. Supported query patterns are \"exact match(k=v)\", \"fuzzy match(k=~v)\", \"range(k=[min~max])\", \"list with union releationship(k={v1 v2 v3})\" and \"list with intersetion relationship(k=(v1 v2 v3))\". The value of range and list can be string(enclosed by \" or '), integer or time(in format \"2020-04-09 02:36:00\"). All of these query patterns should be put in the query string \"q=xxx\" and splitted by \",\". e.g. q=k1=v1,k2=~v2,k3=[min~max], Note: q is empty mean query all result.")
            // @RequestParam(required = false) String  q,
            @PathVariable long id,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "ASC") Sort.Direction sort,
            @RequestParam(defaultValue = "id") String sortByColum

        ) {
        return stockService.searchHistory(id,pageNo,pageSize,sort,sortByColum);
    }
    

    @DeleteMapping("/delete/{id}")
    private Object deleteStockById( @PathVariable long id){
        return stockService.deleteStockById(id);
    }


    @PutMapping("/update/{id}")
    private Object updateStockById(@PathVariable long id,@Valid @RequestBody StockUpdateRequest stockUpdateRequest){
        return stockService.updateStock(id,stockUpdateRequest);
    }



}
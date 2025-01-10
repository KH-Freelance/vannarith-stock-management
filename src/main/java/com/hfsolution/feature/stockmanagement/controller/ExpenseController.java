package com.hfsolution.feature.stockmanagement.controller;


import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hfsolution.app.util.AppTools;
import com.hfsolution.feature.stockmanagement.dto.request.expense.ExpenseRequest;
import com.hfsolution.feature.stockmanagement.dto.request.expense.ExpenseUpdateRequest;
import com.hfsolution.feature.stockmanagement.service.expense.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import static com.hfsolution.app.constant.AppConstant.*;

@RestController
@RequestMapping("/expense")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;
    @Autowired
    private HttpServletRequest httpServletRequest;

    @GetMapping("/search")
    @Operation(summary = "List Expenses")
    public Object getSearch( 
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "ASC") Sort.Direction sort,
            @RequestParam(defaultValue = "id") String sortByColum

        ) {
        String q = username == null || username.isEmpty() ? "" : "username=~"+username;
        return expenseService.search(q,pageNo,pageSize,sort,sortByColum);
    }

    @PostMapping("/add")
    private Object addExpense(@Valid @RequestBody ExpenseRequest expenseRequest){
        httpServletRequest.setAttribute(REQ_INFO,"Detail-Req = "+AppTools.convertObjectToJson(expenseRequest));
        return expenseService.addExpense(expenseRequest);
    }

    @DeleteMapping("/delete/{id}")
    private Object deleteExpenseById( @PathVariable long id){
        httpServletRequest.setAttribute(REQ_INFO,"Expense ID = " +id);
        return expenseService.deleteExpenseById(id);
    }

    @PutMapping("/update/{id}")
    private Object updateExpenseById(@PathVariable long id,@Valid @RequestBody ExpenseUpdateRequest expenseUpdateRequest){
        httpServletRequest.setAttribute(REQ_INFO,"Expense ID = " +id+",  Detail-Req = "+AppTools.convertObjectToJson(expenseUpdateRequest));
        return expenseService.updateExpense(id,expenseUpdateRequest);
    }

    

    
}

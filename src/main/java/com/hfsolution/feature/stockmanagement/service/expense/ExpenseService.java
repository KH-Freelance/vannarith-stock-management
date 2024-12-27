package com.hfsolution.feature.stockmanagement.service.expense;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.hfsolution.feature.stockmanagement.dto.request.expense.ExpenseRequest;
import com.hfsolution.feature.stockmanagement.dto.request.expense.ExpenseUpdateRequest;

@Service
public interface ExpenseService {
    
    public Object search(String  q, int pageNo, int pageSize, Sort.Direction sort, String sortByColum);
    public Object addExpense(ExpenseRequest expoRequest);
    public Object updateExpense(Long id , ExpenseUpdateRequest expenseUpdateRequest);   
    public Object deleteExpenseById(Long id);
    
} 

package com.hfsolution.feature.stockmanagement.service.expense;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.hfsolution.app.dto.BaseEntityResponseDto;
import com.hfsolution.app.dto.PageRequestDto;

import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.app.exception.AppException;
import com.hfsolution.app.exception.DatabaseException;
import com.hfsolution.app.services.CustomSpecification;

import com.hfsolution.app.util.AppTools;
import com.hfsolution.app.util.CSVHelper;
import com.hfsolution.app.util.InfoGenerator;
import com.hfsolution.feature.stockmanagement.dao.ExpenseDao;
import com.hfsolution.feature.stockmanagement.dao.PurchaseDao;
import com.hfsolution.feature.stockmanagement.dto.request.expense.ExpenseDto;
import com.hfsolution.feature.stockmanagement.dto.request.expense.ExpenseRequest;
import com.hfsolution.feature.stockmanagement.dto.request.expense.ExpenseUpdateRequest;
import com.hfsolution.feature.stockmanagement.dto.stock.StockDto;
import com.hfsolution.feature.stockmanagement.entity.Expense;
import com.hfsolution.feature.stockmanagement.entity.Payment;
import com.hfsolution.feature.stockmanagement.entity.Product;
import com.hfsolution.feature.stockmanagement.entity.Purchase;
import com.hfsolution.feature.stockmanagement.enums.PaymentStatus;
import com.hfsolution.feature.stockmanagement.enums.PaymentType;
import com.hfsolution.feature.user.entity.User;
import com.hfsolution.feature.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import static com.hfsolution.app.constant.AppConstant.*;

@Service
@RequiredArgsConstructor
public class ExpenseServicelmp implements ExpenseService {

    
    private final ExpenseDao expenseDao;
    private final HttpServletRequest httpServletRequest;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public Object search(String q, int pageNo, int pageSize, Direction sort, String sortByColum) {

        httpServletRequest.setAttribute(ACTION,"SEARCH EXPENSE");
        SuccessResponse<Object> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {

            Specification<Expense> Expenses = new CustomSpecification<>(q);
            PageRequestDto pageRequestDto = new PageRequestDto();
            pageRequestDto.setPageNo(pageNo);
            pageRequestDto.setPageSize(pageSize);
            pageRequestDto.setSort(sort);
            pageRequestDto.setSortByColumn(sortByColum);
            Pageable pageable = new PageRequestDto().getPageable(pageRequestDto);
            BaseEntityResponseDto<Expense> ExpenseResult = expenseDao.search(Expenses,pageable);
            if(!ExpenseResult.getStatus().equals(SUCCESS) || ExpenseResult.getPage()==null){
                String msg = AppTools.appGetMessage("054");
            
                throw new AppException("054",msg);
            }

            Page<ExpenseDto> ExpenseDtoPage = ExpenseResult.getPage().map(Expense ->{
                ExpenseDto ExpenseDto = new ExpenseDto();
                BeanUtils.copyProperties(Expense, ExpenseDto);
                return ExpenseDto;
            });
            
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setData(ExpenseDtoPage);
            return response;

        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }
       
    } 


    @Override
    public Object addExpense(ExpenseRequest expenseRequest) {

        httpServletRequest.setAttribute(ACTION,"ADD EXPENSE");
        SuccessResponse<Expense> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {

            Long userId = (Long)httpServletRequest.getAttribute(USERID);
            User user = userRepository.findById(userId).get();

            Expense expense = new Expense();
            expense.setId(expenseDao.getExpenseId());
            expense.setAmount(expenseRequest.getAmount());
            expense.setRemark(expenseRequest.getRemark());
            expense.setUserId(userId);
            expense.setUsername(user.getFirstname()+" "+user.getLastname());
            expense.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            expense.setUpdatedDate(new Timestamp(System.currentTimeMillis()));

            expenseDao.saveEntity(expense);
            response.setStatus(SUCCESS);
            response.setCode("056");
            response.setMsg(AppTools.appGetMessage("056"));
            return response;

        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }
    }

    @Override
    public Object deleteExpenseById(Long id) {

        httpServletRequest.setAttribute(ACTION,"DELETE EXPENSE BY ID");
        SuccessResponse<Expense> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {
            
            expenseDao.deleteByExpenseID(id);
            String msg = AppTools.appGetMessage("055");
            response.setStatus(SUCCESS);
            response.setCode("055");
            response.setMsg(msg);
            return response;

        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }

    }


    @Override
    public Object updateExpense(Long id, ExpenseUpdateRequest ExpenseUpdateRequest) {

        httpServletRequest.setAttribute(ACTION,"UPDATE EXPENSE BY ID");
        SuccessResponse<Expense> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {

            BaseEntityResponseDto<Expense> ExpenseResult = expenseDao.findById(id);
            Expense Expense = ExpenseResult.getEntity();
            Optional.ofNullable(ExpenseUpdateRequest.getAmount()).ifPresent(Expense::setAmount);
            Optional.ofNullable(ExpenseUpdateRequest.getRemark()).ifPresent(Expense::setRemark);
          
            Expense.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
            Expense rExpense = expenseDao.saveEntity(Expense).getEntity();
            response.setStatus(SUCCESS);
            response.setData(rExpense);
            response.setCode("057");
            response.setMsg(AppTools.appGetMessage("057"));
            return response;

        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }

    }





   
    
}

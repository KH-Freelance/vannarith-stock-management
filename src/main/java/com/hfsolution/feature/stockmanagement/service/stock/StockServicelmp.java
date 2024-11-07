package com.hfsolution.feature.stockmanagement.service.stock;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hfsolution.app.dto.BaseEntityResponseDto;
import com.hfsolution.app.dto.PageRequestDto;
import com.hfsolution.app.dto.SearchRequestDTO;
import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.app.exception.AppException;
import com.hfsolution.app.exception.CsvException;
import com.hfsolution.app.exception.DatabaseException;
import com.hfsolution.app.services.CustomSpecification;
import com.hfsolution.app.services.SearchFilter;
import com.hfsolution.app.util.AppTools;
import com.hfsolution.app.util.CSVHelper;
import com.hfsolution.feature.stockmanagement.dao.ProductDao;
import com.hfsolution.feature.stockmanagement.dao.StockDao;
import com.hfsolution.feature.stockmanagement.dto.request.stock.StockRequest;
import com.hfsolution.feature.stockmanagement.dto.request.stock.StockUpdateRequest;
import com.hfsolution.feature.stockmanagement.entity.Product;
import com.hfsolution.feature.stockmanagement.entity.Stock;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static com.hfsolution.app.constant.AppConstant.*;

@Service
@RequiredArgsConstructor
public class StockServicelmp implements StockService {

    
    private final StockDao stockDao;
    private final ProductDao productDao;
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;
    private final SearchFilter<Stock> searchFilter;
    private final String CSV_FILENAME="stock";

    @Override
    public Object searchStock(SearchRequestDTO request) {

        httpServletRequest.setAttribute(ACTION,"SEARCH STOCK");
        SuccessResponse<Page<Stock>> response = new SuccessResponse<>();
        try {

            Specification<Stock> stocks = searchFilter.getSearchSpecification(request.getSearchRequest(), request.getGlobalOperator());
            Pageable pageable = new PageRequestDto().getPageable(request.getPageRequestDto());
            BaseEntityResponseDto<Stock> productResult = stockDao.searchStock(stocks,pageable);
            if(!productResult.getStatus().equals(SUCCESS) || productResult.getPage()==null){
                String msg = AppTools.appGetMessage("024");
                throw new AppException("024",msg);
            }
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setData(productResult.getPage());
            return response;

        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        }
        
    }

    @Override
    public Object addStock(StockRequest stockRequest) {

        httpServletRequest.setAttribute(ACTION,"IMPORT STOCK");
        SuccessResponse<Stock> response = new SuccessResponse<>();
        try {

            Stock stock ;

            //check product id
            BaseEntityResponseDto<Stock> stockResult = stockDao.findStockByProductID(stockRequest.getProductId());
            if(stockResult.getEntity()!=null){
                stock = stockResult.getEntity();
                stock.setQty(stock.getQty()+stockRequest.getQty());
                stock.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
            }else{
                stock = new Stock();
                BaseEntityResponseDto<Product> product = productDao.findByProductID(stockRequest.getProductId());
                stock.setId(stockDao.getStockId());
                stock.setProduct(product.getEntity());
                stock.setQty(stockRequest.getQty());
                stock.setCreatedDate(new Timestamp(System.currentTimeMillis()));
                stock.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
            }
            stockDao.saveEntity(stock);
            response.setStatus(SUCCESS);
            response.setCode("026");
            response.setMsg(AppTools.appGetMessage("026"));
            return response;

        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        }
    }

    @Override
    public Object deleteStockById(Long id) {

        httpServletRequest.setAttribute(ACTION,"DELETE STOCK BY ID");
        SuccessResponse<Stock> response = new SuccessResponse<>();
        try {
    
            stockDao.deleteStockByID(id);
            String msg = AppTools.appGetMessage("025");
            response.setStatus(SUCCESS);
            response.setCode("025");
            response.setMsg(msg);
            return response;

        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        }

    }


    @Override
    public Object updateStock(Long id, StockUpdateRequest stockUpdateRequest) {

        httpServletRequest.setAttribute(ACTION,"UPDATE STOCK BY ID");
        SuccessResponse<Stock> response = new SuccessResponse<>();
        try {

            BaseEntityResponseDto<Stock> stockResult = stockDao.findById(id);
            Stock stock = stockResult.getEntity();
            // Optional.ofNullable(stockUpdateRequest.getProductId()).ifPresent(stock::setPro);
            Optional.ofNullable(stockUpdateRequest.getQty()).ifPresent(stock::setQty);
            stock.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
            stockDao.saveEntity(stock);
            response.setStatus(SUCCESS);
            response.setCode("027");
            response.setMsg(AppTools.appGetMessage("027"));
            return response;

        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        }

    }

    @Override
    public void export(String q) {
        httpServletRequest.setAttribute(ACTION, "EXPORT STOCK");
        try {
            CSVHelper<Stock> csvService = new CSVHelper<>(Stock.class,httpServletResponse);
            Specification<Stock> stocks = new CustomSpecification<>(q);
            BaseEntityResponseDto<Stock> stockResult = stockDao.searchStock(stocks);
            csvService.export(stockResult.getEntityList(), CSV_FILENAME+AppTools.getCurrentDateWithFormatString("YYYY-MM-dd-HH-mm-ss")+".csv");

        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw new AppException("028",e.getMessage(),true); 
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        }
    }

    @Override
    public Object importData(MultipartFile file) {
        httpServletRequest.setAttribute(ACTION, "IMPORT STOCK");
        try {
            CSVHelper<Stock> csvService = new CSVHelper<>(Stock.class);
            List<Stock> stockList = csvService.parseCsv(file);
            stockDao.saveEntities(stockList);
            SuccessResponse<?> response = new SuccessResponse<>();
            response.setStatus(SUCCESS);
            response.setMsg(AppTools.appGetMessage("031"));
            response.setCode("031");
            return response;
        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw new AppException("030",e.getMessage(),true); 
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        
        }
    
    }

    @Override
    public Object search(String q, int pageNo, int pageSize, Direction sort, String sortByColum) {

        httpServletRequest.setAttribute(ACTION,"SEARCH STOCK");
        SuccessResponse<Page<Stock>> response = new SuccessResponse<>();
        try {

            Specification<Stock> stocks = new CustomSpecification<>(q);
            PageRequestDto pageRequestDto = new PageRequestDto();
            pageRequestDto.setPageNo(pageNo);
            pageRequestDto.setPageSize(pageSize);
            pageRequestDto.setSort(sort);
            pageRequestDto.setSortByColumn(sortByColum);
            Pageable pageable = new PageRequestDto().getPageable(pageRequestDto);
            BaseEntityResponseDto<Stock> stockResult = stockDao.searchStock(stocks,pageable);
            if(!stockResult.getStatus().equals(SUCCESS) || stockResult.getPage()==null){
                String msg = AppTools.appGetMessage("024");
                throw new AppException("024",msg);
            }
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setData(stockResult.getPage());
            return response;

        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        }
       
    } 
    
}

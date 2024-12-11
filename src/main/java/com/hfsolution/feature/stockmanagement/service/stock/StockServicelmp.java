package com.hfsolution.feature.stockmanagement.service.stock;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import com.hfsolution.feature.stockmanagement.dao.ProductDao;
import com.hfsolution.feature.stockmanagement.dao.StockDao;
import com.hfsolution.feature.stockmanagement.dao.StockHistoryDao;
import com.hfsolution.feature.stockmanagement.dto.CsvRepresentation.StockCsv;
import com.hfsolution.feature.stockmanagement.dto.product.ProductDto;
import com.hfsolution.feature.stockmanagement.dto.request.stock.StockRequest;
import com.hfsolution.feature.stockmanagement.dto.request.stock.StockUpdateRequest;
import com.hfsolution.feature.stockmanagement.dto.stock.StockDetailDto;
import com.hfsolution.feature.stockmanagement.dto.stock.StockDto;
import com.hfsolution.feature.stockmanagement.dto.stock.StockHistoryDto;
import com.hfsolution.feature.stockmanagement.dto.user.User;
import com.hfsolution.feature.stockmanagement.entity.Product;
import com.hfsolution.feature.stockmanagement.entity.Stock;
import com.hfsolution.feature.stockmanagement.entity.StockHistory;
import com.hfsolution.feature.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

import static com.hfsolution.app.constant.AppConstant.*;

@Service
@RequiredArgsConstructor
public class StockServicelmp implements StockService {

    
    private final StockDao stockDao;
    private final StockHistoryDao stockHistoryDao;
    private final UserRepository userRepository;
    private final ProductDao productDao;
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;
    private final String CSV_FILENAME="stock";

    @Override
    public Object addStock(StockRequest stockRequest) {

        httpServletRequest.setAttribute(ACTION,"IMPORT STOCK");
        SuccessResponse<Stock> response = new SuccessResponse<>();
        try {

            Stock stock ;

            //check product id
            BaseEntityResponseDto<Stock> stockResult = stockDao.findStockByProductID(stockRequest.getProductId());
            if(stockResult.getEntity()!=null){
                String msg = AppTools.appGetMessage("0240");
                throw new AppException("0240",msg);
                // stock = stockResult.getEntity();
                // stock.setQty(stock.getQty()+stockRequest.getQty());
                // stock.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
            }
            stock = new Stock();
            BaseEntityResponseDto<Product> product = productDao.findByProductID(stockRequest.getProductId());
            stock.setId(stockDao.getStockId());
            stock.setProduct(product.getEntity());
            stock.setQty(stockRequest.getQty());
            stock.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            stock.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
            stock = stockDao.saveEntity(stock).getEntity();
            // Need to be call async 
            Long userId = (Long)httpServletRequest.getAttribute(USERID);
            StockHistory stockHistory = new StockHistory();
            stockHistory.setId(stockHistoryDao.getStockHistoryId());
            // stockHistory.setStock(stock);
            stockHistory.setUser(userRepository.findById(userId).get());
            stockHistory.setRemark("New Creation Product");
            stockHistory.setQty(stockRequest.getQty());
            stockHistory.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            stockHistoryDao.saveEntityAsync(stockHistory);
            
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
            CSVHelper<StockCsv> csvService = new CSVHelper<>(StockCsv.class,httpServletResponse);
            Specification<Stock> stocks = new CustomSpecification<>(q);
            BaseEntityResponseDto<Stock> stockResult = stockDao.searchStock(stocks);
            List<StockCsv> stockCsvs = new ArrayList<>();
            stockResult.getEntityList().stream().forEach(stock->{
                StockCsv stockCsv = new StockCsv();
                BeanUtils.copyProperties(stock, stockCsv);
                stockCsv.setProductId(stock.getProduct().getId());
                stockCsv.setProductName(stock.getProduct().getProductName());
                stockCsvs.add(stockCsv);
            });
            csvService.export(stockCsvs, CSV_FILENAME+AppTools.getCurrentDateWithFormatString("YYYY-MM-dd-HH-mm-ss")+".csv");

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
            CSVHelper<StockCsv> csvService = new CSVHelper<>(StockCsv.class);
            List<StockCsv> stockCsvList = csvService.parseCsv(file);
            List<Stock> stockList = new ArrayList<>();
            stockCsvList.forEach(stockCsv->{
                Stock stock = new Stock();
                BeanUtils.copyProperties(stockCsv, stock);
                stock.setProduct(productDao.findById(stockCsv.getProductId()).getEntity());
                stockList.add(stock);
            });
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
    @Transactional
    public Object searchV2(String q, int pageNo, int pageSize, Direction sort, String sortByColum) {

        httpServletRequest.setAttribute(ACTION,"SEARCH STOCK");
        SuccessResponse<Object> response = new SuccessResponse<>();
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
            
            calculatePercentageOfQty(stockResult.getPage());
            Page<StockDto> userDtoPage = stockResult.getPage().map(stock ->{
                StockDto stockDto = new StockDto();
                BeanUtils.copyProperties(stock, stockDto);
                stockDto.setProduct(new StockDto.Product(stock.getProduct().getId(), stock.getProduct().getProductName()));
                return stockDto;
            });
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setData(userDtoPage);
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

            calculatePercentageOfQty(stockResult.getPage());
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

    @Override
    public Object searchHistory(String q, int pageNo, int pageSize, Direction sort, String sortByColum) {

        httpServletRequest.setAttribute(ACTION,"SEARCH STOCK HISTORY");
        SuccessResponse<Page<StockHistory>> response = new SuccessResponse<>();
        try {
            // q.concat(q+",stock.id="+stockId);
            Specification<StockHistory> stockHistories = new CustomSpecification<>(q);
            PageRequestDto pageRequestDto = new PageRequestDto();
            pageRequestDto.setPageNo(pageNo);
            pageRequestDto.setPageSize(pageSize);
            pageRequestDto.setSort(sort);
            pageRequestDto.setSortByColumn(sortByColum);
            Pageable pageable = new PageRequestDto().getPageable(pageRequestDto);
            // BaseEntityResponseDto<StockHistory> stockHistoryResult = stockHistoryDao.findAllByStockId(stockId,pageable);
            BaseEntityResponseDto<StockHistory> stockHistoryResult = stockHistoryDao.search(stockHistories,pageable);
            if(!stockHistoryResult.getStatus().equals(SUCCESS) || stockHistoryResult.getPage()==null){
                String msg = AppTools.appGetMessage("024");
                throw new AppException("024",msg);
            }

            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setData(stockHistoryResult.getPage());
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
    public Object addQuantity(Long id, StockUpdateRequest stockUpdateRequest) {

        httpServletRequest.setAttribute(ACTION,"ADD QUANTITY TO STOCK");
        SuccessResponse<Stock> response = new SuccessResponse<>();
        try {

            BaseEntityResponseDto<Stock> stockResult = stockDao.findById(id);
            if(!stockResult.getStatus().equals(SUCCESS) || stockResult.getEntity()==null){
                String msg = AppTools.appGetMessage("024");
                throw new AppException("024",msg);
            }
            Stock stock = stockResult.getEntity();
            if(stockUpdateRequest.getQty()>0){
                stock.setQty(stock.getQty()+stockUpdateRequest.getQty());
                stock.setUpdatedDate(new Timestamp(System.currentTimeMillis()));

                // Need to be call async 
                Long userId = (Long)httpServletRequest.getAttribute(USERID);
                StockHistory stockHistory = new StockHistory();
                stockHistory.setId(stockHistoryDao.getStockHistoryId());
                // stockHistory.setStock(stock);
                stockHistory.setUser(userRepository.findById(userId).get());
                stockHistory.setRemark(stockUpdateRequest.getRemark());
                stockHistory.setQty(stockUpdateRequest.getQty());
                stockHistory.setCreatedDate(new Timestamp(System.currentTimeMillis()));
                stockHistoryDao.saveEntityAsync(stockHistory);
            }
            stockDao.saveEntity(stock);
            response.setStatus(SUCCESS);
            response.setCode("0241");
            response.setMsg(AppTools.appGetMessage("0241"));
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
    public Object removeQuantity(Long id, StockUpdateRequest stockUpdateRequest) {
        httpServletRequest.setAttribute(ACTION,"REMOVE QUANTITY TO STOCK");
        SuccessResponse<Stock> response = new SuccessResponse<>();
        try {

            BaseEntityResponseDto<Stock> stockResult = stockDao.findById(id);
            if(!stockResult.getStatus().equals(SUCCESS) || stockResult.getEntity()==null){
                String msg = AppTools.appGetMessage("024");
                throw new AppException("024",msg);
            }
            Stock stock = stockResult.getEntity();
            // Check the remaining quantity of stock
            if(stock.getQty()-stockUpdateRequest.getQty()<0){
                String msg = AppTools.appGetMessage("0243").replace("[qty]", String.valueOf(stock.getQty()));
                throw new AppException("0243",msg,"Y");
            }

            if(stockUpdateRequest.getQty()>0){
                stock.setQty(stock.getQty()-stockUpdateRequest.getQty());
                stock.setUpdatedDate(new Timestamp(System.currentTimeMillis()));

                // Need to be call async 
                Long userId = (Long)httpServletRequest.getAttribute(USERID);
                StockHistory stockHistory = new StockHistory();
                stockHistory.setId(stockHistoryDao.getStockHistoryId());
                // stockHistory.setStock(stock);
                stockHistory.setUser(userRepository.findById(userId).get());
                stockHistory.setRemark(stockUpdateRequest.getRemark());
                stockHistory.setQty(stockUpdateRequest.getQty()*-1);
                stockHistory.setCreatedDate(new Timestamp(System.currentTimeMillis()));
                stockHistoryDao.saveEntityAsync(stockHistory);
            }
            stockDao.saveEntity(stock);
            response.setStatus(SUCCESS);
            response.setCode("0242");
            response.setMsg(AppTools.appGetMessage("0242"));
            return response;

        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        }
    }



    public void calculatePercentageOfQty(Page<Stock> stockPage) {
        Long totalQty = stockDao.getTotal();
        if (totalQty == null || totalQty == 0) {
            System.out.println("Total quantity is zero. Cannot calculate percentages.");
            return;
        }

        // Calculate and print the percentage for each stock entry
        for (Stock stock : stockPage) {
            double percentage = ((double) stock.getQty() / totalQty) * 100;
            BigDecimal bd = new BigDecimal(percentage).setScale(2, RoundingMode.HALF_UP);
            stock.setPercentage(bd.doubleValue());
        }
    }

    @Override
    @Transactional
    public Object searchDetail(long id) {
        httpServletRequest.setAttribute(ACTION,"SEARCH STOCK DETAIL BY ID");
        SuccessResponse<Object> response = new SuccessResponse<>();
        try {
            BaseEntityResponseDto<Stock> stockResult = stockDao.findById(id);
            if(!stockResult.getStatus().equals(SUCCESS) || stockResult.getEntity()==null){
                String msg = AppTools.appGetMessage("024");
                throw new AppException("024",msg);
            }

            //COPY StockDetailDto Property
            StockDetailDto stockDetailDto = new StockDetailDto();
            BeanUtils.copyProperties(stockResult.getEntity(), stockDetailDto);

            //COPY StockHistoryDto Property
            List<StockHistoryDto> stockHistoryDtos = new ArrayList<>();
            for (StockHistory stockHistory : stockResult.getEntity().getStockHistories()) {

                //COPY StockHistory Property
                StockHistoryDto stockHistoryDto = new StockHistoryDto();
                BeanUtils.copyProperties(stockHistory, stockHistoryDto);

                //COPY User Property
                User user = new User();
                BeanUtils.copyProperties(stockHistory.getUser(), user);
                stockHistoryDto.setUser(user);

                stockHistoryDtos.add(stockHistoryDto);
            }

            //COPY Product Property
            ProductDto productDto = new ProductDto();
            BeanUtils.copyProperties(stockResult.getEntity().getProduct(), productDto);

            stockDetailDto.setStockHistories(stockHistoryDtos);
            stockDetailDto.setProduct(productDto);

            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setData(stockDetailDto);
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

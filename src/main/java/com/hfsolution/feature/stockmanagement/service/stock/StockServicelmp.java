package com.hfsolution.feature.stockmanagement.service.stock;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
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
import com.hfsolution.app.util.CSVHelperV2;
import com.hfsolution.app.util.InfoGenerator;
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
import com.hfsolution.feature.stockmanagement.dto.stock.StockPercentageDto;
import com.hfsolution.feature.stockmanagement.dto.user.User;
import com.hfsolution.feature.stockmanagement.entity.Product;

import com.hfsolution.feature.stockmanagement.entity.Stock;
import com.hfsolution.feature.stockmanagement.entity.StockHistory;
import com.hfsolution.feature.stockmanagement.util.stock.ExcelUtil;
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
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {

            Stock stock ;

            //check product id
            BaseEntityResponseDto<Stock> stockResult = stockDao.findStockByProductIDAndBatchId(stockRequest.getProductId(),stockRequest.getBatchId());
            if(stockResult.getEntity()!=null){
                String msg = AppTools.appGetMessage("0240");
                throw new AppException("0240",msg);
                // stock = stockResult.getEntity();
                // stock.setQty(stock.getQty()+stockRequest.getQty());
                // stock.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
            }
            stock = new Stock();
            BaseEntityResponseDto<Product> product = productDao.findByProductID(stockRequest.getProductId());


            // Need to be call async 
            Long userId = (Long)httpServletRequest.getAttribute(USERID);
            com.hfsolution.feature.user.entity.User user = userRepository.findById(userId).get();
            StockHistory stockHistory = new StockHistory();
            stockHistory.setId(stockHistoryDao.getStockHistoryId());
            stockHistory.setFirstname(user.getFirstname());
            stockHistory.setLastname(user.getLastname());
            stockHistory.setRemark("New Creation Product");
            stockHistory.setQty(stockRequest.getQty());
            stockHistory.setCreatedDate(new Timestamp(System.currentTimeMillis()));

            stock.setId(stockDao.getStockId());
            stock.setBatchId(stockRequest.getBatchId());
            stock.setProductId(product.getEntity().getId());
            stock.setQty(stockRequest.getQty());
            stock.setImportPrice(stockRequest.getImportPrice());
            stock.setExpiryDate(Timestamp.valueOf(LocalDateTime.of(LocalDate.parse(stockRequest.getExpiryDate()), LocalTime.MIDNIGHT)));
            stock.setFactory(stockRequest.getFactory());
            // LocalDateTime factoryDate = LocalDateTime.parse(stockRequest.getFactoryDate(),DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a"));
            // stock.setFactoryDate(Timestamp.valueOf(factoryDate));
            stock.setFactoryDate(Timestamp.valueOf(LocalDateTime.of(LocalDate.parse(stockRequest.getFactoryDate()), LocalTime.MIDNIGHT)));
            stock.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            stock.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
            stock.addStockHistory(stockHistory);
            stock = stockDao.saveEntity(stock).getEntity();
            
            
            response.setStatus(SUCCESS);
            response.setCode("026");
            response.setMsg(AppTools.appGetMessage("026"));
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
    public Object deleteStockById(Long id) {

        httpServletRequest.setAttribute(ACTION,"DELETE STOCK BY ID");
        SuccessResponse<Stock> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
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
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }

    }


    @Override
    public Object updateStock(Long id, StockUpdateRequest stockUpdateRequest) {

        httpServletRequest.setAttribute(ACTION,"UPDATE STOCK BY ID");
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        SuccessResponse<Stock> response = new SuccessResponse<>();
        try {

            BaseEntityResponseDto<Stock> stockResult = stockDao.findById(id);
            Stock stock = stockResult.getEntity();
            // Optional.ofNullable(stockUpdateRequest.getProductId()).ifPresent(stock::setPro);
            Optional.ofNullable(stockUpdateRequest.getQty()).ifPresent(stock::setQty);
            Optional.ofNullable(stockUpdateRequest.getImportPrice()).ifPresent(stock::setImportPrice);
            Optional.ofNullable(stockUpdateRequest.getExpiryDate())
            .map(date -> Timestamp.valueOf(LocalDateTime.of(LocalDate.parse(date), LocalTime.MIDNIGHT)))
            .ifPresent(stock::setExpiryDate);
            Optional.ofNullable(stockUpdateRequest.getFactory()).ifPresent(stock::setFactory);
            // Optional.ofNullable(stockUpdateRequest.getFactoryDate())
            // .map(factoryDate -> {
            //     LocalDateTime factoryDateTime = LocalDateTime.parse(factoryDate, DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a"));
            //     return Timestamp.valueOf(factoryDateTime);
            // })
            // .ifPresent(stock::setFactoryDate);
            Optional.ofNullable(stockUpdateRequest.getFactoryDate())
            .map(date -> Timestamp.valueOf(LocalDateTime.of(LocalDate.parse(date), LocalTime.MIDNIGHT)))
            .ifPresent(stock::setFactoryDate);
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
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }

    }

    @Override
    @Transactional
    public void export(String q) {
        httpServletRequest.setAttribute(ACTION, "EXPORT STOCK");
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {
            Specification<Stock> stocks = new CustomSpecification<>(q);
            BaseEntityResponseDto<Stock> stockResult = stockDao.searchStock(stocks);
            httpServletResponse.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=Stocks.xlsx";
            httpServletResponse.setHeader(headerKey, headerValue);
            ExcelUtil stock = new ExcelUtil(stockResult.getEntityList());
            stock.exportDataToExcel(httpServletResponse);
           
        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw new AppException("028",e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true); 
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }
    }
    // @Override
    // @Transactional
    // public void export(String q) {
    //     httpServletRequest.setAttribute(ACTION, "EXPORT STOCK");
    //     try {
    //         CSVHelper<StockCsv> csvService = new CSVHelper<>(StockCsv.class,httpServletResponse);
    //         Specification<StockHistory> stocks = new CustomSpecification<>(q);
    //         BaseEntityResponseDto<StockHistory> stockResult = stockHistoryDao.search(stocks);
    //         List<StockCsv> stockCsvs = new ArrayList<>();

    //         for (StockHistory stockHistory : stockResult.getEntityList()) {
    //             StockCsv stockCsv = new StockCsv();
    //             stockCsv.setId(stockHistory.getStock().getId());
    //             stockCsv.setQty(stockHistory.getStock().getQty());
    //             stockCsv.setFirstname(stockHistory.getFirstname());
    //             stockCsv.setLastname(stockHistory.getLastname());
    //             stockCsv.setProductId(stockHistory.getStock().getProductId());
    //             stockCsv.setCreatedDate(stockHistory.getStock().getCreatedDate());
    //             stockCsv.setUpdatedDate(stockHistory.getStock().getUpdatedDate());
    //             stockCsv.setStockHistoryId(stockHistory.getId());
    //             stockCsv.setRemark(stockHistory.getRemark());
    //             stockCsv.setStockHistoryQty(stockHistory.getQty());
    //             stockCsv.setStockHistoryCreatedDate(stockHistory.getCreatedDate());
    //             stockCsvs.add(stockCsv);
    //         }
    //         csvService.export(stockCsvs, CSV_FILENAME+AppTools.getCurrentDateWithFormatString("YYYY-MM-dd-HH-mm-ss")+".csv");

    //     }catch (DatabaseException e) {
    //         throw e;   
    //     }catch (AppException e) {
    //         throw new AppException("028",e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true); 
    //     }catch(Exception e){
    //         throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
    //     }
    // }


    // @Override
    // public Object importData(MultipartFile file) {
    //     httpServletRequest.setAttribute(ACTION, "IMPORT STOCK");
    //     try {
    //         CSVHelperV2<?> csvService = new CSVHelperV2<>(httpServletResponse,StockCsv.class);
    //         List<StockCsv> stockCsvList = csvService.parseCsv(file);
    //         Map<Long, Stock> stockMap = new HashMap<>();


    //         List<Stock> stockList = new ArrayList<>();
    //         stockCsvList.forEach(stockCsv->{
    //             Stock stock = new Stock();
    //             stock.setId(stockCsv.getId());
    //             stock.setQty(stockCsv.getQty());
    //             stock.setProductId(stockCsv.getProductId());
    //             stock.setCreatedDate(stockCsv.getCreatedDate());
    //             stock.setUpdatedDate(stockCsv.getUpdatedDate());

    //             StockHistory stockHistory = new StockHistory();
    //             stockHistory.setId(stockCsv.getStockHistoryId());
    //             stockHistory.setQty(stockCsv.getStockHistoryQty());
    //             stockHistory.setFirstname(stockCsv.getFirstname());
    //             stockHistory.setLastname(stockCsv.getLastname());
    //             stockHistory.setRemark(stockCsv.getRemark());
    //             stockHistory.setCreatedDate(stockCsv.getStockHistoryCreatedDate());
                
    //             if (stockMap.containsKey(stock.getId())){
    //                 stockMap.get(stock.getId()).addStockHistory(stockHistory);
    //             }else{
    //                 stock.addStockHistory(stockHistory);
    //                 stockMap.put(stock.getId(), stock);
    //             }
    //         });
    //         stockList = stockMap.values().stream().collect(Collectors.toList());
    //         stockDao.saveEntities(stockList);
    //         SuccessResponse<?> response = new SuccessResponse<>();
    //         response.setStatus(SUCCESS);
    //         response.setMsg(AppTools.appGetMessage("031"));
    //         response.setCode("031");
    //         return response;
    //     }catch (DatabaseException e) {
    //         throw e;   
    //     }catch (AppException e) {
    //         throw new AppException("030",e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true); 
    //     }catch(Exception e){
    //         throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        
    //     }
    
    // }
    @Override
    public Object importData(MultipartFile file) {
        httpServletRequest.setAttribute(ACTION, "IMPORT STOCK");
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {
             if(ExcelUtil.isValidExcelFile(file)){
                try {
                    List<Stock> stocks = ExcelUtil.getStockDataFromExcel(file.getInputStream());
                    stockDao.saveEntities(stocks);
                } catch (IOException e) {
                    throw new IllegalArgumentException("The file is not a valid excel file");
                }
            }
            SuccessResponse<?> response = new SuccessResponse<>();
            response.setStatus(SUCCESS);
            response.setMsg(AppTools.appGetMessage("031"));
            response.setCode("031");
            return response;
        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw new AppException("030",e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true); 
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        
        }
    
    }

    @Override
    //@Transactional
    public Object searchV2(String q, int pageNo, int pageSize, Direction sort, String sortByColum) {

        httpServletRequest.setAttribute(ACTION,"SEARCH STOCK");
        SuccessResponse<Object> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
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
            Page<StockDto> stockDtoPage = stockResult.getPage().map(stock ->{
                StockDto stockDto = new StockDto();
                stockDto.setProduct(new StockDto.Product(stock.getProduct().getId(), stock.getProduct().getProductName()));
                BeanUtils.copyProperties(stock, stockDto);
                return stockDto;
            });
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setData(stockDtoPage);
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
    @Transactional
    public Object search(String q, int pageNo, int pageSize, Direction sort, String sortByColum) {

        httpServletRequest.setAttribute(ACTION,"SEARCH STOCK");
        SuccessResponse<Page<Stock>> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
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
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }
       
    } 

    @Override
    @Transactional
    public Object searchHistory(String q, int pageNo, int pageSize, Direction sort, String sortByColum) {

        httpServletRequest.setAttribute(ACTION,"SEARCH STOCK HISTORY");
        SuccessResponse<Object> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {
            // q.concat(q+",stock.id="+stockId);
            Specification<StockHistory> stockHistories = new CustomSpecification<>(q);
            PageRequestDto pageRequestDto = new PageRequestDto();
            pageRequestDto.setPageNo(pageNo);
            pageRequestDto.setPageSize(pageSize);
            pageRequestDto.setSort(sort);
            pageRequestDto.setSortByColumn(sortByColum);
            Pageable pageable = new PageRequestDto().getPageable(pageRequestDto);
            BaseEntityResponseDto<StockHistory> stockHistoryResult = stockHistoryDao.search(stockHistories,pageable);
            if(!stockHistoryResult.getStatus().equals(SUCCESS) || stockHistoryResult.getPage()==null){
                String msg = AppTools.appGetMessage("024");
                throw new AppException("024",msg);
            }

            Page<StockHistoryDto> stockHistoryDtoPage = stockHistoryResult.getPage().map(stockHistory ->{
                StockHistoryDto stockHistoryDto = new StockHistoryDto();
                if (stockHistoryDto!=null){
                    BeanUtils.copyProperties(stockHistory, stockHistoryDto);
                }

                StockHistoryDto.User userDto = new StockHistoryDto.User(stockHistory.getFirstname(),stockHistory.getLastname());
                stockHistoryDto.setUser(userDto);

                StockDetailDto stockDto = new StockDetailDto();
                if (stockHistory.getStock()!=null){
                    BeanUtils.copyProperties(stockHistory.getStock(), stockDto);
                }

                ProductDto productDto = new ProductDto();
                if (stockHistory.getStock().getProduct()!=null){
                    BeanUtils.copyProperties(stockHistory.getStock().getProduct(), productDto);
                }
                stockDto.setProduct(productDto);

                stockHistoryDto.setStock(stockDto);


                return stockHistoryDto;
            });

            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setData(stockHistoryDtoPage);
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
    @Transactional
    public Object addQuantity(Long id, StockUpdateRequest stockUpdateRequest) {

        httpServletRequest.setAttribute(ACTION,"ADD QUANTITY TO STOCK");
        SuccessResponse<Stock> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
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
                com.hfsolution.feature.user.entity.User user = userRepository.findById(userId).get();
                StockHistory stockHistory = new StockHistory();
                stockHistory.setId(stockHistoryDao.getStockHistoryId());
                stockHistory.setFirstname(user.getFirstname());
                stockHistory.setLastname(user.getLastname());
                stockHistory.setRemark(stockUpdateRequest.getRemark());
                stockHistory.setQty(stockUpdateRequest.getQty());
                stockHistory.setCreatedDate(new Timestamp(System.currentTimeMillis()));
                stock.addStockHistory(stockHistory);
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
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }
    }

    @Override
    @Transactional
    public Object removeQuantity(Long id, StockUpdateRequest stockUpdateRequest) {
        httpServletRequest.setAttribute(ACTION,"REMOVE QUANTITY TO STOCK");
        SuccessResponse<Stock> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
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
                com.hfsolution.feature.user.entity.User user = userRepository.findById(userId).get();
                StockHistory stockHistory = new StockHistory();
                stockHistory.setId(stockHistoryDao.getStockHistoryId());
                stockHistory.setFirstname(user.getFirstname());
                stockHistory.setLastname(user.getLastname());
                stockHistory.setRemark(stockUpdateRequest.getRemark());
                stockHistory.setQty(stockUpdateRequest.getQty()*-1);
                stockHistory.setCreatedDate(new Timestamp(System.currentTimeMillis()));
                stock.addStockHistory(stockHistory);
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
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }
    }



    private void calculatePercentageOfQty(Page<Stock> stockPage) {
        BaseEntityResponseDto<StockPercentageDto> stockPercentage = stockDao.findPercentage(stockPage.stream().map(stock -> stock.getId()).toList());
        
        for (StockPercentageDto stockPercentageDto : stockPercentage.getEntityList()) {
            for (Stock stock : stockPage) {
                if (stock.getId() == stockPercentageDto.getId()){
                    BigDecimal bd = new BigDecimal(stockPercentageDto.getPercentage()).setScale(2, RoundingMode.HALF_UP);
                    stock.setPercentage(bd.doubleValue());
                }
            }
        }

    }

    private void calculatePercentageOfQty(Stock stock) {
        BaseEntityResponseDto<StockPercentageDto> stockPercentage = stockDao.findPercentage(Arrays.asList(stock.getId()));
        
        for (StockPercentageDto stockPercentageDto : stockPercentage.getEntityList()) {
            if (stock.getId() == stockPercentageDto.getId()){
                BigDecimal bd = new BigDecimal(stockPercentageDto.getPercentage()).setScale(2, RoundingMode.HALF_UP);
                stock.setPercentage(bd.doubleValue());
            }
        }

    }

    @Override
    @Transactional
    public Object searchDetail(long id) {
        httpServletRequest.setAttribute(ACTION,"SEARCH STOCK DETAIL BY ID");
        SuccessResponse<Object> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
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
                StockHistoryDto.User user = new StockHistoryDto.User(stockHistory.getFirstname(),stockHistory.getLastname());
                stockHistoryDto.setUser(user);

                stockHistoryDtos.add(stockHistoryDto);
            }

            calculatePercentageOfQty(stockResult.getEntity());
            Double stockPercentage = stockResult.getEntity().getPercentage();

            Product product = stockResult.getEntity().getProduct();

            //COPY Product Property
            ProductDto productDto = new ProductDto();
            BeanUtils.copyProperties(product, productDto);
            stockDetailDto.setStockHistories(stockHistoryDtos);
            stockDetailDto.setProduct(productDto);
            stockDetailDto.setPercentage(stockPercentage);
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setData(stockDetailDto);
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

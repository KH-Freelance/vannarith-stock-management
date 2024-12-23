package com.hfsolution.feature.stockmanagement.service.recovery;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.sql.DataSource;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hfsolution.app.dto.BaseEntityResponseDto;
import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.app.util.AppTools;
import com.hfsolution.feature.stockmanagement.dao.CustomerDao;
import com.hfsolution.feature.stockmanagement.dao.PaymentDao;
import com.hfsolution.feature.stockmanagement.dao.ProductDao;
import com.hfsolution.feature.stockmanagement.dao.PurchaseDao;
import com.hfsolution.feature.stockmanagement.dao.PurchaseItemDao;
import com.hfsolution.feature.stockmanagement.dao.StockDao;
import com.hfsolution.feature.stockmanagement.dao.StockHistoryDao;
import com.hfsolution.feature.stockmanagement.entity.Customer;
import com.hfsolution.feature.stockmanagement.entity.Payment;
import com.hfsolution.feature.stockmanagement.entity.Product;
import com.hfsolution.feature.stockmanagement.entity.Purchase;
import com.hfsolution.feature.stockmanagement.entity.PurchaseItem;
import com.hfsolution.feature.stockmanagement.entity.Stock;
import com.hfsolution.feature.stockmanagement.entity.StockHistory;
import com.hfsolution.feature.stockmanagement.util.ExcelService;
import com.hfsolution.feature.user.entity.User;
import com.hfsolution.feature.user.repository.UserRepository;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;

@Service
public class RecoveryServiceImp implements RecoveryService{

    @Autowired
    private ProductDao productDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PurchaseDao purchaseDao;

    @Autowired
    private UserRepository userRepository;

    @Autowired 
    private StockDao stockDao;

    private final String UPLOAD_DIR = "./uploaded_files";


    @Override
    public Object recovery(MultipartFile file) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'recovery'");
    }

    @Override
    @Transactional
    public Object backup() throws IOException {
        ExcelService excelService = new ExcelService();
        try {
            List<Product> products = productDao.findAll().getEntityList();
            List<Customer> customers = customerDao.findAll().getEntityList();
            List<Purchase> purchases = purchaseDao.findAll().getEntityList();
            List<Stock> stocks = stockDao.findAll().getEntityList();
            List<User> users = userRepository.findAll();
            System.out.println("before");
            excelService.writeCustomerData(customers);
            System.out.println("before1");
            excelService.writeProductData(products);
            System.out.println("before2");
            excelService.writeUserData(users);
            System.out.println("before3");
            excelService.writePurchaseData(purchases);
            System.out.println("before4");
            excelService.writePurchaseItemsData(purchases);
            System.out.println("before4");
            excelService.writePaymentData(purchases);
            System.out.println("before5");
            excelService.writeStockData(stocks);
            System.out.println("before6");
            excelService.writeStockHistoryData(stocks);
            System.out.println("after");
    
            Path savePath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(savePath)) {
                System.out.println("Create Dir: "+savePath.toAbsolutePath());
                Files.createDirectories(savePath); // Ensure the directory is created if it doesn't exist.
                System.out.println("Create Dir Successfully: "+savePath.toAbsolutePath());
            }

            
            Path filePath = savePath.resolve("back-up-%s.xlsx".formatted(AppTools.getCurrentDateWithFormatString("YYYY-MM-dd")));
            System.out.println("Dir Existed: "+filePath.toAbsolutePath());

            excelService.exportDataToExcel(filePath);
            
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

       
      
      
        SuccessResponse<Object> response = new SuccessResponse<>();
        response.setStatus(SUCCESS);
        response.setCode(SUCCESS_CODE);
        response.setMsg("Backup in progressing");
        return response;
        
    }
    // @Override
    // @Transactional
    // public Object backup() throws IOException {
    //     ExcelService excelService = new ExcelService();
    //     // CompletableFuture<BaseEntityResponseDto<Product>>  productFuture = productDao.findAllAsync();
    //     // CompletableFuture<BaseEntityResponseDto<Customer>>  customerFuture = customerDao.findAllAsync();
    //     // CompletableFuture<BaseEntityResponseDto<Stock>>  stockFuture = stockDao.findAllAsync();
    //     // CompletableFuture<BaseEntityResponseDto<Purchase>>  purchaseFuture = purchaseDao.findAllAsync();
    //     // List<User> users = userRepository.findAll();
    //     // CompletableFuture<Void> allOf = CompletableFuture.allOf(productFuture,customerFuture,stockFuture,purchaseFuture);
    //     // allOf.thenRun(() -> {
    //     //     try {
                

    //     //         List<Product> products = productFuture.get().getEntityList(); // Get user results
    //     //         List<Customer> customers = customerFuture.get().getEntityList(); // Get user results
    //     //         List<Stock> stocks = stockFuture.get().getEntityList(); // Get user results
    //     //         List<Purchase> purchases = purchaseFuture.get().getEntityList(); // Get user results

    //     //         excelService.writeCustomerData(customers);
    //     //         excelService.writeProductData(products);
    //     //         excelService.writeUserData(users);
    //     //         excelService.writePurchaseData(purchases);
    //     //         excelService.writePurchaseItemsData(purchases);
    //     //         excelService.writePaymentData(purchases);
    //     //         excelService.writeStockData(stocks);
    //     //         excelService.writeStockHistoryData(stocks);
    //     //     } catch (Exception e) {
    //     //         e.printStackTrace();
    //     //     }
    //     // });

    //     // List<Product> products = new ArrayList<>();
    //     // List<Customer> customers = new ArrayList<>();;
    //     // List<Purchase> purchases = new ArrayList<>();;
    //     // List<Stock> stocks = new ArrayList<>();;
    //     // try {
    //     //     products = productFuture.get().getEntityList();
    //     //     customers = customerFuture.get().getEntityList(); // Get user results
    //     //     stocks = stockFuture.get().getEntityList(); // Get user results
    //     //     purchases = purchaseFuture.get().getEntityList(); // Get user results

    //     // } catch (InterruptedException | ExecutionException e) {
    //     //     // TODO Auto-generated catch block
    //     //     e.printStackTrace();
    //     // } // Get user results
        

    //     List<Product> products = productDao.findAll().getEntityList();
    //     List<Customer> customers = customerDao.findAll().getEntityList();
    //     List<Purchase> purchases = purchaseDao.findAll().getEntityList();
    //     List<Stock> stocks = stockDao.findAll().getEntityList();
    //     List<User> users = userRepository.findAll();

    //     products.stream().forEach(result->System.out.println(result.getProductName()));
    //     customers.stream().forEach(result->System.out.println(result.getCustomerName()));
    //     // purchaseDao.stream().forEach(result->System.out.println(result.get));
    //     stocks.stream().forEach(result->System.out.println(result.getQty()));
    //     purchases.stream().forEach(result->System.out.println(result.getTotal()));

    //     excelService.writeCustomerData(customers);
    //     excelService.writeProductData(products);
    //     excelService.writeUserData(users);
    //     excelService.writePurchaseData(purchases);
    //     excelService.writePurchaseItemsData(purchases);
    //     excelService.writePaymentData(purchases);
    //     excelService.writeStockData(stocks);
    //     excelService.writeStockHistoryData(stocks);

    //     // response.setContentType("application/octet-stream");
    //     // String headerKey = "Content-Disposition";
    //     // String headerValue = "attachment; filename=backup.xlsx";
    //     // response.setHeader(headerKey, headerValue);
    //     String directoryPath = "./uploads"; // Relative or absolute path

    //     // Create a File object
    //     File directory = new File(directoryPath);

    //     // Check if the directory already exists
    //     if (!directory.exists()) {
    //         // Attempt to create the directory
    //         if (directory.mkdir()) {
    //             System.out.println("Directory created successfully at: " + directory.getAbsolutePath());
    //         } else {
    //             System.out.println("Failed to create directory!");
    //         }
    //     } else {
    //         System.out.println("Directory already exists: " + directory.getAbsolutePath());
    //     }
    //     try {
    //         Thread.sleep(10000L);
    //     } catch (InterruptedException e) {
    //         // TODO Auto-generated catch block
    //         e.printStackTrace();
    //     }
    //     excelService.exportDataToExcel("./uploads/back-up-%s.xlsx".formatted(AppTools.getCurrentDateWithFormatString("YYYY-MM-dd")));
    //     SuccessResponse<Object> response = new SuccessResponse<>();
    //     response.setStatus(SUCCESS);
    //     response.setCode(SUCCESS_CODE);
    //     response.setMsg("Backup in progressing");
    //     return response;
        
    // }

    @Override
    public ResponseEntity<Resource>  getExcelData() throws URISyntaxException, IOException {
       try {
            Path savePath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(savePath)) {
                Files.createDirectories(savePath); // Ensure the directory is created if it doesn't exist.
            }

            Path filePath = savePath.resolve("back-up-%s.xlsx".formatted(AppTools.getCurrentDateWithFormatString("YYYY-MM-dd")));

            // // Create parent directories if they don't exist
            // Files.createDirectories(path.getParent());

            // // Create the file if it doesn't exist
            // if (!Files.exists(path)) {
            //     Files.createFile(path);
            //     System.out.println("File created: " + filePath);
            // } else {
            //     System.out.println("File already exists: " + filePath);
            // }
            // Path to the file on the server
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                // Set headers to indicate the file type and disposition
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // @Override
    // public ResponseEntity<Resource>  getExcelData() throws URISyntaxException, IOException {
    //    try {
    //         String filePath = "./back-up-%s.xlsx".formatted(AppTools.getCurrentDateWithFormatString("YYYY-MM-dd"));

    //         Path path = Paths.get(filePath).toAbsolutePath().normalize();

    //         // Path to the file on the server
    //         Resource resource = new UrlResource(path.toUri());

    //         if (resource.exists()) {
    //             // Set headers to indicate the file type and disposition
    //             HttpHeaders headers = new HttpHeaders();
    //             headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());

    //             return ResponseEntity.ok()
    //                     .headers(headers)
    //                     .body(resource);
    //         } else {
    //             return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    //         }
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    //     }
    // }
}
    


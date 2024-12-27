package com.hfsolution.feature.stockmanagement.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.hfsolution.feature.stockmanagement.entity.Customer;
import com.hfsolution.feature.stockmanagement.entity.Payment;
import com.hfsolution.feature.stockmanagement.entity.Product;
import com.hfsolution.feature.stockmanagement.entity.Purchase;
import com.hfsolution.feature.stockmanagement.entity.PurchaseItem;
import com.hfsolution.feature.stockmanagement.entity.Stock;
import com.hfsolution.feature.stockmanagement.entity.StockHistory;
import com.hfsolution.feature.user.entity.User;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;


public class ExcelService {


    private XSSFWorkbook workbook;
    private XSSFSheet sheet;



    private final String CUSTOMER_SHEET = "customer";
    private final String PRODUCT_SHEET = "product";
    private final String PURCHASE_SHEET = "purchase";
    private final String STOCK_SHEET = "stock";
    private final String STOCK_HISTORY_SHEET = "stockHistory";
    private final String PAYMENT_SHEET = "payment";
    private final String USER_SHEET = "user";
    private final String PURCHASE_ITEM_SHEET = "purchaseItem";

    
    public ExcelService(){
        workbook = new XSSFWorkbook();
    }

    public void createCell(Row row, int columnCount, Object value, CellStyle style){

        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer){
            cell.setCellValue((Integer) value);
        }else if (value instanceof Double){
            cell.setCellValue((Double) value);
        }else if (value instanceof Boolean){
            cell.setCellValue((Boolean) value);
        }else if (value instanceof Long){
            cell.setCellValue((Long) value);
        }else if (value instanceof BigDecimal){
            BigDecimal bigDecimalValue = (BigDecimal) value;
            cell.setCellValue(bigDecimalValue.doubleValue()); // Convert to double
        }else if (value instanceof java.sql.Timestamp) {
            java.sql.Timestamp timestampValue = (java.sql.Timestamp) value;
            cell.setCellValue(timestampValue.toString()); // Convert to String
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

     public void writeUserData(List<User> userList){
        sheet   = workbook.createSheet(USER_SHEET);
        Row rowHeader = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(20);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        createCell(rowHeader, 0, "User Information", style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
        font.setFontHeightInPoints((short) 10);
        int rowCount = 1;
        font.setFontHeight(14);
        style.setFont(font);
        
        for (User user : userList) {
           
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++,  user.getId(), style);
            createCell(row, columnCount++,  user.getFirstname(), style);
            createCell(row, columnCount++,  user.getLastname(), style);
            createCell(row, columnCount++,  user.getEmail(), style);
            createCell(row, columnCount++,  user.getPassword(), style);
            createCell(row, columnCount++,  user.getRole().getName(), style);
        }

       
    }

    public void writeRoleData(List<User> userList){
        sheet   = workbook.createSheet(USER_SHEET);
        Row rowHeader = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(20);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        createCell(rowHeader, 0, "User Information", style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
        font.setFontHeightInPoints((short) 10);
        int rowCount = 1;
        font.setFontHeight(14);
        style.setFont(font);
        
        for (User user : userList) {
            // for (Cell cell : user.getRole()) {
                
            // }
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++,  user.getId(), style);
            createCell(row, columnCount++,  user.getFirstname(), style);
            createCell(row, columnCount++,  user.getLastname(), style);
            createCell(row, columnCount++,  user.getEmail(), style);
            createCell(row, columnCount++,  user.getPassword(), style);
            createCell(row, columnCount++,  user.getRole().getName(), style);
        }

       
    }

    public void writeStockData(List<Stock> stockList){
        sheet   = workbook.createSheet(STOCK_SHEET);
        Row rowHeader = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(20);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        createCell(rowHeader, 0, "Stock Information", style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
        font.setFontHeightInPoints((short) 10);
        int rowCount = 1;

        font.setFontHeight(14);
        style.setFont(font);

        for (Stock UpdatedDate : stockList){
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, UpdatedDate.getId(), style);
            createCell(row, columnCount++, UpdatedDate.getProductId(), style);
            createCell(row, columnCount++, UpdatedDate.getQty(), style);
            createCell(row, columnCount++, UpdatedDate.getPercentage(), style);
            createCell(row, columnCount++, UpdatedDate.getCreatedDate(), style);
            createCell(row, columnCount++, UpdatedDate.getUpdatedDate(), style);
        }
    }

    

   public void writeProductData(List<Product> productList){
        sheet   = workbook.createSheet(PRODUCT_SHEET);
        Row rowHeader = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(20);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        createCell(rowHeader, 0, "Product Information", style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
        font.setFontHeightInPoints((short) 10);
        int rowCount = 1;

        font.setFontHeight(14);
        style.setFont(font);

        for (Product UpdatedDate : productList){
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, UpdatedDate.getId(), style);
            createCell(row, columnCount++, UpdatedDate.getProductName(), style);
            createCell(row, columnCount++, UpdatedDate.getProductDesc(), style);
            createCell(row, columnCount++, UpdatedDate.getPrice(), style);
            createCell(row, columnCount++, UpdatedDate.getPrice(), style);
            createCell(row, columnCount++, UpdatedDate.getDiscount(), style);
            createCell(row, columnCount++, UpdatedDate.getCreatedDate(), style);
            createCell(row, columnCount++, UpdatedDate.getUpdatedDate(), style);
            //createCell(row, columnCount++, UpdatedDate.getExpiryDate(), style);
        }
    }

    public void writePurchaseData(List<Purchase> purchases){
        sheet   = workbook.createSheet(PURCHASE_SHEET);
        Row rowHeader = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(20);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        createCell(rowHeader, 0, "Purchase Information", style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
        font.setFontHeightInPoints((short) 10);
        int rowCount = 1;

        font.setFontHeight(14);
        style.setFont(font);

        for (Purchase UpdatedDate : purchases){
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, UpdatedDate.getId(), style);
            createCell(row, columnCount++, UpdatedDate.getCustomer().getId(), style);
            createCell(row, columnCount++, UpdatedDate.getUser().getId(), style);
            createCell(row, columnCount++, UpdatedDate.getQty(), style);
            createCell(row, columnCount++, UpdatedDate.getTotal(), style);
            createCell(row, columnCount++, UpdatedDate.getPaymentType().name(), style);
            createCell(row, columnCount++, UpdatedDate.getPaymentStatus().name(), style);
            createCell(row, columnCount++, UpdatedDate.getLocation(), style);
            createCell(row, columnCount++, UpdatedDate.getPurchaseCode(), style);
            createCell(row, columnCount++, UpdatedDate.getCreatedDate(), style);
            createCell(row, columnCount++, UpdatedDate.getUpdatedDate(), style);
        }
    }

    public void writePaymentData(List<Purchase> purchases){
        sheet   = workbook.createSheet(PAYMENT_SHEET);
        Row rowHeader = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(20);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        createCell(rowHeader, 0, "Payment Information", style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
        font.setFontHeightInPoints((short) 10);
        int rowCount = 1;

        font.setFontHeight(14);
        style.setFont(font);

        for (Purchase purchase : purchases){
            for (Payment payment : purchase.getPayments()) {
                Row row = sheet.createRow(rowCount++);
                int columnCount = 0;
                createCell(row, columnCount++, payment.getId(), style);
                createCell(row, columnCount++, payment.getPurchase().getId(), style);
                createCell(row, columnCount++, payment.getAmount(), style);
                createCell(row, columnCount++, payment.getCreatedDate(), style);
                createCell(row, columnCount++, payment.getUpdatedDate(), style);
            }
        }
    }

    public void writePurchaseItemsData(List<Purchase> purchases){
        sheet   = workbook.createSheet(PURCHASE_ITEM_SHEET);
        Row rowHeader = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(20);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        createCell(rowHeader, 0, "Purchase Items Information", style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
        font.setFontHeightInPoints((short) 10);
        int rowCount = 1;
        font.setFontHeight(14);
        style.setFont(font);

        for (Purchase purchase : purchases){
            for (PurchaseItem purchaseItem : purchase.getPurchaseItems()) {
                Row row = sheet.createRow(rowCount++);
                int columnCount = 0;
                createCell(row, columnCount++, purchaseItem.getId(), style);
                createCell(row, columnCount++, purchaseItem.getPurchase().getId(), style);
                createCell(row, columnCount++, purchaseItem.getProductId(), style);
                createCell(row, columnCount++, purchaseItem.getStatus(), style);
                createCell(row, columnCount++, purchaseItem.getQty(), style);
                createCell(row, columnCount++, purchaseItem.getPrice(), style);
                createCell(row, columnCount++, purchaseItem.getDiscount(), style);
            }
        }
    }

    public void writeCustomerData(List<Customer> customerList){
       try {
        sheet   = workbook.createSheet(CUSTOMER_SHEET);
        Row rowHeader = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(20);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        createCell(rowHeader, 0, "Customer Information", style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
        font.setFontHeightInPoints((short) 10);
        int rowCount = 1;
        font.setFontHeight(14);
        style.setFont(font);

        for (Customer customer : customerList){
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, customer.getId(), style);
            createCell(row, columnCount++, customer.getCustomerName(), style);
            createCell(row, columnCount++, customer.getEmail(), style);
            createCell(row, columnCount++, customer.getPhone(), style);
            createCell(row, columnCount++, customer.getAddress(), style);
            createCell(row, columnCount++, customer.getDiscount(), style);
            createCell(row, columnCount++, customer.getCredit(), style);
            createCell(row, columnCount++, customer.getCreatedDate(), style);
            createCell(row, columnCount++, customer.getUpdatedDate(), style);
        }
       } catch (Exception e) {
        // TODO: handle exception
        e.printStackTrace();
       }

    }

    public void writeStockHistoryData(List<Stock> stockList){
        sheet   = workbook.createSheet(STOCK_HISTORY_SHEET);
        Row rowHeader = sheet.createRow(0);
        
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(20);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        createCell(rowHeader, 0, "Stock History Information", style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
        font.setFontHeightInPoints((short) 10);
        int rowCount = 1;

        font.setFontHeight(14);
        style.setFont(font);

        for (Stock UpdatedDate : stockList){
            for (StockHistory stockHistory : UpdatedDate.getStockHistories()) {
                Row row = sheet.createRow(rowCount++);
                int columnCount = 0;
                createCell(row, columnCount++, stockHistory.getId(), style);
                createCell(row, columnCount++, stockHistory.getStock().getId(), style);
                createCell(row, columnCount++, stockHistory.getFirstname(), style);
                createCell(row, columnCount++, stockHistory.getLastname(), style);
                createCell(row, columnCount++, stockHistory.getQty(), style);
                createCell(row, columnCount++, stockHistory.getRemark(), style);
                createCell(row, columnCount++, stockHistory.getCreatedDate(), style);
            }
        }
    }

    public void exportDataToExcel(HttpServletResponse response) throws IOException {
        
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
    public void exportDataToExcel(Path filePath) throws IOException {
        
        try (OutputStream outputStream = Files.newOutputStream(filePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            workbook.write(outputStream);
            workbook.close();
            outputStream.close();

            System.out.println("File written successfully using OutputStream!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
}

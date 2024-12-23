package com.hfsolution.feature.stockmanagement.util.purchase;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import com.hfsolution.feature.stockmanagement.entity.Customer;
import com.hfsolution.feature.stockmanagement.entity.Payment;
import com.hfsolution.feature.stockmanagement.entity.Purchase;
import com.hfsolution.feature.stockmanagement.entity.PurchaseItem;
import com.hfsolution.feature.stockmanagement.enums.PaymentStatus;
import com.hfsolution.feature.stockmanagement.enums.PaymentType;
import com.hfsolution.feature.user.entity.User;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

public class ExcelUtil {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Purchase> purchases;
    private final String SHEET_PURCHASE = "purchases";
    private final String SHEET_PAYMENT = "payment";
    private final String SHEET_PURCHASE_ITEM = "purchaseItems";

    // Export
     
    public ExcelUtil(List<Purchase> purchases) {
        this.purchases = purchases;
        workbook = new XSSFWorkbook();
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style){
        
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

    private void createPurchaseHeaderRow(){
        
        sheet   = workbook.createSheet(SHEET_PURCHASE);
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(20);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        createCell(row, 0, "Purchase Information", style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
        font.setFontHeightInPoints((short) 10);

        row = sheet.createRow(1);
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "ID", style);
        createCell(row, 1, "Customer ID", style);
        createCell(row, 2, "User ID", style);
        createCell(row, 3, "Qty", style);
        createCell(row, 4, "Total", style);
        createCell(row, 5, "Payment Type", style);
        createCell(row, 6, "Payment Status", style);
        createCell(row,7, "Location", style);
        createCell(row, 8, "Purchase Code", style);
        createCell(row, 9, "CreatedDate", style);
        createCell(row, 10, "UpdatedDate", style);
    }
    private void createPurchaseItemHeaderRow(){
        
        sheet   = workbook.createSheet(SHEET_PURCHASE_ITEM);
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(20);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        createCell(row, 0, "Purchase Items Information", style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
        font.setFontHeightInPoints((short) 10);

        row = sheet.createRow(1);
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "ID", style);
        createCell(row, 1, "Purchase ID", style);
        createCell(row, 2, "Product ID", style);
        createCell(row, 3, "Status", style);
        createCell(row, 4, "Qty", style);
        createCell(row, 5, "Price", style);
        createCell(row, 6, "Discount", style);
    }


    private void createPaymentHeaderRow(){
        
        sheet   = workbook.createSheet(SHEET_PAYMENT);
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(20);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        createCell(row, 0, "Payment Information", style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
        font.setFontHeightInPoints((short) 10);

        row = sheet.createRow(1);
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "ID", style);
        createCell(row, 1, "Purchase ID", style);
        createCell(row, 2, "Amount", style);
        createCell(row, 3, "CreatedDate", style);
        createCell(row, 4, "UpdatedDate", style);
    }

    private void writePurchaseData(){
        
        int rowCount = 2;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
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
    private void writePaymentData(){
        
        int rowCount = 2;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
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
    private void writePurchaseItemsData(){
        
        int rowCount = 2;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
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

    public void exportDataToExcel(HttpServletResponse response) throws IOException {
        createPurchaseHeaderRow();
        writePurchaseData();
        createPaymentHeaderRow();
        writePaymentData();
        createPurchaseItemHeaderRow();
        writePurchaseItemsData();
        createCustomerHeaderRow();
        writeCustomerData();
        createUserHeaderRow();
        writeUserData();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    private void createCustomerHeaderRow(){
        
        sheet   = workbook.createSheet("customers");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(20);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        createCell(row, 0, "Customer Information", style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
        font.setFontHeightInPoints((short) 10);

        row = sheet.createRow(1);
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "ID", style);
        createCell(row, 1, "Customer", style);
        createCell(row, 2, "Email", style);
        createCell(row, 3, "Phone", style);
        createCell(row, 4, "Address", style);
        createCell(row, 5, "Discount", style);
        createCell(row, 6, "Credit", style);
        createCell(row, 7, "CreatedDate", style);
        createCell(row, 8, "UpdatedDate", style);
    }

    private void writeCustomerData(){
        
        int rowCount = 2;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        List<Customer> customers = purchases.stream().map(purchase->purchase.getCustomer()).toList();
        Set<Customer> customersSet = new HashSet<>(customers);
        for (Customer customer : customersSet) {
           
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++,  customer.getId(), style);
            createCell(row, columnCount++,  customer.getCustomerName(), style);
            createCell(row, columnCount++,  customer.getEmail(), style);
            createCell(row, columnCount++,  customer.getPhone(), style);
            createCell(row, columnCount++,  customer.getAddress(), style);
            createCell(row, columnCount++,  customer.getDiscount(), style);
            createCell(row, columnCount++,  customer.getCredit(), style);
            createCell(row, columnCount++,  customer.getCreatedDate(), style);
            createCell(row, columnCount++,  customer.getUpdatedDate(), style);
            
        }

       
    }

    private void createUserHeaderRow(){
        
        sheet   = workbook.createSheet("users");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(20);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        createCell(row, 0, "User Information", style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
        font.setFontHeightInPoints((short) 10);

        row = sheet.createRow(1);
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "ID", style);
        createCell(row, 1, "First Name", style);
        createCell(row, 2, "Last Name", style);
        createCell(row, 3, "Email", style);
        createCell(row, 4, "Password", style);
        createCell(row, 5, "Role", style);
    }

    private void writeUserData(){
        
        int rowCount = 2;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        List<User> users = purchases.stream().map(purchase->purchase.getUser()).toList();
        Set<User> usersSet = new HashSet<>(users);
        for (User user : usersSet) {
           
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


    // Import 
    public static boolean isValidExcelFile(MultipartFile file){
        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" );
    }

    public static List<Purchase> getPurchaseDataFromExcel(InputStream inputStream){
        List<Purchase> purchases = new ArrayList<>();
        Map<Long, Purchase> purchaseMap = new HashMap<>();
       try {
           XSSFWorkbook workbook = new XSSFWorkbook(inputStream);


        //     // Customer
        //     List<Customer> customers = new ArrayList<>();
        //    XSSFSheet custoomerSheet = workbook.getSheet("customers");
        //    int custoemrRowIndex = 0;
        //    for (Row row : custoomerSheet){
        //         if (custoemrRowIndex <= 1){
        //             custoemrRowIndex++;
        //            continue;
        //        }
        //        Iterator<Cell> cellIterator = row.iterator();
        //        int cellIndex = 0;
        //        Customer customer = new Customer();
               
        //        while (cellIterator.hasNext()){
        //            Cell cell = cellIterator.next();
        //            switch (cellIndex){
        //                case 0 -> customer.setId((long)cell.getNumericCellValue());
        //                case 1 -> customer.setCustomerName(cell.getStringCellValue());
        //                case 2 -> customer.setEmail(cell.getStringCellValue());
        //                case 3 -> customer.setPhone(cell.getStringCellValue());
        //                case 4 -> customer.setAddress(cell.getStringCellValue());
        //                case 5 -> customer.setDiscount(new BigDecimal(cell.getNumericCellValue()));
        //                case 6 -> customer.setCredit(new BigDecimal(cell.getNumericCellValue()));
        //                case 7 -> customer.setCreatedDate(Timestamp.valueOf(cell.getStringCellValue()));
        //                case 8 -> customer.setUpdatedDate(Timestamp.valueOf(cell.getStringCellValue()));
        //                default -> {
        //                }
        //            }
        //            cellIndex++;
        //        }
        //        customers.add(customer);
        //    }


            // Stock History
           XSSFSheet sheet = workbook.getSheet("purchases");
           int rowIndex = 0;
           for (Row row : sheet){
                if (rowIndex <= 1){
                   rowIndex++;
                   continue;
               }
               Iterator<Cell> cellIterator = row.iterator();
               int cellIndex = 0;
               Purchase purchase = new Purchase();
               
               while (cellIterator.hasNext()){
                   Cell cell = cellIterator.next();
                   switch (cellIndex){
                       case 0 -> purchase.setId((long)cell.getNumericCellValue());
                       case 1 ->  //No Need data 
                                {}
                       case 2 -> //No Need data 
                                {}
                       case 3 -> purchase.setQty((long)cell.getNumericCellValue());
                       case 4 -> purchase.setTotal(new BigDecimal(cell.getNumericCellValue()));
                       case 5 -> purchase.setPaymentType(PaymentType.valueOf(cell.getStringCellValue()));
                       case 6 -> purchase.setPaymentStatus(PaymentStatus.valueOf(cell.getStringCellValue()));
                       case 7 -> purchase.setLocation(cell.getStringCellValue());
                       case 8 -> purchase.setPurchaseCode(cell.getStringCellValue());
                       case 9 -> purchase.setCreatedDate(Timestamp.valueOf(cell.getStringCellValue()));
                       case 10 -> purchase.setUpdatedDate(Timestamp.valueOf(cell.getStringCellValue()));
                       default -> {
                       }
                   }
                   cellIndex++;
               }

               purchaseMap.put(purchase.getId(), purchase);
           }
           // Stock History
           XSSFSheet payemntSheet = workbook.getSheet("payment");
           int rowIndex2 =0;
           for (Row row : payemntSheet){
               if (rowIndex2 <= 1){
                rowIndex2++;
                   continue;
               }
               Iterator<Cell> cellIterator = row.iterator();
               int cellIndex = 0;
               Payment payment = new Payment();
               Long purchaseId = 0L;
               
               while (cellIterator.hasNext()){
                   Cell cell = cellIterator.next();
                   switch (cellIndex){
                       case 0 -> payment.setId((long)cell.getNumericCellValue());
                       case 1 -> purchaseId = ((long)cell.getNumericCellValue());
                       case 2 -> payment.setAmount(new BigDecimal(cell.getNumericCellValue()));
                       case 3 -> payment.setCreatedDate(Timestamp.valueOf(cell.getStringCellValue()));
                       case 4 -> payment.setUpdatedDate(Timestamp.valueOf(cell.getStringCellValue()));
                       default -> {
                       }
                   }
                   cellIndex++;
               }
               if(purchaseMap.containsKey(purchaseId)) {
                 purchaseMap.get(purchaseId).addPayment(payment);
               }
           }
           // Stock History
           XSSFSheet purchaseItemSheet = workbook.getSheet("purchaseItems");
           int rowIndex3 = 0;
           for (Row row : purchaseItemSheet){
               if (rowIndex3 <= 1){
                rowIndex3++;
                   continue;
               }
               Iterator<Cell> cellIterator = row.iterator();
               int cellIndex = 0;
               PurchaseItem purchaseItem = new PurchaseItem();
               Long purchaseId = 0L;
               
               while (cellIterator.hasNext()){
                   Cell cell = cellIterator.next();
                   switch (cellIndex){
                       case 0 -> purchaseItem.setId((long)cell.getNumericCellValue());
                       case 1 -> purchaseId = ((long)cell.getNumericCellValue());
                       case 2 -> purchaseItem.setProductId((long)cell.getNumericCellValue());
                       case 3 -> purchaseItem.setStatus(cell.getStringCellValue());
                       case 4 -> purchaseItem.setQty((long)cell.getNumericCellValue());
                       case 5 -> purchaseItem.setPrice(new BigDecimal(cell.getNumericCellValue()));
                       case 6 -> purchaseItem.setDiscount(new BigDecimal(cell.getNumericCellValue()));
                       default -> {
                       }
                   }
                   cellIndex++;
               }
               if(purchaseMap.containsKey(purchaseId)) {
                 purchaseMap.get(purchaseId).addPurchaseItem(purchaseItem);
               }
           }

       } catch (IOException e) {
           e.getStackTrace();
       }
        purchases = purchaseMap.values().stream().collect(Collectors.toList());
       return purchases;
   }
}

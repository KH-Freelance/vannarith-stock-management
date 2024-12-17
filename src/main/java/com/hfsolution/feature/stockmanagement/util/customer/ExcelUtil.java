package com.hfsolution.feature.stockmanagement.util.customer;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

public class ExcelUtil {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Customer> customerList;

    public ExcelUtil(List<Customer> customerList) {
        this.customerList = customerList;
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

    private void createHeaderRow(){
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
    }

    public void exportDataToExcel(HttpServletResponse response) throws IOException {
        createHeaderRow();
        writeCustomerData();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    public static boolean isValidExcelFile(MultipartFile file){
        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" );
    }
   public static List<Customer> getCustomersDataFromExcel(InputStream inputStream){
        List<Customer> customers = new ArrayList<>();
       try {
           XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
           XSSFSheet sheet = workbook.getSheet("customers");
           int rowIndex =0;
           for (Row row : sheet){
               if (rowIndex <= 1){
                   rowIndex++;
                   continue;
               }
               Iterator<Cell> cellIterator = row.iterator();
               int cellIndex = 0;
               Customer customer = new Customer();
               
               while (cellIterator.hasNext()){
                   Cell cell = cellIterator.next();
                   switch (cellIndex){
                       case 0 -> customer.setId((long)cell.getNumericCellValue());
                       case 1 -> customer.setCustomerName(cell.getStringCellValue());
                       case 2 -> customer.setEmail(cell.getStringCellValue());
                       case 3 -> customer.setPhone(cell.getStringCellValue());
                       case 4 -> customer.setAddress(cell.getStringCellValue());
                       case 5 -> customer.setDiscount(new BigDecimal(cell.getNumericCellValue()));
                       case 6 -> customer.setCredit(new BigDecimal(cell.getNumericCellValue()));
                       case 7 -> customer.setCreatedDate(Timestamp.valueOf(cell.getStringCellValue()));
                       case 8 -> customer.setUpdatedDate(Timestamp.valueOf(cell.getStringCellValue()));
                       default -> {
                       }
                   }
                   cellIndex++;
               }
               customers.add(customer);
           }
       } catch (IOException e) {
           e.getStackTrace();
       }
       return customers;
   }
}

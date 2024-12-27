package com.hfsolution.feature.stockmanagement.util.product;

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
import com.hfsolution.feature.stockmanagement.entity.Product;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

public class ExcelUtil {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Product> productList;

    // Export
     
    public ExcelUtil(List<Product> productList) {
        this.productList = productList;
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

    private void createHeaderRow(String sheetname){
        sheet   = workbook.createSheet(sheetname);
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(20);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        createCell(row, 0, "Product Information", style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
        font.setFontHeightInPoints((short) 10);

        row = sheet.createRow(1);
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "ID", style);
        createCell(row, 1, "Product Name", style);
        createCell(row, 2, "Description", style);
        createCell(row, 3, "Price", style);
        createCell(row, 4, "Import Price", style);
        createCell(row, 5, "Discount", style);
        createCell(row, 6, "CreatedDate", style);
        createCell(row, 7, "UpdatedDate", style);
        createCell(row, 8, "ExpiryDate", style);
    }

    private void writeCustomerData(){
        int rowCount = 2;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
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

    public void exportDataToExcel(HttpServletResponse response) throws IOException {
        createHeaderRow("product");
        writeCustomerData();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }


    // Import 
    public static boolean isValidExcelFile(MultipartFile file){
        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" );
    }

    public static List<Product> getProductsDataFromExcel(InputStream inputStream){
        List<Product> products = new ArrayList<>();
       try {
           XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
           XSSFSheet sheet = workbook.getSheet("products");
           int rowIndex =0;
           for (Row row : sheet){
               if (rowIndex <= 1){
                   rowIndex++;
                   continue;
               }
               Iterator<Cell> cellIterator = row.iterator();
               int cellIndex = 0;
               Product prodcut = new Product();
               
               while (cellIterator.hasNext()){
                   Cell cell = cellIterator.next();
                   switch (cellIndex){
                       case 0 -> prodcut.setId((long)cell.getNumericCellValue());
                       case 1 -> prodcut.setProductName(cell.getStringCellValue());
                       case 2 -> prodcut.setProductDesc(cell.getStringCellValue());
                       case 3 -> prodcut.setPrice(new BigDecimal(cell.getNumericCellValue()));
                       case 4 -> prodcut.setPrice(new BigDecimal(cell.getNumericCellValue()));
                       case 5 -> prodcut.setDiscount(new BigDecimal(cell.getNumericCellValue()));
                       case 6 -> prodcut.setCreatedDate(Timestamp.valueOf(cell.getStringCellValue()));
                       case 7 -> prodcut.setUpdatedDate(Timestamp.valueOf(cell.getStringCellValue()));
                       //case 8 -> prodcut.setExpiryDate(Timestamp.valueOf(cell.getStringCellValue()));
                       default -> {
                       }
                   }
                   cellIndex++;
               }
               products.add(prodcut);
           }
       } catch (IOException e) {
           e.getStackTrace();
       }
       return products;
   }
}

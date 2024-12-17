package com.hfsolution.feature.stockmanagement.util.stock;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import com.hfsolution.feature.stockmanagement.entity.Product;
import com.hfsolution.feature.stockmanagement.entity.Stock;
import com.hfsolution.feature.stockmanagement.entity.StockHistory;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

public class ExcelUtil {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Stock> stockList;

    // Export
     
    public ExcelUtil(List<Stock> stockList) {
        this.stockList = stockList;
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

    private void createStockHeaderRow(){
        sheet   = workbook.createSheet("stocks");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(20);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        createCell(row, 0, "Stock Information", style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
        font.setFontHeightInPoints((short) 10);

        row = sheet.createRow(1);
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "ID", style);
        createCell(row, 1, "Product ID", style);
        createCell(row, 2, "Qty", style);
        createCell(row, 3, "Percentage", style);
        createCell(row, 4, "CreatedDate", style);
        createCell(row, 5, "UpdatedDate", style);
    }

    private void createStockHistoryHeaderRow(){
        sheet   = workbook.createSheet("stockhistories");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(20);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        createCell(row, 0, "Stock Histories Information", style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
        font.setFontHeightInPoints((short) 10);

        row = sheet.createRow(1);
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "ID", style);
        createCell(row, 1, "Stock ID", style);
        createCell(row, 2, "Staff Fristname", style);
        createCell(row, 3, "Staff Lastname", style);
        createCell(row,4, "Qty", style);
        createCell(row, 5, "Remark", style);
        createCell(row, 6, "CreatedDate", style);
    }

    private void writeStockData(){
        int rowCount = 2;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
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
    private void writeStockHistoryData(){
        int rowCount = 2;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
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
        createStockHeaderRow();
        writeStockData();
        createStockHistoryHeaderRow();
        writeStockHistoryData();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }


    // Import 
    public static boolean isValidExcelFile(MultipartFile file){
        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" );
    }

    public static List<Stock> getStockDataFromExcel(InputStream inputStream){
        List<Stock> stocks = new ArrayList<>();
        Map<Long, Stock> stockMap = new HashMap<>();
       try {
           XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            // Stock History
           XSSFSheet sheet = workbook.getSheet("stocks");
           int rowIndex =0;
           for (Row row : sheet){
               if (rowIndex <= 1){
                   rowIndex++;
                   continue;
               }
               Iterator<Cell> cellIterator = row.iterator();
               int cellIndex = 0;
               Stock Stock = new Stock();
               
               while (cellIterator.hasNext()){
                   Cell cell = cellIterator.next();
                   switch (cellIndex){
                       case 0 -> Stock.setId((long)cell.getNumericCellValue());
                       case 1 -> Stock.setProductId((long)cell.getNumericCellValue());
                       case 2 -> Stock.setQty((long)cell.getNumericCellValue());
                       case 3 -> Stock.setPercentage(new BigDecimal(cell.getNumericCellValue()).doubleValue());
                       case 4 -> Stock.setCreatedDate(Timestamp.valueOf(cell.getStringCellValue()));
                       case 5 -> Stock.setUpdatedDate(Timestamp.valueOf(cell.getStringCellValue()));
                       default -> {
                       }
                   }
                   cellIndex++;
               }

               stockMap.put(Stock.getId(), Stock);
           }
           // Stock History
           XSSFSheet sheet2 = workbook.getSheet("stockhistories");
           int rowIndex2 =0;
           for (Row row : sheet2){
               if (rowIndex2 <= 1){
                rowIndex2++;
                   continue;
               }
               Iterator<Cell> cellIterator = row.iterator();
               int cellIndex = 0;
               StockHistory stockHistory = new StockHistory();
               Long stockId = 0L;
               
               while (cellIterator.hasNext()){
                   Cell cell = cellIterator.next();
                   switch (cellIndex){
                       case 0 -> stockHistory.setId((long)cell.getNumericCellValue());
                       case 1 -> stockId = ((long)cell.getNumericCellValue());
                       case 2 -> stockHistory.setFirstname(cell.getStringCellValue());
                       case 3 -> stockHistory.setLastname(cell.getStringCellValue());
                       case 4 -> stockHistory.setQty((long)cell.getNumericCellValue());
                       case 5 -> stockHistory.setRemark(cell.getStringCellValue());
                       case 6 -> stockHistory.setCreatedDate(Timestamp.valueOf(cell.getStringCellValue()));
                       default -> {
                       }
                   }
                   cellIndex++;
               }
               if(stockMap.containsKey(stockId)) {
                    stockMap.get(stockId).addStockHistory(stockHistory);
               }
           }

       } catch (IOException e) {
           e.getStackTrace();
       }
        stocks = stockMap.values().stream().collect(Collectors.toList());
       return stocks;
   }
}

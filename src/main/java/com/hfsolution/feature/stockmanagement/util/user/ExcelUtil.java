package com.hfsolution.feature.stockmanagement.util.user;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
import com.hfsolution.feature.user.entity.User;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

public class ExcelUtil {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<User> userList;

    public ExcelUtil(List<User> userList) {
        this.userList = userList;
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

    public void exportDataToExcel(HttpServletResponse response) throws IOException {
        createUserHeaderRow();
        writeUserData();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    public static boolean isValidExcelFile(MultipartFile file){
        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" );
    }
   public static List<UserExcelDto> getCustomersDataFromExcel(InputStream inputStream){
        List<UserExcelDto> users = new ArrayList<>();
       try {
           XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
           XSSFSheet sheet = workbook.getSheet("users");
           int rowIndex =0;
           for (Row row : sheet){
               if (rowIndex <= 1){
                   rowIndex++;
                   continue;
               }
               Iterator<Cell> cellIterator = row.iterator();
               int cellIndex = 0;
               UserExcelDto user = new UserExcelDto();
               
               while (cellIterator.hasNext()){
                   Cell cell = cellIterator.next();
                   switch (cellIndex){
                       case 0 -> user.setId((long)cell.getNumericCellValue());
                       case 1 -> user.setFirstname(cell.getStringCellValue());
                       case 2 -> user.setLastname(cell.getStringCellValue());
                       case 3 -> user.setEmail(cell.getStringCellValue());
                       case 4 -> user.setPassword(cell.getStringCellValue());
                       case 5 -> user.setRoleName(cell.getStringCellValue());
                       default -> {
                       }
                   }
                   cellIndex++;
               }
               users.add(user);
           }
       } catch (IOException e) {
           e.getStackTrace();
       }
       return users;
   }
}

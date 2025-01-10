package com.hfsolution.app.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hfsolution.feature.stockmanagement.entity.Stock;

public class AppTools {

    @SuppressWarnings("deprecation")
    public static String convertObjectToJson(Object object) {
        if (object == null) {
            return "";
        }
        var mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
        }
        return "";
    }

    public static String appGetMessage(final String errorCode) {

        var resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasenames("message/error");
        resourceBundleMessageSource.setUseCodeAsDefaultMessage(true);
        resourceBundleMessageSource.setDefaultLocale(new Locale("EN"));
        resourceBundleMessageSource.setDefaultEncoding("UTF-8");
        Locale locale = new Locale("EN");
        String message = resourceBundleMessageSource.getMessage(errorCode,null,locale);
        return message;
        
    }

    


    public static String getCurrentDateString(){
        return new SimpleDateFormat("YYYY/MM/dd HH:mm:ss.SSS").format(new Date());
    }

    public static String getCurrentDateWithFormatString(String pattern){
        return new SimpleDateFormat(pattern).format(new Date());
    }

    public static String formatTimestamp(Timestamp timestamp,String pattern) {
        try {
            if (timestamp == null) return null;
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.format(timestamp);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> Page<T> convertListToPage(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        List<T> subList = list.subList(start, end);

        return new PageImpl<>(subList, pageable, list.size());
    }

    public static List<String> generateMonthAndYear(String startDateStr,String endDateStr){

        // Define a custom formatter to parse the input format
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        // Parse the strings into LocalDateTime objects
        LocalDateTime startDate = LocalDateTime.parse(startDateStr, inputFormatter);
        LocalDateTime endDate = LocalDateTime.parse(endDateStr, inputFormatter);

        // List to store the resulting strings
        List<String> monthYearList = new ArrayList<>();

        // Formatter to generate the desired output format
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM yy");

        // Generate the list of months and years
        while (!startDate.isAfter(endDate)) {
            // Format the current date and add to the list
            monthYearList.add(startDate.format(outputFormatter));
            // Move to the next month
            startDate = startDate.plusMonths(1);
        }

       
        return monthYearList;
    }

    public static Timestamp formatDateStringToTimestamp(String dateString,String pattern) throws ParseException {
        
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        // Parse the date string into a java.util.Date
        java.util.Date parsedDate = dateFormat.parse(dateString);
        // Da
        // Convert to java.sql.Timestamp
        Timestamp timestamp = new Timestamp(parsedDate.getTime());
        // Print the result
        return timestamp;
        
    }

    public static Timestamp formatDateStringToTimestamp(String dateString) throws ParseException {
        // try {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // Parse the date string into a java.util.Date
        java.util.Date parsedDate = dateFormat.parse(dateString);
        // Da
        // Convert to java.sql.Timestamp
        Timestamp timestamp = new Timestamp(parsedDate.getTime());
        // Print the result
        return timestamp;
    }

    public static Object convertValue(String value){
        if (value == null || value.trim().isEmpty()) {
            return ""; // Handle null or empty values
        }

        // Check for boolean
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Boolean.valueOf(value);
        }

        // Check for integer
        if (value.matches("^-?\\d+$")) { // Support for negative numbers
            try {
                return Integer.valueOf(value);
            } catch (NumberFormatException e) {
                // Fall back to Long if value exceeds Integer range
                return Long.valueOf(value);
            }
        }

        // Check for decimal number
        if (value.matches("^-?\\d+\\.\\d+$")) { // Support for negative decimals
            return new BigDecimal(value);
        }

         // Check for ISO datetime (yyyy-MM-dd HH:mm:ss)
         try {
            return Timestamp.valueOf(value); // Expecting 'yyyy-MM-dd HH:mm:ss' format
        } catch (IllegalArgumentException e) {
            // Not a timestamp
        }

        // Check for ISO date (yyyy-MM-dd)
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withResolverStyle(ResolverStyle.STRICT);
            return LocalDate.parse(value, dateFormatter);
        } catch (DateTimeParseException e) {
            // Not an ISO date
        }

       

        // Check for general date-time formats using SimpleDateFormat (customizable)
        String[] dateFormats = { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy", "dd-MM-yyyy" };
        for (String format : dateFormats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false); // Strict date parsing
                return sdf.parse(value);
            } catch (ParseException e) {
                // Not matching this format
            }
        }

        // Default to string if no other type matches
        return value;
    }

    public static HorizontalCellStyleStrategy createCustomStyle() {
         // Header Style
        WriteCellStyle headerStyle = new WriteCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.MEDIUM);
        headerStyle.setBorderLeft(BorderStyle.MEDIUM);
        headerStyle.setBorderRight(BorderStyle.MEDIUM);
        headerStyle.setBorderTop(BorderStyle.MEDIUM);
        headerStyle.setHorizontalAlignment(HorizontalAlignment.CENTER); // Center text horizontally
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center text vertically

        WriteFont headerFont = new WriteFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 8);
        headerStyle.setWriteFont(headerFont);

        // Content Style
        WriteCellStyle contentStyle = new WriteCellStyle();
        contentStyle.setBorderBottom(BorderStyle.THIN);
        contentStyle.setBorderLeft(BorderStyle.THIN);
        contentStyle.setBorderRight(BorderStyle.THIN);
        // contentStyle.setBorderTop(BorderStyle.THIN);
        contentStyle.setHorizontalAlignment(HorizontalAlignment.CENTER); // Center text horizontally
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Center text vertically

        WriteFont contentFont = new WriteFont();
        contentFont.setFontHeightInPoints((short) 10);
        contentFont.setBold(false);
        contentStyle.setWriteFont(contentFont);

        return new HorizontalCellStyleStrategy(headerStyle, contentStyle);
    }

}
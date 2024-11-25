package com.hfsolution.app.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.hfsolution.feature.stockmanagement.entity.Stock;

public class AppTools {

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

    public static Timestamp formatDateStringToTimestamp(String dateString,String pattern) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            // Parse the date string into a java.util.Date
            java.util.Date parsedDate = dateFormat.parse(dateString);
            // Da
            // Convert to java.sql.Timestamp
            Timestamp timestamp = new Timestamp(parsedDate.getTime());
            // Print the result
            return timestamp;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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

}
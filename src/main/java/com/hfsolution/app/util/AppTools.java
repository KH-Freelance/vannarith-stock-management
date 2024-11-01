package com.hfsolution.app.util;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.context.support.ResourceBundleMessageSource;

import com.hfsolution.app.enums.FieldType;

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

    public static Object convertValue(String value, FieldType fieldType) {
        switch (fieldType) {
            case INTEGER:
                return Integer.parseInt(value);
            case DOUBLE:
                return Double.parseDouble(value);
            case LONG:
                return Long.parseLong(value);
            case BOOLEAN:
                return Boolean.parseBoolean(value);
            default:  // STRING or any unsupported types are returned as-is
                return value;
        }
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

}
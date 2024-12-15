package com.hfsolution.app.util;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;
import com.hfsolution.app.exception.AppException;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;

public class CSVHelper<T> {

    private HttpServletResponse response;
    private Class<T> clazz;
    private Map<Integer, String> cachedHeaders = new TreeMap<>();

    public CSVHelper(Class<T> clazz, HttpServletResponse response) {
        this.clazz = clazz;
        this.response = response;
        buildHeaders();
    }

    public CSVHelper(Class<T> clazz) {
        this.clazz = clazz;
        buildHeaders();
    }

    private void buildHeaders() {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(CsvBindByPosition.class)) {
                int position = field.getAnnotation(CsvBindByPosition.class).position();
                cachedHeaders.put(position, field.getName());
            }
        }
    }

    public List<T> parseCsv(MultipartFile file) {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            ColumnPositionMappingStrategy<T> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(clazz);
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withMappingStrategy(strategy)
                    .withSkipLines(1)
                    .withIgnoreEmptyLine(true)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            return csvToBean.parse();
        } catch (Exception e) {
            throw new AppException(FAIL_CODE, e.getMessage());
        }
    }

    public void export(List<T> data, String filename) {
        try {
            response.setContentType("text/csv");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

            try (CSVWriter csvWriter = new CSVWriter(response.getWriter())) {
                csvWriter.writeNext(cachedHeaders.values().toArray(new String[0]));

                ColumnPositionMappingStrategy<T> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(clazz);

                StatefulBeanToCsv<T> writer = new StatefulBeanToCsvBuilder<T>(csvWriter)
                        .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                        .withOrderedResults(true)
                        .withMappingStrategy(strategy)
                        .build(); 
                writer.write(data);
            }
        } catch (Exception e) {
            throw new AppException(FAIL_CODE, e.getMessage());
        }
    }
}

// package com.hfsolution.app.util;

// import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;

// import java.io.BufferedReader;
// import java.io.InputStreamReader;
// import java.io.Reader;
// import java.lang.reflect.Field;
// import java.util.*;
// import java.util.stream.Collectors;

// import com.hfsolution.app.exception.AppException;
// import com.opencsv.CSVWriter;
// import com.opencsv.bean.*;
// import jakarta.servlet.http.HttpServletResponse;
// import org.springframework.http.HttpHeaders;
// import org.springframework.web.multipart.MultipartFile;

// public class CSVHelper<T> {

//     private HttpServletResponse response;
//     private Class<T> clazz;
//     private Map<String, String> cachedHeaders = new LinkedHashMap<>(); // Flattened headers

//     public CSVHelper(Class<T> clazz, HttpServletResponse response) {
//         this.clazz = clazz;
//         this.response = response;
//         buildHeaders(clazz, null);
//     }

//     public CSVHelper(Class<T> clazz) {
//         this.clazz = clazz;
//         buildHeaders(clazz, null);
//     }

//     private void buildHeaders(Class<?> currentClass, String parentPrefix) {
//         for (Field field : currentClass.getDeclaredFields()) {
//             field.setAccessible(true);
//             String columnName = (parentPrefix == null ? "" : parentPrefix + ".") + field.getName();

//             if (field.isAnnotationPresent(CsvBindByPosition.class)) {
//                 cachedHeaders.put(columnName, field.getName());
//             } else if (isRelationship(field)) {
//                 buildHeaders(field.getType(), columnName);
//             }
//         }
//     }

//     private boolean isRelationship(Field field) {
//         return field.isAnnotationPresent(jakarta.persistence.OneToOne.class)
//                 || field.isAnnotationPresent(jakarta.persistence.ManyToOne.class)
//                 || field.isAnnotationPresent(jakarta.persistence.OneToMany.class)
//                 || field.isAnnotationPresent(jakarta.persistence.ManyToMany.class);
//     }

//     public List<T> parseCsv(MultipartFile file) {
//         try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
//             ColumnPositionMappingStrategy<T> strategy = new ColumnPositionMappingStrategy<>();
//             strategy.setType(clazz);
//             CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
//                     .withMappingStrategy(strategy)
//                     .withSkipLines(1)
//                     .withIgnoreEmptyLine(true)
//                     .withIgnoreLeadingWhiteSpace(true)
//                     .build();
//             return csvToBean.parse();
//         } catch (Exception e) {
//             throw new AppException(FAIL_CODE, e.getMessage());
//         }
//     }

//     public void export(List<T> data, String filename) {
//         try {
//             response.setContentType("text/csv");
//             response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
    
//             try (CSVWriter csvWriter = new CSVWriter(response.getWriter())) {
//                 // Write headers
//                 csvWriter.writeNext(cachedHeaders.keySet().toArray(new String[0]));
    
//                 // Write data
//                 for (T record : data) {
//                     List<String> row = new ArrayList<>();
//                     flattenEntity(record, row, null);
//                     csvWriter.writeNext(row.toArray(new String[0]));
//                 }
    
//                 // Ensure data is flushed
//                 csvWriter.flush();
//             }
//         } catch (Exception e) {
//             throw new AppException(FAIL_CODE, e.getMessage());
//         }
//     }

//     private void flattenEntity(Object entity, List<String> row, String parentPrefix) {
//         if (entity == null) {
//             return;
//         }

//         Class<?> clazz = entity.getClass();
//         for (Field field : clazz.getDeclaredFields()) {
//             field.setAccessible(true);
//             String columnName = (parentPrefix == null ? "" : parentPrefix + ".") + field.getName();

//             try {
//                 Object value = field.get(entity);

//                 if (isRelationship(field)) {
//                     flattenEntity(value, row, columnName);
//                 } else {
//                     row.add(value != null ? value.toString() : "");
//                 }
//             } catch (IllegalAccessException e) {
//                 throw new AppException(FAIL_CODE, "Error while extracting field value: " + e.getMessage());
//             }
//         }
//     }
// }

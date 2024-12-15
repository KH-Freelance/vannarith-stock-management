package com.hfsolution.app.util;

import com.hfsolution.app.exception.AppException;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.*;

public class CSVHelperV2<T> {

    private HttpServletResponse response;
    private Map<String, String> headers = new LinkedHashMap<>();
    private List<Class<?>> entityClasses;
    private Class<T> clazz;

    // Constructor to accept multiple related entities (supports OneToMany)
    public CSVHelperV2(HttpServletResponse response, Class<?>... entityClasses) {
        this.response = response;
        this.entityClasses = Arrays.asList(entityClasses);
        buildHeaders();
    }

    public CSVHelperV2(Class<T> clazz) {
        this.clazz = clazz;
        buildHeaders();
    }

    private void buildHeaders() {
        for (Class<?> entityClass : entityClasses) {
            for (Field field : entityClass.getDeclaredFields()) {
                field.setAccessible(true);
                headers.put(entityClass.getSimpleName() + "." + field.getName(), field.getName());
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

    // Export method to handle OneToMany relationships
    public void export(List<Object[]> combinedData, String filename) {
        try {
            response.setContentType("text/csv");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

            try (Writer writer = response.getWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
                // Write headers
                csvWriter.writeNext(headers.keySet().toArray(new String[0]));

                // Write data rows
                for (Object[] rowObjects : combinedData) {
                    List<String> row = new ArrayList<>();
                    for (Object entity : rowObjects) {
                        if (entity instanceof Collection<?>) {
                            // Handle collections (OneToMany) by iterating over related entities
                            for (Object child : (Collection<?>) entity) {
                                addEntityFields(child, row);
                            }
                        } else {
                            addEntityFields(entity, row);
                        }
                    }
                    csvWriter.writeNext(row.toArray(new String[0]));
                }
            }
        } catch (Exception e) {
            throw new AppException("EXPORT_ERROR", "Error exporting data: " + e.getMessage());
        }
    }

    // Helper method to extract field values from an entity
    private void addEntityFields(Object entity, List<String> row) throws IllegalAccessException {
        if (entity != null) {
            for (Field field : entity.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(entity);
                row.add(value != null ? value.toString() : "");
            }
        } else {
            row.add(""); // Add empty value for null entity
        }
    }
}

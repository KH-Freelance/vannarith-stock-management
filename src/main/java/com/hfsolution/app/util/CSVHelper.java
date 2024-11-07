package com.hfsolution.app.util;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hfsolution.app.exception.AppException;
import com.hfsolution.app.exception.CsvException;
import com.opencsv.CSVReader;
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

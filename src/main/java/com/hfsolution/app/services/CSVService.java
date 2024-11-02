package com.hfsolution.app.services;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hfsolution.app.exception.CsvException;
import com.hfsolution.app.util.InfoGenerator;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CSVService<T> {

    private final HttpServletResponse response;

    @SuppressWarnings("resource")
    public List<String[]> readCSV(MultipartFile file) {
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try{
            Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
		    CSVReader csvReader = new CSVReader(reader);
        return csvReader.readAll();
        } catch (Exception e) {
            throw new CsvException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
        }
	}

    public List<T> parseCsv(MultipartFile file, Class<T> clazz) {
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try(Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            ColumnPositionMappingStrategy<T> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(clazz);
            CsvToBean<T> csvToBean =
                    new CsvToBeanBuilder<T>(reader)
                            .withMappingStrategy(strategy)
                            .withIgnoreEmptyLine(true)
                            .withIgnoreLeadingWhiteSpace(true)
                            .build();
            return csvToBean.parse()
                    .stream()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CsvException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
        }
    }

    public void export(List<T> data, Class<T> clazz, String filename) {
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try{
            response.setContentType("text/csv");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "");
            ColumnPositionMappingStrategy<T> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(clazz);
            // strategy.setColumnMapping(orderColumn);      
            StatefulBeanToCsv<T> writer = new StatefulBeanToCsvBuilder<T>(response.getWriter())
            .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
            .withOrderedResults(true)
            .withMappingStrategy(strategy)
            .build();
            writer.write(data);
            
        } catch (Exception e) {
            throw new CsvException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
        }
    }

    public void export(List<T> data, String filename) {
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try{
            response.setContentType("text/csv");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "");
            StatefulBeanToCsv<T> writer = new StatefulBeanToCsvBuilder<T>(response.getWriter())
            .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
            .withOrderedResults(true)
            .build();
            writer.write(data);
        } catch (Exception e) {
            throw new CsvException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
        }
    }
}

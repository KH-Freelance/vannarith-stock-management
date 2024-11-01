package com.hfsolution.app.services;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;

import java.io.FileReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hfsolution.app.exception.CsvException;
import com.hfsolution.app.util.InfoGenerator;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CSVService<T> {
    private final HttpServletResponse response;
    private final String FILE_PATH = "./";
    public void readCSV(MultipartFile file) {
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try{
		CSVReader reader = new CSVReader(new FileReader(FILE_PATH + file.getOriginalFilename()));

		List<String[]> rows = reader.readAll();
		// List<Files> filesList = new ArrayList<>();

		for (String[] row : rows) {
			System.out.println(row[0] + "," + row[1] + "," + row[2] + "," + row[3]);

		// 	Files files = new Files();
		// 	files.setId(row[0]);
		// 	files.setDesc1(row[1]);
		// 	files.setDesc2(row[2]);
		// 	files.setVal1(row[3]);
		// 	files.setVal2(row[4]);
		// 	files.setVal3(row[5]);
		// 	files.setVal4(row[6]);
		// 	files.setVal5(row[7]);
		// 	files.setVal6(row[8]);
		// 	files.setVal7(row[9]);

		// 	filesList.add(files);
		}

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

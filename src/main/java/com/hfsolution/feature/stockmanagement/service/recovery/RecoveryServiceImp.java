package com.hfsolution.feature.stockmanagement.service.recovery;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.app.util.AppTools;
import jakarta.transaction.Transactional;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;

@Service
public class RecoveryServiceImp implements RecoveryService{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String FILE_STORAGE_PATH = "./uploaded_files/";
    private final String FILE_NAME= "backup-";

    @Override
    @Transactional
    public Object recovery(MultipartFile file) throws IOException {
        try {
            // Ensure the file is not empty
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Uploaded file is empty");
            }

            // Specify the base directory for file storage
            String directoryPath = "/private/var/folders/4z/f3kv9c41067b4rcf7p_n1jvw0000gp/T/tomcat.8080.5956231277216322288/work/Tomcat/localhost/ROOT/uploaded_files/";

            // Ensure the directory exists, create it if it doesn't
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                if (directory.mkdirs()) {
                    System.out.println("Directory created successfully: " + directoryPath);
                } else {
                    throw new IOException("Failed to create directory: " + directoryPath);
                }
            }

            // Create a temporary file to store the uploaded content
            File tempFile = new File(directoryPath + "import-" + System.currentTimeMillis() + ".xlsx");
            file.transferTo(tempFile);

            // Use EasyExcel to read the Excel file
            EasyExcel.read(tempFile, new ExcelDataListener()).sheet().doRead();

            // Return success response
            SuccessResponse<Object> response = new SuccessResponse<>();
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setMsg("Data import completed successfully");
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Data import failed due to an error", e);
        }
    }

    // ExcelDataListener to handle reading rows from the Excel file
    private class ExcelDataListener extends com.alibaba.excel.event.AnalysisEventListener<Map<Integer, String>> {
        
        @Override
        public void invoke(Map<Integer, String> data, AnalysisContext context) {
            String sheetName = context.readSheetHolder().getSheetName();
            // if (sheetName.equalsIgnoreCase("Summary")) return;
            // Process data based on the sheet name, assuming sheet names correspond to table names
            List<String> columnNames = new ArrayList<>();
            List<Object> rowData = new ArrayList<>();

            for (Map.Entry<Integer, String> entry : data.entrySet()) {
                columnNames.add("column" + (entry.getKey() + 1)); // Assuming columns are named column1, column2, etc.
                rowData.add(entry.getValue());
            }
            
            // Insert data into the corresponding table
            insertDataIntoTable(sheetName, columnNames, rowData);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // Can be used to perform any post-processing
        }
    }

    // Method to insert the data into the database table
    private void insertDataIntoTable(String tableName, List<String> columnNames, List<Object> rowData) {
        // Prepare the SQL query for insertion
        String columns = String.join(", ", columnNames);
        String placeholders = String.join(", ", columnNames.stream().map(c -> "?").toArray(String[]::new));
        String query = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";
        
        // Insert the data into the table
        jdbcTemplate.update(query, rowData.toArray());
    }
    


    @Override
    @Transactional
    public Object backup() throws IOException {
        try {
            // Step 1: Get all table names
            List<String> tableNames = jdbcTemplate.queryForList(
                    "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'",
                    String.class);

            // Ensure the output directory exists
            Path savePath = Paths.get(FILE_STORAGE_PATH);
            if (!Files.exists(savePath)) {
                Files.createDirectories(savePath);
            }

            // Step 2: Prepare the output Excel file
            File outputFile = new File(FILE_STORAGE_PATH + FILE_NAME + AppTools.getCurrentDateWithFormatString("YYYY-MM-dd") + ".xlsx");
            try (ExcelWriter excelWriter = EasyExcel.write(outputFile)
                    .registerConverter(new TimestampConverter())
                    .autoCloseStream(true)
                    .build()) {
                            
                // Step 3: Write the summary sheet
                WriteSheet summarySheet = EasyExcel.writerSheet("Summary")
                        .head(generateSummaryHead())
                        .build();
                excelWriter.write(generateSummaryData(tableNames), summarySheet);

                // Step 4: Query each table asynchronously
                List<CompletableFuture<Void>> futures = new ArrayList<>();

                for (String tableName : tableNames) {
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        try {
                            List<Map<String, Object>> rows = queryTableData(tableName);
                            if (!rows.isEmpty()) {
                                synchronized (excelWriter) {
                                    WriteSheet tableSheet = EasyExcel.writerSheet(tableName)
                                            .head(generateDynamicHead(rows))
                                            .build();
                                    excelWriter.write(generateDynamicData(rows), tableSheet);
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("Failed to process table: " + tableName);
                            e.printStackTrace();
                        }
                    });
                    futures.add(future);
                }

                // Wait for all futures to complete
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            }

            System.out.println("Export completed successfully: " + outputFile.getAbsolutePath());

            // Build the success response
            SuccessResponse<Object> response = new SuccessResponse<>();
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setMsg("Backup completed successfully");
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Backup failed due to an error", e);
        }
    }

    @Override
    public ResponseEntity<Resource>  getExcelData() throws URISyntaxException, IOException {
       try {
            Path savePath = Paths.get(FILE_STORAGE_PATH);
            if (!Files.exists(savePath)) {
                Files.createDirectories(savePath); // Ensure the directory is created if it doesn't exist.
            }

            Path filePath = savePath.resolve(FILE_NAME+AppTools.getCurrentDateWithFormatString("YYYY-MM-dd")+".xlsx");

           
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                // Set headers to indicate the file type and disposition
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    private List<Map<String, Object>> queryTableData(String tableName) {
        String query = "SELECT * FROM " + tableName;
        return jdbcTemplate.queryForList(query);
    }

    private List<List<String>> generateSummaryHead() {
        List<List<String>> headers = new ArrayList<>();
        headers.add(List.of("Table Name"));
        return headers;
    }

    private List<List<Object>> generateSummaryData(List<String> tableNames) {
        List<List<Object>> data = new ArrayList<>();
        for (String tableName : tableNames) {
            data.add(List.of(tableName));
        }
        return data;
    }

    private List<List<String>> generateDynamicHead(List<Map<String, Object>> rows) {
        List<List<String>> headers = new ArrayList<>();
        if (!rows.isEmpty()) {
            for (String columnName : rows.get(0).keySet()) {
                headers.add(List.of(columnName));
            }
        }
        return headers;
    }

    private List<List<Object>> generateDynamicData(List<Map<String, Object>> rows) {
        List<List<Object>> data = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            List<Object> rowData = new ArrayList<>(row.values());
            data.add(rowData);
        }
        return data;
    }

    // Custom TimestampConverter to handle java.sql.Timestamp fields
    public static class TimestampConverter implements Converter<Timestamp> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public Class<?> supportJavaTypeKey() {
            return Timestamp.class;
        }

        @Override
        public WriteCellData<?> convertToExcelData(WriteConverterContext<Timestamp> context) {
            Timestamp timestamp = context.getValue();
            if (timestamp != null) {
                return new WriteCellData<>(timestamp.toLocalDateTime().format(FORMATTER));
            }
            return new WriteCellData<>("");
        }
    }
   

}

    


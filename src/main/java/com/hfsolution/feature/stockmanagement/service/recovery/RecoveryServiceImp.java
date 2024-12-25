package com.hfsolution.feature.stockmanagement.service.recovery;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.app.util.AppTools;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.transaction.Transactional;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;

@Service
public class RecoveryServiceImp implements RecoveryService{

    private static final int BATCH_SIZE = 800;  // Define batch size here
    
    public RecoveryServiceImp(@Qualifier("postgressDataSource") DataSource dataSource,JdbcTemplate jdbcTemplate){
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private JdbcTemplate jdbcTemplate;
    private DataSource dataSource;
    private final String FILE_STORAGE_PATH = "/uploaded_files/";
    private final String FILE_NAME= "backup-";
    @Value("${backup.restore.temp.dir:/tmp}") // Specify temp directory for storing the file
    private String tempDir;

    @Override
    @Transactional
    public Object recovery(MultipartFile file) throws IOException {
        CompletableFuture<Map<String, List<String>>> uniqueColumns = getUniqueColumnsAsync();
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        String directoryPath = System.getProperty("user.dir")+FILE_STORAGE_PATH;
        File directory = new File(directoryPath);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Failed to create directory: " + directoryPath);
        }

        File tempFile = new File(directoryPath + "/import-" + System.currentTimeMillis() + ".xlsx");
        file.transferTo(tempFile);
        try {
            // Use EasyExcel to read all sheets
            try (ExcelReader excelReader = EasyExcel.read(tempFile, new ExcelDataListener(uniqueColumns.get())).headRowNumber(0).build()) {

                List<ReadSheet> sheets = excelReader.excelExecutor().sheetList();
                
                for (ReadSheet sheet : sheets) {
                    
                    // excelReader.read(sheet);
                    
                    if (!"Summary".equalsIgnoreCase(sheet.getSheetName())) {
                        excelReader.read(sheet);
                    }
                }
            }

            SuccessResponse<Object> response = new SuccessResponse<>();
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setMsg("Data import completed successfully");
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Data import failed due to an error", e);
        }finally{
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }


    private class ExcelDataListener extends com.alibaba.excel.event.AnalysisEventListener<Map<Integer, String>> {

        private Map<String, List<String>> columnNames = new HashMap<>();
        private Map<String, List<Object>> rowDatas = new HashMap<>();
        private Map<String, List<String>> tableUquineKey = new HashMap<>();

        public ExcelDataListener (Map<String, List<String>> tableUquineKey){
            this.tableUquineKey = tableUquineKey;
        }

        @Override
        public void invoke(Map<Integer, String> data, AnalysisContext context) {
            if (context.readRowHolder().getRowIndex() == 0) {
                // Process the header row
                String sheetName = context.readSheetHolder().getSheetName();
                columnNames.computeIfAbsent(sheetName, k -> new ArrayList<>()).addAll(data.values().stream().map(dt->dt).toList());
                // columnNames = new ArrayList<>(data.values());
                if (columnNames.isEmpty()) {
                    throw new IllegalStateException("Header row is missing or empty. Cannot determine column names.");
                }
            } else {
                if (columnNames.isEmpty()) {
                    throw new IllegalStateException("Header row is missing. Cannot process data rows.");
                }

                if (context.readSheetHolder().getSheetName().equalsIgnoreCase("customer")) {

                    System.out.println("sss");
                }
                List<Object> rowData = new ArrayList<>(data.values().stream().map(dt -> AppTools.convertValue(dt)).toList());

                // // Accumulate rows for the current sheet
                String sheetName = context.readSheetHolder().getSheetName();
                rowDatas.computeIfAbsent(sheetName, k -> new ArrayList<>()).add(rowData);
            }
        }
    
        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // Perform batch insert for each sheet after processing all rows
            for (Map.Entry<String, List<Object>> entry : rowDatas.entrySet()) {
                String tableName = entry.getKey();
                List<Object> rows = entry.getValue();

                // Split the rows into smaller batches if necessary
                for (int i = 0; i < rows.size(); i += BATCH_SIZE) {
                    int end = Math.min(i + BATCH_SIZE, rows.size());
                    List<Object> batch = rows.subList(i, end);

                    // Insert the batch into the database
                    insertDataIntoTable(tableName, columnNames.get(tableName), batch, tableUquineKey.get(tableName));
                }
                rowDatas.remove(entry.getKey());
            }
        }
    }
    
    

    private void insertDataIntoTable(String tableName, List<String> columnNames, List<Object> rowDatas, List<String> uniqueColumns) {
        if (columnNames.isEmpty() || rowDatas.isEmpty()) {
            throw new IllegalArgumentException("Column names or row data cannot be empty");
        }
    
        // Step 1: Dynamically get the unique columns from the database schema
        // List<String> uniqueColumns = getUniqueColumns(tableName);
        if (uniqueColumns.isEmpty()) {
            throw new IllegalArgumentException("No unique columns found for table " + tableName);
        }
    
        // Step 2: Prepare the SQL query for batch insert or upsert
        String columns = String.join(", ", columnNames);
        String placeholders = String.join(", ", columnNames.stream().map(c -> "?").toArray(String[]::new));
        
        // Build the update set for all columns
        String updateSet = String.join(", ", columnNames.stream().map(c -> c + " = EXCLUDED." + c).toArray(String[]::new));
    
        // Build the conflict target from unique columns
        String conflictTarget = String.join(", ", uniqueColumns);
        
        String query = String.format(
            "INSERT INTO %s (%s) VALUES (%s) ON CONFLICT (%s) DO UPDATE SET %s",
            tableName, columns, placeholders, conflictTarget, updateSet
        );
    
        // Step 3: Accumulate rows for batch insert
        List<Object[]> batchArgs = new ArrayList<>();
       
        
        // Collect data for batch insert
        for (Object  rowData : rowDatas) {
            if (rowData instanceof ArrayList) {
                @SuppressWarnings("unchecked")
                ArrayList<Object> re = (ArrayList<Object>) rowData;
                Object[] r = re.toArray();
                batchArgs.add(r);
            }
        }
        
        // Perform batch insert
        try {
            jdbcTemplate.batchUpdate(query, batchArgs);
            System.out.println("Batch insert successfully for table: " + tableName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upsert data into table: " + tableName, e);
        }
    }
    
    

    

    private CompletableFuture<Map<String, List<String>>> getUniqueColumnsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, List<String>> tableColumnMap = new HashMap<>();

            List<String> tableNames = new ArrayList<>();
            String query = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'"; // Adjust for your schema if necessary

            try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) {
                    tableNames.add(resultSet.getString("table_name"));
                }
            } catch (Exception e) {
                throw new RuntimeException("Error fetching table names from the database", e);
            }
    
            // Construct query for multiple table names
            String tables = String.join(", ", tableNames.stream().map(t -> "'" + t + "'").toArray(String[]::new));
            String query2 = "SELECT table_name, column_name FROM information_schema.key_column_usage " +
                           "WHERE constraint_name LIKE '%pkey%' AND table_name IN (" + tables + ")";
    
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query2)) {
    
                while (resultSet.next()) {
                    String tableName = resultSet.getString("table_name");
                    String columnName = resultSet.getString("column_name");
    
                    // Add the column to the corresponding table's list
                    tableColumnMap.computeIfAbsent(tableName, k -> new ArrayList<>()).add(columnName);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error fetching unique columns for tables: " + tableNames, e);
            }
    
            return tableColumnMap;
        });
    }
    
    
    private List<String> getUniqueColumns(String tableName) {
        List<String> uniqueColumns = new ArrayList<>();
        
        // Query to get the primary key columns
        String query = "SELECT column_name FROM information_schema.key_column_usage WHERE (constraint_name like '%pkey%') and table_name = '"+tableName+"'";    
        if(tableName.equalsIgnoreCase("permissions")){
            System.out.println("tableName");
        }
        
        
        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                uniqueColumns.add(resultSet.getString("column_name"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching unique columns for tables: " + tableName, e);
        }
        
        return uniqueColumns;
    }


    @Override
    @Transactional
    public Object backup(String filename) throws IOException {
        try {
            // Step 1: Get all table names
            List<String> tableNames = jdbcTemplate.queryForList(
                    "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'",
                    String.class);

            // Ensure the output directory exists
            Path savePath = Paths.get( System.getProperty("user.dir")+FILE_STORAGE_PATH);
            if (!Files.exists(savePath)) {
                Files.createDirectories(savePath);
            }

            // Step 2: Prepare the output Excel file
            File outputFile = new File(System.getProperty("user.dir")+FILE_STORAGE_PATH + filename + ".xlsx");
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
    public ResponseEntity<Resource>  getExcelData(String filename) throws URISyntaxException, IOException {
       try {
            Path savePath = Paths.get( System.getProperty("user.dir")+FILE_STORAGE_PATH);
            if (!Files.exists(savePath)) {
                Files.createDirectories(savePath); // Ensure the directory is created if it doesn't exist.
            }

            Path filePath = savePath.resolve(filename+".xlsx");

           
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



    @Override
    @Async
    public CompletableFuture<Void> backupV2() throws URISyntaxException, IOException {
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            String jdbcUrl = hikariDataSource.getJdbcUrl();
            URI jdbcUri = new URI(jdbcUrl.substring(5));  // Skip "jdbc:" part of the URL
            
            String host = jdbcUri.getHost();
            int port = jdbcUri.getPort();
            String database = jdbcUri.getPath().substring(1);  // Remove leading '/'
            String username = hikariDataSource.getUsername();
            String password = hikariDataSource.getPassword();
            // Prepare the pg_dump command
            String command = String.format(
                    "pg_dump -h %s -p %d -U %s -F c -b -v -f %sbackup.dump %s",
                    host, port, username,System.getProperty("user.dir")+FILE_STORAGE_PATH, database);
            // Set the password in the environment variable for pg_dump
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.environment().put("PGPASSWORD", password);
            // Execute the backup command
            Process process = processBuilder.command(command.split(" ")).start();
            // Wait for the process to complete
            try {
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    System.out.println("Backup successful");
                } else {
                    System.err.println("Backup failed");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // Clean up
                processBuilder.environment().remove("PGPASSWORD");
            }
            }else{
                System.out.println("Not HikariDataSource");
            }
            return CompletableFuture.completedFuture(null);
        }


        @Override
        @Async
        public CompletableFuture<Void> recoveryV2(MultipartFile file) throws URISyntaxException, IOException {
            if (dataSource instanceof HikariDataSource) {
                HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
                String jdbcUrl = hikariDataSource.getJdbcUrl();
                URI jdbcUri = new URI(jdbcUrl.substring(5));  // Skip "jdbc:" part of the URL

                String host = jdbcUri.getHost();
                int port = jdbcUri.getPort();
                String database = jdbcUri.getPath().substring(1);  // Remove leading '/'
                String username = hikariDataSource.getUsername();
                String password = hikariDataSource.getPassword();

                String backupFilePath = System.getProperty("user.dir") + FILE_STORAGE_PATH + "backup.dump"; // Adjust the path as needed

                // Prepare the pg_restore command with --clean and --if-exists options
                String command = String.format(
                        "pg_restore --clean --if-exists -h %s -p %d -U %s -d %s -v %s",
                        host, port, username, database, backupFilePath);

                // Set the password in the environment variable for pg_restore
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.environment().put("PGPASSWORD", password);
                // Execute the restore command
                Process process = processBuilder.command(command.split(" ")).start();

                // Wait for the process to complete
                try {
                    int exitCode = process.waitFor();
                    if (exitCode == 0) {
                        System.out.println("Restore successful");
                    } else {
                        System.err.println("Restore failed");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // Clean up
                    processBuilder.environment().remove("PGPASSWORD");
                }
            } else {
                System.out.println("Not HikariDataSource");
            }
            return CompletableFuture.completedFuture(null);
        }


    @Override
    public ResponseEntity<Resource>  getBackupFile() throws URISyntaxException, IOException {
       try {
            Path savePath = Paths.get(System.getProperty("user.dir")+FILE_STORAGE_PATH);
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

     
        
   

}

    


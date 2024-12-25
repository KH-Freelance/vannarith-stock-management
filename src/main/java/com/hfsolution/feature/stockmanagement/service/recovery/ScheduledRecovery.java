package com.hfsolution.feature.stockmanagement.service.recovery;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.hfsolution.app.external.telegram.TelegramRestClientConsumer;
import com.hfsolution.feature.stockmanagement.util.CustomMultipartFile;
import io.jsonwebtoken.io.IOException;

@Component
public class ScheduledRecovery {


    @Autowired
    private  TelegramRestClientConsumer telegramRestClientConsumer;


    @Autowired
    private  JdbcTemplate jdbcTemplate;
    private final String FILE_STORAGE_PATH = "/uploaded_files/";


    // private final String TOKEN="7524240463:AAFokw3C3D5lQ6dYV806XMuDTZd1pA0X_Ys";
    private final String CHAT_ID="-4553450364";

    private final String UPLOAD_DIR="./uploads/";
    // @Scheduled(cron = "0 0 2 * * ?" )
    @Scheduled(fixedDelay = 1000000 )
    public void backupDatabaseWithDateRange() throws InterruptedException, ExecutionException, java.io.IOException {

        // String startDate = "2024-12-01";
        // String endDate = "2024-12-31";
        // System.out.println("Hello");
        // File file = new File(UPLOAD_DIR + "stock-backup.sql");
        // if (!file.exists()) {
        //     throw new IOException("File not found: " + UPLOAD_DIR + "stock-backup.sql");
        // }

        
        // List<String> tables = jdbcTemplate.queryForList(
        // "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'", 
        // String.class);

        // tables.forEach(table->{
        //     System.out.println(table);
        // });

        // tables.forEach(table -> {
        //     // String query = String.format("COPY (SELECT * FROM %s WHERE created_date BETWEEN '%s' AND '%s') TO './upload/%s_backup.csv' DELIMITER ',' CSV HEADER;",table, startDate, endDate, table);
        //     String query = String.format("COPY (SELECT * FROM %s ) TO './upload/%s_backup.csv' DELIMITER ',' CSV HEADER;",table, table);
    
        //     jdbcTemplate.execute(query);
        // });


        // // Create a custom MultipartFile
        // MultipartFile multipartFile = new CustomMultipartFile(file, "application/octet-stream");
        // telegramRestClientConsumer.sendFileToTelegram(multipartFile, CHAT_ID);
    
    }

    @Scheduled(cron = "0 0 2 * * ?") // Runs daily at 2:00 AM
    public void cleanUpTempFiles() {
        File tempDir = new File(System.getProperty("user.dir")+FILE_STORAGE_PATH);
        for (File file : tempDir.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".xlsx") && 
                file.lastModified() < System.currentTimeMillis() - 3600000) {
                file.delete();
            }
        }
    }
    
}

package com.hfsolution.feature.stockmanagement.service.recovery;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;

@Service
public class RecoveryServiceImp implements RecoveryService{

    DataSource dataSource;

    public RecoveryServiceImp(@Qualifier("postgressDataSource") DataSource dataSource){
        this.dataSource = dataSource;
    }

    @Override
    @Async
    public CompletableFuture<Void> backup() throws URISyntaxException, IOException {


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
                    "pg_dump -h %s -p %d -U %s -F c -b -v -f ./backup.dump %s",
                    host, port, username, database);

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
    public Object recovery(MultipartFile file) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'recovery'");
    }
    
}

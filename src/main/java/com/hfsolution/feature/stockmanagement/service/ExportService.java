package com.hfsolution.feature.stockmanagement.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.metadata.data.WriteCellData;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExportService {

    private final JdbcTemplate jdbcTemplate;

    public ExportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void exportAllTablesToExcel(String databaseName, String outputFilePath) {
        try {
            // Step 1: Get all table names
            List<String> tableNames = jdbcTemplate.queryForList(
                    "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'",
                    String.class);

            // Step 2: Prepare the output Excel file
            File outputFile = new File(outputFilePath);
            try (ExcelWriter excelWriter = EasyExcel.write(outputFile)
                    .registerConverter(new TimestampConverter())
                    .autoCloseStream(true)
                    .build()) {

                // Step 3: Write the summary sheet
                WriteSheet summarySheet = EasyExcel.writerSheet("Summary")
                        .head(generateSummaryHead())
                        .build();
                excelWriter.write(generateSummaryData(tableNames), summarySheet);

                // Step 4: Write each table's data to a separate sheet
                for (String tableName : tableNames) {
                    List<Map<String, Object>> rows = queryTableData(tableName);
                    if (!rows.isEmpty()) {
                        WriteSheet tableSheet = EasyExcel.writerSheet(tableName)
                                .head(generateDynamicHead(rows))
                                .build();
                        excelWriter.write(generateDynamicData(rows), tableSheet);
                    }
                }
            }

            System.out.println("Export completed successfully: " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
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

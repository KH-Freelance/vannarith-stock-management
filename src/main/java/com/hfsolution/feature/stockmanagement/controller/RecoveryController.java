package com.hfsolution.feature.stockmanagement.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;
import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.feature.stockmanagement.service.ExportService;
import com.hfsolution.feature.stockmanagement.service.recovery.RecoveryService;

@RestController
@RequestMapping("/postgres")
public class RecoveryController {

    @Autowired
    private RecoveryService recoveryService;

    @Autowired
    private ExportService exportService;

    // @PostMapping(value = "/backup")
    // private Object backup() throws URISyntaxException, IOException{
    //     recoveryService.backupV2();
    //     SuccessResponse<Object> response = new SuccessResponse<>();
    //     response.setStatus(SUCCESS);
    //     response.setMsg("Backup in progress");
    //     response.setCode(SUCCESS_CODE);
    //     return response;
    // }
    // @PostMapping(value = "/recovery")
    // private Object recovery(@RequestPart("file") MultipartFile file) throws URISyntaxException, IOException{
    //     recoveryService.recoveryV2(file);
    //     SuccessResponse<Object> response = new SuccessResponse<>();
    //     response.setStatus(SUCCESS);
    //     response.setMsg("Restore in progress");
    //     response.setCode(SUCCESS_CODE);
    //     return response;
    // }
    @PostMapping(value = "/backup")
    private Object backup() throws URISyntaxException, IOException{
        return recoveryService.backup();
    }

    @PostMapping(value = "/recovery")
    private Object recovery(@RequestPart("file") MultipartFile file) throws URISyntaxException, IOException{
        return recoveryService.recovery(file);
    }

    @GetMapping(value = "/get-excel-data")
    private ResponseEntity<Resource> getExcelData() throws URISyntaxException, IOException{
        return recoveryService.getExcelData();
    }
    
}

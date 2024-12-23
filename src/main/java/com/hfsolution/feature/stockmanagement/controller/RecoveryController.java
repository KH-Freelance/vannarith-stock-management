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

import com.hfsolution.feature.stockmanagement.service.recovery.RecoveryService;

@RestController
@RequestMapping("/postgres")
public class RecoveryController {

    @Autowired
    private RecoveryService recoveryService;

    @PostMapping(value = "/backup")
    private Object backup() throws URISyntaxException, IOException{
        return recoveryService.backup();
    }

    @GetMapping(value = "/get-excel-data")
    private ResponseEntity<Resource> getExcelData() throws URISyntaxException, IOException{
        return recoveryService.getExcelData();
    }
    
}

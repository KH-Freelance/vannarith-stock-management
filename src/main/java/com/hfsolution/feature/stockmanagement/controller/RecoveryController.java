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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;
import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.app.util.AppTools;
import com.hfsolution.feature.stockmanagement.service.ExportService;
import com.hfsolution.feature.stockmanagement.service.recovery.RecoveryService;

import jakarta.servlet.http.HttpServletRequest;

import static com.hfsolution.app.constant.AppConstant.*;

@RestController
@RequestMapping("/data")
public class RecoveryController {

    @Autowired
    private RecoveryService recoveryService;
    @Autowired
    private HttpServletRequest httpServletRequest;

    @PostMapping(value = "/backup/{filename}")
    private Object backup(@PathVariable String filename) throws URISyntaxException, IOException{
        httpServletRequest.setAttribute(REQ_INFO,"Pay Name = " +filename);
        return recoveryService.backup(filename);
    }

    @PostMapping(value = "/recovery")
    private Object recovery(@RequestPart("file") MultipartFile file) throws URISyntaxException, IOException{
        return recoveryService.recovery(file);
    }

    @GetMapping(value = "/download/{filename}")
    private ResponseEntity<Resource> getExcelData(@PathVariable String filename) throws URISyntaxException, IOException{
        httpServletRequest.setAttribute(REQ_INFO,"Pay Name = " +filename);
        return recoveryService.getExcelData(filename);
    }
    
}

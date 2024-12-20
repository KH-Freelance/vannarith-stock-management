package com.hfsolution.feature.stockmanagement.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
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
        recoveryService.backup();
        return "success";
    }
    
}

package com.hfsolution.feature.stockmanagement.service.recovery;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface  RecoveryService {
    public Object backupV2()throws URISyntaxException,IOException;
    public Object backup()throws URISyntaxException,IOException;
    public ResponseEntity<Resource>  getExcelData()throws URISyntaxException,IOException;
    public ResponseEntity<Resource>  getBackupFile()throws URISyntaxException,IOException;
    public Object recoveryV2(MultipartFile file) throws URISyntaxException, IOException ;
    public Object recovery(MultipartFile file) throws IOException;
}

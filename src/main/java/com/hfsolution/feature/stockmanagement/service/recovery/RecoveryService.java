package com.hfsolution.feature.stockmanagement.service.recovery;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface  RecoveryService {
    public CompletableFuture<Void> backup()throws URISyntaxException,IOException;
    public Object recovery(MultipartFile file);
}

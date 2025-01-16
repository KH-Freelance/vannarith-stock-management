package com.hfsolution.feature.stockmanagement.service.configuration;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.hfsolution.feature.stockmanagement.dto.request.configuration.ConfigurationRequest;


@Service
public interface ConfigurationService {

    Object search(String q, int pageNo, int pageSize, Direction sort, String sortByColum);
    Object updateConfiguration(Long id , ConfigurationRequest configurationRequest);
    
}

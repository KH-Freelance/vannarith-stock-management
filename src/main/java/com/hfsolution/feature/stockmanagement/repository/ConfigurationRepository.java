package com.hfsolution.feature.stockmanagement.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.hfsolution.app.repository.IBaseRepository;
import com.hfsolution.feature.stockmanagement.entity.Configuration;

public interface ConfigurationRepository extends IBaseRepository<Configuration,Long>, JpaSpecificationExecutor<Configuration>{

    List<Configuration> findAllByActiveTrue();
    Configuration findByFunctionTypeAndActiveTrue(String functionType);
    List<Configuration> findByFunctionTypeInAndActiveTrue(List<String> functionTypes);

} 

package com.hfsolution.app.config.database.context;

import com.hfsolution.app.enums.EDataSourceType;

public interface IDataSourceContextHolder {

  EDataSourceType getBranchContext();

  void setBranchContext(EDataSourceType dataSourceType);

  void clearBranchContext();

  EDataSourceType getPrimaryDataSourceType();

  void setPrimaryDataSourceType(EDataSourceType dataSourceType);
  
}


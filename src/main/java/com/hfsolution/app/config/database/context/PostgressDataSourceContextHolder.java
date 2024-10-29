package com.hfsolution.app.config.database.context;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.hfsolution.app.enums.EDataSourceType;


@Component("postgressDataSourceContextHolder")
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PostgressDataSourceContextHolder implements IDataSourceContextHolder {

  private static final ThreadLocal<EDataSourceType> threadLocal = new ThreadLocal<>();
  private EDataSourceType primaryDataSourceType = EDataSourceType.PRIMARY;

  @Override
  public EDataSourceType getBranchContext() {
    return threadLocal.get();
  }

  @Override
  public void setBranchContext(EDataSourceType dataSourceType) {
    threadLocal.set(dataSourceType);
  }

  @Override
  public void clearBranchContext() {
    threadLocal.remove();
  }

  @Override
  public EDataSourceType getPrimaryDataSourceType() {
    return primaryDataSourceType;
  }

  @Override
  public void setPrimaryDataSourceType(EDataSourceType dataSourceType) {
    primaryDataSourceType = dataSourceType;
  }
}

package com.hfsolution.app.dao;


import static com.hfsolution.app.constant.AppResponseStatus.*;
import static com.hfsolution.app.constant.AppResponseCode.*;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import com.hfsolution.app.config.database.context.IDataSourceContextHolder;
import com.hfsolution.app.dto.BaseEntityResponseDto;
import com.hfsolution.app.enums.EDataSourceType;
import com.hfsolution.app.exception.DatabaseException;
import com.hfsolution.app.interfaces.IBaseDBDao;
import com.hfsolution.app.repository.IBaseRepository;
import lombok.Cleanup;

public abstract class BaseDBDao<T, ID> implements IBaseDBDao<T, ID> {

  protected IBaseRepository<T, ID> repository;
  protected IDataSourceContextHolder dataSourceDCContextHolder;

  public BaseDBDao(IBaseRepository<T, ID> repository,IDataSourceContextHolder dataSourceDCContextHolder) {
    this.repository = repository;
    this.dataSourceDCContextHolder = dataSourceDCContextHolder;
  }

  @Override
  public BaseEntityResponseDto<T> saveEntity(T entity) {
    // String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    

    try {
      switchDataSource(EDataSourceType.PRIMARY);
      T savedEntity = repository.saveEntity(entity);
      

      var entityDto = new BaseEntityResponseDto<T>();
      entityDto.setStatus(SUCCESS);
      entityDto.setEntity(savedEntity);
      return entityDto;

    } catch (Exception e) {
      
      
      throw new DatabaseException(FAIL_CODE, e.getMessage());
    }
  }

  @Override
  @Async("jpaExecutor")
  public CompletableFuture<BaseEntityResponseDto<T>> saveEntityAsync(T entity) {
    // String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    

    try {
      switchDataSource(EDataSourceType.PRIMARY);
      T savedEntity = repository.saveEntity(entity);
      

      var entityDto = new BaseEntityResponseDto<T>();
      entityDto.setStatus(SUCCESS);
      entityDto.setEntity(savedEntity);
      
      return CompletableFuture.completedFuture(entityDto);

    } catch (Exception e) {
      
      throw new DatabaseException(FAIL_CODE, e.getMessage());
    }
  }

  @Override
  public BaseEntityResponseDto<List<T>> saveEntities(List<T> entities) {
    // String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    

    try {
      switchDataSource(EDataSourceType.PRIMARY);
      List<T> savedEntities = repository.saveAll(entities);
      

      var entityDto = new BaseEntityResponseDto<List<T>>();
      entityDto.setStatus(SUCCESS);
      entityDto.setEntity(savedEntities);
      
      return entityDto;

    } catch (Exception e) {
      
      throw new DatabaseException(FAIL_CODE, e.getMessage());
    }
  }

  @Override
  @Async("jpaExecutor")
  public CompletableFuture<BaseEntityResponseDto<List<T>>> saveEntitiesAsync(List<T> entities) {
    // String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    

    try {
      switchDataSource(EDataSourceType.PRIMARY);
      List<T> savedEntities = repository.saveAll(entities);
      

      var entityDto = new BaseEntityResponseDto<List<T>>();
      entityDto.setStatus(SUCCESS);
      entityDto.setEntity(savedEntities);
      
      return CompletableFuture.completedFuture(entityDto);

    } catch (Exception e) {
      
      throw new DatabaseException(FAIL_CODE, e.getMessage());
    }
  }

  @Override
  public BaseEntityResponseDto<T> findById(ID id) {
    // String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    
    String responseStatus = SUCCESS;
    try {
      switchDataSource(EDataSourceType.SECONDARY);
      Optional<T> optionalEntity = repository.getEntityById(id);
      
      if (!optionalEntity.isPresent()) {
        responseStatus = FAIL;
      }
      T entity = optionalEntity.orElseGet(() -> null);

      var entityDto = new BaseEntityResponseDto<T>();
      entityDto.setCode(optionalEntity.isPresent() ? SUCCESS_CODE : NOT_FOUND);
      entityDto.setStatus(responseStatus);
      entityDto.setEntity(entity);
      
      return entityDto;

    } catch (Exception e) {
      
      throw new DatabaseException(FAIL_CODE, e.getMessage());
    }
  }
  
  @Override
  @Async("jpaExecutor")
  public CompletableFuture<BaseEntityResponseDto<T>> getEntityByIdAsync(ID id) {
    // String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    

    try {
      
      switchDataSource(EDataSourceType.SECONDARY);
      Optional<T> optionalEntity = repository.getEntityById(id);
      T entity = optionalEntity.orElseGet(() -> null);

      var entityDto = new BaseEntityResponseDto<T>();
      entityDto.setCode(optionalEntity.isPresent() ? SUCCESS_CODE : NOT_FOUND);
      entityDto.setStatus(SUCCESS);
      entityDto.setEntity(entity);
      
      return CompletableFuture.completedFuture(entityDto);

    } catch (Exception e) {
      
      throw new DatabaseException(FAIL_CODE, e.getMessage());
    }
  }

  @Override
  public BaseEntityResponseDto<T> update(T entity) {
    // String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    

    try {
      switchDataSource(EDataSourceType.PRIMARY);
      T updatedEntity = repository.updateEntity(entity);
      

      var entityDto = new BaseEntityResponseDto<T>();
      entityDto.setStatus(SUCCESS);
      entityDto.setEntity(updatedEntity);
      
      return entityDto;

    } catch (Exception e) {
      
      throw new DatabaseException(FAIL_CODE, e.getMessage());
    }
  }

  @Override
  @Async("jpaExecutor")
  public CompletableFuture<BaseEntityResponseDto<T>> updateAsync(T entity) {
    // String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    

    try {
      switchDataSource(EDataSourceType.PRIMARY);
      T updatedEntity = repository.updateEntity(entity);
      

      var entityDto = new BaseEntityResponseDto<T>();
      entityDto.setStatus(SUCCESS);
      entityDto.setEntity(updatedEntity);
      
      return CompletableFuture.completedFuture(entityDto);

    } catch (Exception e) {
      
      throw new DatabaseException(FAIL_CODE, e.getMessage());
    }
  }

  @Override
  public BaseEntityResponseDto<T> findAllWithSortBy(String sortBy, String sortDirection) {
    // String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    

    try {
      switchDataSource(EDataSourceType.SECONDARY);
      Sort sortOrder;
      sortOrder = (sortBy == "") ? Sort.by("Id") : Sort.by(sortBy);
      sortOrder = (sortDirection == "") ? sortOrder.ascending() : sortOrder.descending();

      List<T> entityLIst = repository.listAll(sortOrder);
      

      var appModelList = new BaseEntityResponseDto<T>();
      appModelList.setStatus(SUCCESS);
      appModelList.setEntityList(entityLIst);
      
      return appModelList;

    } catch (Exception e) {
      
      throw new DatabaseException(FAIL_CODE, e.getMessage());
    }
  }

  @Override
  public BaseEntityResponseDto<T> findAll(PageRequest page) {
    // String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    

    try {
      switchDataSource(EDataSourceType.SECONDARY);

      List<T> entityLIst = repository.findAll(page).toList();
      

      var appModelList = new BaseEntityResponseDto<T>();
      
      appModelList.setStatus(SUCCESS);
      appModelList.setEntityList(entityLIst);
      
      return appModelList;

    } catch (Exception e) {
      
      throw new DatabaseException(FAIL_CODE, e.getMessage());
    }
  }

  @Override
  public BaseEntityResponseDto<T> findAll(Pageable page) {
    // String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    

    try {
      switchDataSource(EDataSourceType.SECONDARY);

      Page<T> entityLIst = repository.findAll(page);
      

      var appModelList = new BaseEntityResponseDto<T>();
      
      appModelList.setStatus(SUCCESS);
      appModelList.setPage(entityLIst);
      
      return appModelList;

    } catch (Exception e) {
      
      throw new DatabaseException(FAIL_CODE, e.getMessage());
    }
  }

  @Override
  public BaseEntityResponseDto<T> findAll() {
    // String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    

    try {
      switchDataSource(EDataSourceType.SECONDARY);

      List<T> entityLIst = repository.findAll();
      

      var appModelList = new BaseEntityResponseDto<T>();
      
      appModelList.setStatus(SUCCESS);
      appModelList.setEntityList(entityLIst);
      
      return appModelList;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE, e.getMessage());
    }
  }

  @Override
  public CompletableFuture<BaseEntityResponseDto<T>> findAllAsync() {
    // String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    

    try {
      switchDataSource(EDataSourceType.SECONDARY);

      List<T> entityLIst = repository.findAll();
      

      var appModelList = new BaseEntityResponseDto<T>();
      appModelList.setStatus(SUCCESS);
      appModelList.setEntityList(entityLIst);
      
      return CompletableFuture.completedFuture(appModelList);

    } catch (Exception e) {
      
      throw new DatabaseException(FAIL_CODE, e.getMessage());
    }
  }

  @Override
  @Async("jpaExecutor")
  public CompletableFuture<BaseEntityResponseDto<T>> listAllAsync(String sortBy, String sortDirection) {
    // String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    

    try {
      switchDataSource(EDataSourceType.SECONDARY);
      Sort sortOrder;
      sortOrder = (sortBy == "") ? Sort.by("Id") : Sort.by(sortBy);
      sortOrder = (sortDirection == "") ? sortOrder.ascending() : sortOrder.descending();

      List<T> entityLIst = repository.listAll(sortOrder);
      

      var appModelList = new BaseEntityResponseDto<T>();
      appModelList.setStatus(SUCCESS);
      appModelList.setEntityList(entityLIst);
      
      return CompletableFuture.completedFuture(appModelList);

    } catch (Exception e) {
      
      throw new DatabaseException(FAIL_CODE, e.getMessage());
    }
  }

  @Override
  public BaseEntityResponseDto<T> deleteEntity(ID id) {
    // String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    

    try {
      switchDataSource(EDataSourceType.PRIMARY);
      repository.deleteEntity(id);
      

      var deleteEntity = new BaseEntityResponseDto<T>();
      deleteEntity.setStatus(SUCCESS);
      
      return deleteEntity;

    } catch (Exception e) {
      
      throw new DatabaseException(FAIL_CODE, e.getMessage());
    }
  }

  @Override
  @Async("jpaExecutor")
  public CompletableFuture<BaseEntityResponseDto<T>> deleteEntityAsync(ID id) {
    // String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    

    try {
      switchDataSource(EDataSourceType.PRIMARY);
      repository.deleteEntity(id);
      

      var deleteEntity = new BaseEntityResponseDto<T>();
      deleteEntity.setStatus(SUCCESS);
      
      return CompletableFuture.completedFuture(deleteEntity);

    } catch (Exception e) {
      
      throw new DatabaseException(FAIL_CODE, e.getMessage());
    }
  }

  @Override
  public BaseEntityResponseDto<T> callStoreProcedure(Connection connection,String procedureSignature, String[] paramNames, String[] paramValues) {
    // String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    
    ResultSetMetaData metaData = null;

    try {
      var dataRows = new BaseEntityResponseDto<T>();
      var procedureCallFormatSb = new StringBuffer("{call ");
      procedureCallFormatSb.append(procedureSignature);
      procedureCallFormatSb.append("}");

      @Cleanup
      CallableStatement prepStm = connection.prepareCall(procedureCallFormatSb.toString());

      for (int index = 0; index < paramNames.length; index++) {
        prepStm.setString(paramNames[index], paramValues[index]);
      }

      @Cleanup
      ResultSet resultSet = prepStm.executeQuery();

      var resultList = new ArrayList<Map<String, String>>();
      Map<String, String> row = null;

      metaData = resultSet.getMetaData();
      Integer columnCount = metaData.getColumnCount();

      while (resultSet.next()) {
        row = new HashMap<String, String>();
        for (int i = 1; i <= columnCount; i++) {
          row.put(metaData.getColumnName(i).toUpperCase(), resultSet.getString(i)); // All columns name return from
                                                                                    // database convert to upper case.
        }
        resultList.add(row);
      }

      
      
      dataRows.setDataRows(resultList);

      return dataRows;
    } catch (Exception e) {
      
      throw new DatabaseException(FAIL_CODE, e.getMessage());
    } finally {
      metaData = null;
    }
  }

  @Override
  @Async("jpaExecutor")
  public CompletableFuture<BaseEntityResponseDto<T>> callStoreProcedureAsync(Connection connection, String procedureSignature, String[] paramNames, String[] paramValues) {
    // String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    
    ResultSetMetaData metaData = null;

    try {
      var dataRows = new BaseEntityResponseDto<T>();
      var procedureCallFormatSb = new StringBuffer("{call ");
      procedureCallFormatSb.append(procedureSignature);
      procedureCallFormatSb.append("}");

      @Cleanup
      CallableStatement prepStm = connection.prepareCall(procedureCallFormatSb.toString());

      for (int index = 0; index < paramNames.length; index++) {
        prepStm.setString(paramNames[index], paramValues[index]);
      }

      @Cleanup
      ResultSet resultSet = prepStm.executeQuery();

      var resultList = new ArrayList<Map<String, String>>();
      Map<String, String> row = null;

      metaData = resultSet.getMetaData();
      Integer columnCount = metaData.getColumnCount();

      while (resultSet.next()) {
        row = new HashMap<String, String>();
        for (int i = 1; i <= columnCount; i++) {
          row.put(metaData.getColumnName(i).toUpperCase(), resultSet.getString(i)); // All columns name return from
                                                                                    // database convert to upper case.
        }
        resultList.add(row);
      }

      
      
      dataRows.setDataRows(resultList);

      return CompletableFuture.completedFuture(dataRows);
    } catch (Exception e) {
      
      throw new DatabaseException(FAIL_CODE, e.getMessage());
    } finally {
      metaData = null;
    }
  }

  protected void switchDataSource(EDataSourceType dataSourceType) {

    if (dataSourceDCContextHolder == null) {
      return;
    }

    // Switch to primary database servers
    if (dataSourceType == EDataSourceType.PRIMARY) {
      // Current primary database server is primary side.
      if (dataSourceDCContextHolder.getPrimaryDataSourceType() == EDataSourceType.PRIMARY) {
        dataSourceDCContextHolder.setBranchContext(EDataSourceType.PRIMARY);
        return;
      }
      // Current primary database server is secondary side.
      // So primary database server is secondary side.
      dataSourceDCContextHolder.setBranchContext(EDataSourceType.SECONDARY);
      return;
    }

    // Switch to primary database servers
    if (dataSourceType == EDataSourceType.SECONDARY) {
      // Current primary database server is primary side.
      if (dataSourceDCContextHolder.getPrimaryDataSourceType() == EDataSourceType.PRIMARY) {
        dataSourceDCContextHolder.setBranchContext(EDataSourceType.SECONDARY);
        return;
      }
      // Current primary database server is secondary side.
      // So secondary database server is primary side.
      dataSourceDCContextHolder.setBranchContext(EDataSourceType.PRIMARY);
      return;
    }
  }
}
package com.hfsolution.app.interfaces;

import java.sql.Connection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.hfsolution.app.dto.BaseEntityResponseDto;

public interface IBaseDBDao <T, ID> {

  BaseEntityResponseDto<T> saveEntity(T entity);
  CompletableFuture<BaseEntityResponseDto<T>> saveEntityAsync(T entity);

	BaseEntityResponseDto<List<T>> saveEntities(List<T> entities);
  CompletableFuture<BaseEntityResponseDto<List<T>>> saveEntitiesAsync(List<T> entities);

	BaseEntityResponseDto<T> findById(ID id);
  CompletableFuture<BaseEntityResponseDto<T>> getEntityByIdAsync(ID id);
  CompletableFuture<BaseEntityResponseDto<T>> getAllEntityByIdAsync(List<ID> ids);

	BaseEntityResponseDto<T> update(T entity);
  CompletableFuture<BaseEntityResponseDto<T>> updateAsync(T entity);

	BaseEntityResponseDto<T> findAllWithSortBy(String sortBy, String sortDirection);
  CompletableFuture<BaseEntityResponseDto<T>> listAllAsync(String sortBy, String sortDirection);

  BaseEntityResponseDto<T> findAll(PageRequest page);
  BaseEntityResponseDto<T> findAll(Pageable page);
  // BaseEntityResponseDto<T> findPercentage(Page<T> page);
  BaseEntityResponseDto<T> findAll();
  CompletableFuture<BaseEntityResponseDto<T>> findAllAsync();

	BaseEntityResponseDto<T> deleteEntity(ID id);
  CompletableFuture<BaseEntityResponseDto<T>> deleteEntityAsync(ID id);
  
  BaseEntityResponseDto<T> callStoreProcedure(Connection connection, String procedureSignature, String paramNames[], String paramValues[]);
  CompletableFuture<BaseEntityResponseDto<T>> callStoreProcedureAsync(Connection connection, String procedureSignature, String paramNames[], String paramValues[]);
}
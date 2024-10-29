package com.hfsolution.app.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBaseRepository<T, ID> extends JpaRepository<T, ID> {
  
	public default T saveEntity(T entity) {
		T savedEntity = this.save(entity);
		return savedEntity;
	}

	public default Optional<T> getEntityById(ID id) {
		Optional<T> entity = this.findById(id);
		return entity;
	}

	public default T updateEntity(T entity) {
		T updatedEntity = this.save(entity);
		return updatedEntity;
	}

	public default List<T> listAll(Sort sort) {
		List<T> entityList = this.findAll(sort);
		return entityList;
	}

	public default Page<T> listWithPaging(Pageable pageable) {
		Page<T> paging = this.findAll(pageable);
		return paging;
	}

	public default boolean deleteEntity(ID id) {
		this.deleteById(id);
		return true;
	}

}


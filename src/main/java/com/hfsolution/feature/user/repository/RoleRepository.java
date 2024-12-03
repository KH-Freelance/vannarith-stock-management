package com.hfsolution.feature.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.hfsolution.feature.user.entity.Role;


public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

  // void deleteByPermissionsId(List<Long> ids);

}

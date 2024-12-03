package com.hfsolution.feature.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.hfsolution.feature.user.entity.Permission;


public interface PermissionRepository extends JpaRepository<Permission, Long>,JpaSpecificationExecutor<Permission> {
  // Optional<User> findByEmail(String email);
  // Page<User> findByEmailLike(String email,Pageable pageable);
  // List<User> findByEmailLike(String email);
  // List<User> findByEmailLikeOrderByLastnameAsc(String email);
  // List<User> findByFirstnameLike(String firstname);
  // List<User> findByLastnameLike(String lastname);
  // List<User> findByRoleLike(Role role);
  // List<User> findByRoleLike(Role role);
}

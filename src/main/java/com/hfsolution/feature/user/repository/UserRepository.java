package com.hfsolution.feature.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.hfsolution.feature.user.entity.User;
import com.hfsolution.feature.user.enums.Role;

import jakarta.transaction.Transactional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

  Optional<User> findByEmail(String email);
  Page<User> findByEmailLike(String email,Pageable pageable);
  List<User> findByEmailLike(String email);
  List<User> findByEmailLikeOrderByLastnameAsc(String email);
  List<User> findByFirstnameLike(String firstname);
  List<User> findByLastnameLike(String lastname);
  // List<User> findByRoleLike(Role role);
  // List<User> findByRoleLike(Role role);
}

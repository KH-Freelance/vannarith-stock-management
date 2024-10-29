package com.hfsolution.feature.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.hfsolution.app.repository.IBaseRepository;
import com.hfsolution.feature.user.entity.User;

public interface UserRepository extends IBaseRepository<User, Integer>, JpaSpecificationExecutor<User> {
  Optional<User> findByEmail(String email);
}

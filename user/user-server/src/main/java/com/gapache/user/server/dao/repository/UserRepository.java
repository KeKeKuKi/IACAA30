package com.gapache.user.server.dao.repository;

import com.gapache.jpa.BaseJpaRepository;
import com.gapache.user.server.dao.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author HuSen
 * @since 2020/9/8 11:29 上午
 */
public interface UserRepository extends BaseJpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    boolean existsByUsername(String username);

    UserEntity findByUsername(String username);
}

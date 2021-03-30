package com.gapache.cloud.auth.server.dao.repository.user;

import com.gapache.cloud.auth.server.dao.entity.RoleEntity;
import com.gapache.jpa.BaseJpaRepository;

import java.util.List;

/**
 * @author HuSen
 * @since 2021/1/26 9:47 上午
 */
public interface RoleRepository extends BaseJpaRepository<RoleEntity, Long> {

    RoleEntity findByName(String name);

    List<RoleEntity> findAllByNameLike(String name);
}

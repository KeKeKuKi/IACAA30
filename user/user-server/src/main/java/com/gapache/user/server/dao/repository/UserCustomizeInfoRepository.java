package com.gapache.user.server.dao.repository;

import com.gapache.jpa.BaseJpaRepository;
import com.gapache.user.server.dao.entity.UserCustomizeInfoEntity;

import java.util.List;

/**
 * @author HuSen
 * @since 2021/1/25 1:10 下午
 */
public interface UserCustomizeInfoRepository extends BaseJpaRepository<UserCustomizeInfoEntity, Long> {

    UserCustomizeInfoEntity findByUserIdAndClientId(Long userId, String clientId);
}

package com.gapache.cloud.auth.server.service;

import com.gapache.security.model.PermissionDto;

import java.util.List;

/**
 * @author HuSen
 * @since 2021/1/26 9:56 上午
 */
public interface PermissionService {
    List<PermissionDto> list();

    List<PermissionDto> listByRole(Long id);
}

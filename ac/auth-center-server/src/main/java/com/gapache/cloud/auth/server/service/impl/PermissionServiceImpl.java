package com.gapache.cloud.auth.server.service.impl;

import com.gapache.cloud.auth.server.dao.entity.PermissionEntity;
import com.gapache.cloud.auth.server.dao.entity.ResourceEntity;
import com.gapache.cloud.auth.server.dao.entity.RoleEntity;
import com.gapache.cloud.auth.server.dao.entity.RolePermissionEntity;
import com.gapache.cloud.auth.server.dao.repository.resource.ResourceRepository;
import com.gapache.cloud.auth.server.dao.repository.user.PermissionRepository;
import com.gapache.cloud.auth.server.dao.repository.user.RolePermissionRepository;
import com.gapache.cloud.auth.server.service.PermissionService;
import com.gapache.security.model.PermissionDto;
import com.gapache.security.model.RoleDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author HuSen
 * @since 2021/1/26 9:56 上午
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Resource
    ResourceRepository resourceRepository;

    @Resource
    RolePermissionRepository rolePermissionRepository;

    private PermissionDto resource2Permission(ResourceEntity x) {
        PermissionDto dto = new PermissionDto();
        dto.setId(x.getId());
        dto.setName(x.getName());
        return dto;
    }

    private PermissionDto resourcePermission2Permission(RolePermissionEntity x) {
        PermissionDto dto = new PermissionDto();
        dto.setId(x.getPermissionId());
        return dto;
    }

    @Override
    public List<PermissionDto> list() {
        List<ResourceEntity> all = resourceRepository.findAll();
        return all.stream().map(this :: resource2Permission).collect(Collectors.toList());
    }

    @Override
    public List<PermissionDto> listByRole(Long id) {
        List<RolePermissionEntity> allByRoleId = rolePermissionRepository.findAllByRoleId(id);
        return allByRoleId.stream().map(this :: resourcePermission2Permission).collect(Collectors.toList());
    }
}

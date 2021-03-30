package com.gapache.cloud.auth.server.service.impl;

import com.gapache.cloud.auth.server.dao.entity.RoleEntity;
import com.gapache.cloud.auth.server.dao.entity.RolePermissionEntity;
import com.gapache.cloud.auth.server.dao.repository.user.RolePermissionRepository;
import com.gapache.cloud.auth.server.dao.repository.user.RoleRepository;
import com.gapache.cloud.auth.server.service.RoleService;
import com.gapache.commons.model.IPageRequest;
import com.gapache.commons.model.PageResult;
import com.gapache.commons.model.ThrowUtils;
import com.gapache.jpa.FindUtils;
import com.gapache.security.model.RoleCreateDTO;
import com.gapache.security.model.RoleDTO;
import com.gapache.security.model.RoleUpdateDTO;
import com.gapache.security.model.SecurityError;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author HuSen
 * @since 2021/1/26 9:56 上午
 */
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public RoleServiceImpl(RoleRepository roleRepository, RolePermissionRepository rolePermissionEntity) {
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionEntity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean create(RoleCreateDTO dto) {
        RoleEntity entity = roleRepository.findByName(dto.getName());
        ThrowUtils.throwIfTrue(entity != null, SecurityError.ROLE_EXISTED);
        entity = new RoleEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        roleRepository.save(entity);
        Long roleId = entity.getId();

        Set<Long> permissionList = dto.getPermissionList();
        saveOrUpdatePermission(roleId, permissionList);
        return true;
    }

    private void saveOrUpdatePermission(Long roleId, Set<Long> permissionList) {
        if (!CollectionUtils.isEmpty(permissionList)) {
            List<RolePermissionEntity> rolePermissionEntities = rolePermissionRepository.findAllByRoleId(roleId);
            rolePermissionRepository.deleteAll(rolePermissionEntities);
//            permissionList
//            List<RolePermissionEntity> deleteAll = rolePermissionEntities.stream().filter(old -> !permissionList.contains(old.getPermissionId())).collect(Collectors.toList());
//            rolePermissionRepository.deleteAll(deleteAll);
//
//            Set<Long> permissionIdOld = rolePermissionEntities.stream().map(RolePermissionEntity::getPermissionId).collect(Collectors.toSet());
            List<RolePermissionEntity> newAdd = permissionList.stream().map(permissionId -> {
                RolePermissionEntity rolePermissionEntity = new RolePermissionEntity();
                rolePermissionEntity.setRoleId(roleId);
                rolePermissionEntity.setPermissionId(permissionId);
                return rolePermissionEntity;
            }).collect(Collectors.toList());
            rolePermissionRepository.saveAll(newAdd);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(RoleUpdateDTO dto) {
//        RoleEntity entity = roleRepository.findByName(dto.getName());
//        ThrowUtils.throwIfTrue(entity != null && StringUtils.equals(entity.getName(), dto.getName()), SecurityError.ROLE_EXISTED);

        Optional<RoleEntity> byId = roleRepository.findById(dto.getId());
        ThrowUtils.throwIfTrue(!byId.isPresent(), SecurityError.ROLE_NOT_FOUND);

        byId.ifPresent(roleEntity -> {
            roleEntity.setName(dto.getName());
            roleEntity.setDescription(dto.getDescription());
            roleRepository.save(roleEntity);
            saveOrUpdatePermission(roleEntity.getId(), dto.getPermissionList());
        });

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id) {
        roleRepository.deleteById(id);
        rolePermissionRepository.deleteAllByRoleId(id);
        return true;
    }

    @Override
    public PageResult<RoleDTO> page(IPageRequest<RoleDTO> iPageRequest) {
        PageRequest pageRequest = PageRequest.of(iPageRequest.getPage() - 1, iPageRequest.getNumber());
        Page<RoleEntity> all = roleRepository.findAll(pageRequest);
        return PageResult.of(all.getTotalElements(), this::entity2Dto, all.getContent());
    }

    private RoleDTO entity2Dto(RoleEntity x) {
        RoleDTO roleDTO = new RoleDTO();
        BeanUtils.copyProperties(x, roleDTO);
        roleDTO.setId(x.getId());
        return roleDTO;
    }

    @Override
    public List<RoleDTO> list(RoleDTO roleDTO) {
        List<RoleEntity> allByNameLike = StringUtils.isNotBlank(roleDTO.getName()) ?
                roleRepository.findAllByNameLike(FindUtils.allMatch(roleDTO.getName())) : roleRepository.findAll();
        return allByNameLike.stream().map(this::entity2Dto).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteAll(Stream<Long> ids) {
        ids.forEach(i -> {
            roleRepository.deleteById(i);
            rolePermissionRepository.deleteAllByRoleId(i);
        });
        return true;
    }


}

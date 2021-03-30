package com.gapache.cloud.auth.server.service;

import com.gapache.commons.model.IPageRequest;
import com.gapache.commons.model.PageResult;
import com.gapache.security.model.RoleCreateDTO;
import com.gapache.security.model.RoleDTO;
import com.gapache.security.model.RoleUpdateDTO;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author HuSen
 * @since 2021/1/26 9:56 上午
 */
public interface RoleService {

    Boolean create(RoleCreateDTO dto);

    Boolean update(RoleUpdateDTO dto);

    Boolean delete(Long id);

    PageResult<RoleDTO> page(IPageRequest<RoleDTO> iPageRequest);

    List<RoleDTO> list(RoleDTO roleDTO);

    Boolean deleteAll(Stream<Long> ids);
}

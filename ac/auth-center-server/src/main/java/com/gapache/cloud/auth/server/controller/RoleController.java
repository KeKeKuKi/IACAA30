package com.gapache.cloud.auth.server.controller;

import com.gapache.cloud.auth.server.service.RoleService;
import com.gapache.commons.model.IPageRequest;
import com.gapache.commons.model.JsonResult;
import com.gapache.commons.model.PageResult;
import com.gapache.security.annotation.AuthResource;
import com.gapache.security.annotation.NeedAuth;
import com.gapache.security.holder.AccessCardHolder;
import com.gapache.security.model.RoleCreateDTO;
import com.gapache.security.model.RoleDTO;
import com.gapache.security.model.RoleUpdateDTO;
import com.gapache.web.Check;
import com.gapache.web.Validating;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author HuSen
 * @since 2021/1/26 9:57 上午
 */
@RestController
@Validating
@RequestMapping("/api/role")
@NeedAuth("role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    @AuthResource(scope = "create", name = "创建角色")
    public JsonResult<Boolean> create(@RequestBody @Check RoleCreateDTO dto) {
        return JsonResult.of(roleService.create(dto));
    }

    @PutMapping
    @AuthResource(scope = "update", name = "更新角色")
    public JsonResult<Boolean> update(@RequestBody @Check RoleUpdateDTO dto) {
        return JsonResult.of(roleService.update(dto));
    }

    @DeleteMapping("/{id}")
    @AuthResource(scope = "delete", name = "删除角色")
    public JsonResult<Boolean> delete(@PathVariable Long id) {
        return JsonResult.of(roleService.delete(id));
    }

    @PostMapping("/deleteAll")
    @AuthResource(scope = "deleteAll", name = "删除角色")
    public JsonResult<Boolean> deleteAll(@RequestBody List<RoleDTO> dtos) {
        return JsonResult.of(roleService.deleteAll(dtos.stream().map(RoleDTO :: getId)));
    }

    @PostMapping("/page")
    @AuthResource(scope = "page", name = "分页查询角色")
    public JsonResult<PageResult<RoleDTO>> page(@RequestBody IPageRequest<RoleDTO> iPageRequest) {
        return JsonResult.of(roleService.page(iPageRequest));
    }

    @PostMapping("/list")
    @AuthResource(scope = "list", name = "查询角色")
    public JsonResult<List<RoleDTO>> list(@RequestBody RoleDTO roleDTO) {
        AccessCardHolder.getContext().getUserId();
        return JsonResult.of(roleService.list(roleDTO));
    }
}

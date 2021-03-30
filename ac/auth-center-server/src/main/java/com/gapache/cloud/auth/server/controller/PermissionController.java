package com.gapache.cloud.auth.server.controller;

import com.gapache.cloud.auth.server.service.PermissionService;
import com.gapache.commons.model.JsonResult;
import com.gapache.security.annotation.AuthResource;
import com.gapache.security.annotation.NeedAuth;
import com.gapache.security.holder.AccessCardHolder;
import com.gapache.security.model.PermissionDto;
import com.gapache.security.model.RoleDTO;
import com.gapache.web.Validating;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.management.relation.Role;
import java.security.acl.Permission;
import java.util.List;

/**
 * @author ZhaoZezhong
 * @version V1.0
 * @Title: PermisionController
 * @Description: Company:成都平凡谷科技有限责任公司
 * @date 2021/3/2414:29
 */
@RestController
@Validating
@RequestMapping("/api/permission")
@NeedAuth("permission")
public class PermissionController {
    @Resource
    PermissionService permissionService;

    @PostMapping("/list")
    @AuthResource(scope = "list", name = "权限列表")
    public JsonResult<List<PermissionDto>> list() {
        return JsonResult.of(permissionService.list());
    }

    @PostMapping("/listByRole")
    @AuthResource(scope = "listByRole", name = "角色权限列表")
    public JsonResult<List<PermissionDto>> listByRole(@RequestBody RoleDTO role) {
        return JsonResult.of(permissionService.listByRole(role.getId()));
    }
}

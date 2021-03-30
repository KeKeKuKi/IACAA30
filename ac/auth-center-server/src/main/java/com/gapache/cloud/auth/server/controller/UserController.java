package com.gapache.cloud.auth.server.controller;

import com.gapache.cloud.auth.server.service.UserService;
import com.gapache.commons.model.JsonResult;
import com.gapache.security.annotation.AuthResource;
import com.gapache.security.annotation.NeedAuth;
import com.gapache.security.model.SetUserRoleDTO;
import com.gapache.user.common.model.vo.UserVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author HuSen
 * @since 2020/8/3 11:42 上午
 */
@RestController
@RequestMapping("/api/user")
@NeedAuth("user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping
    @AuthResource(scope = "create", name = "创建用户")
    public JsonResult<Boolean> create(@RequestBody UserVO vo) {
        return JsonResult.of(userService.create(vo));
    }

    @PostMapping("/setUserRole")
    @AuthResource(scope = "setUserRole", name = "设置用户角色")
    public JsonResult<Boolean> setUserRole(@RequestBody SetUserRoleDTO dto) {
        return JsonResult.of(userService.setUserRole(dto));
    }
}

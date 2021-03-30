package com.gapache.cloud.auth.server.service;

import com.gapache.cloud.auth.server.model.UserDetailsImpl;
import com.gapache.security.model.SetUserRoleDTO;
import com.gapache.security.model.UserDTO;
import com.gapache.security.model.UserLoginDTO;
import com.gapache.security.model.UserInfoDTO;
import com.gapache.user.common.model.vo.UserVO;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author HuSen
 * @since 2020/7/31 10:19 上午
 */
public interface UserService extends UserDetailsService {

    /**
     * 用户登陆
     *
     * @param dto 用户登陆数据
     * @return 登陆结果
     */
    UserInfoDTO login(UserLoginDTO dto);

    Boolean create(UserVO vo);

    UserDetailsImpl findById(Long id);

    Boolean logout(HttpServletRequest request);

    Boolean setUserRole(SetUserRoleDTO dto);
}

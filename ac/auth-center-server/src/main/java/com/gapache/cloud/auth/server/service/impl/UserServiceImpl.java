package com.gapache.cloud.auth.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gapache.cloud.auth.server.dao.entity.ClientEntity;
import com.gapache.cloud.auth.server.dao.entity.ResourceEntity;
import com.gapache.cloud.auth.server.dao.entity.UserEntity;
import com.gapache.cloud.auth.server.dao.entity.UserRoleEntity;
import com.gapache.cloud.auth.server.dao.repository.client.ClientRepository;
import com.gapache.cloud.auth.server.dao.repository.resource.ResourceRepository;
import com.gapache.cloud.auth.server.dao.repository.user.RoleRepository;
import com.gapache.cloud.auth.server.dao.repository.user.UserClientRelationRepository;
import com.gapache.cloud.auth.server.dao.repository.user.UserRepository;
import com.gapache.cloud.auth.server.dao.repository.user.UserRoleRepository;
import com.gapache.cloud.auth.server.model.UserDetailsImpl;
import com.gapache.cloud.auth.server.service.UserService;
import com.gapache.cloud.auth.server.utils.DynamicPropertyUtils;
import com.gapache.commons.model.JsonResult;
import com.gapache.commons.model.ThrowUtils;
import com.gapache.commons.model.SecurityException;
import com.gapache.security.interfaces.AuthorizeInfoManager;
import com.gapache.security.model.*;
import com.gapache.security.model.impl.CertificationImpl;
import com.gapache.security.properties.SecurityProperties;
import com.gapache.security.utils.JwtUtils;
import com.gapache.user.common.model.vo.UserVO;
import com.gapache.user.sdk.feign.UserServerFeign;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.PrivateKey;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.gapache.commons.model.AuthConstants.TOKEN_HEADER;

/**
 * @author HuSen
 * @since 2020/7/31 10:19 上午
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private UserRepository userRepository;

    @Resource
    private RoleRepository roleRepository;

    @Resource
    private ResourceRepository resourceRepository;

    @Resource
    private UserRoleRepository userRoleRepository;

    @Resource
    private ClientRepository clientRepository;

    @Resource
    private UserClientRelationRepository userClientRelationRepository;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private PrivateKey privateKey;

    @Resource
    private SecurityProperties securityProperties;

    @Resource
    private UserServerFeign userServerFeign;

    @Resource
    private AuthorizeInfoManager authorizeInfoManager;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String clientId = DynamicPropertyUtils.getString("auth.center.client-id");
        String adminUsername = DynamicPropertyUtils.getString("admin.username");
        if (StringUtils.equals(username, adminUsername)) {
            return loginAdmin(username, clientId);
        } else {
            JsonResult<UserVO> result = userServerFeign.findByUsername(username, clientId);

            if (!result.requestSuccess()) {
                throw new UsernameNotFoundException("用户名或密码不正确");
            }

            UserVO userEntity = result.getData();

            UserDetailsImpl userDetails = new UserDetailsImpl();
            userDetails.setId(userEntity.getId());
            userDetails.setUsername(userEntity.getUsername());
            userDetails.setPassword(userEntity.getPassword());
            userDetails.setClientId(clientId);
            String customizeInfo = userEntity.getCustomizeInfo();
            if (StringUtils.isNotBlank(customizeInfo)) {
                userDetails.setCustomerInfo(JSON.parseObject(customizeInfo, CustomerInfo.class));
            }

            List<GrantedAuthority> authorities = resourceRepository.findAllResource(userEntity.getId()).stream()
                    .map(r -> new SimpleGrantedAuthority(r.getResourceServerName() + ":" + r.getScope()))
                    .collect(Collectors.toList());

            userDetails.setAuthorities(authorities);
            return userDetails;
        }
    }

    private UserDetails loginAdmin(String admin, String clientId) {
        String password = DynamicPropertyUtils.getString("admin.password");
        String introduction = DynamicPropertyUtils.getString("admin.introduction");
        String avatar = DynamicPropertyUtils.getString("admin.avatar");
        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setId(0L);
        userDetails.setUsername(admin);
        userDetails.setPassword(passwordEncoder.encode(password));
        userDetails.setClientId(clientId);
        userDetails.setCustomerInfo(new CustomerInfo());
        userDetails.getCustomerInfo().put("introduction", introduction);
        userDetails.getCustomerInfo().put("avatar", avatar);
        // 拥有所有的权限
        userDetails.setAuthorities(resourceRepository.findAll().stream()
                .map(r -> new SimpleGrantedAuthority(r.fullScopeName()))
                .collect(Collectors.toList()));
        return userDetails;
    }

    @Override
    public UserInfoDTO login(UserLoginDTO dto) {
        String username = DynamicPropertyUtils.getString("admin.username");
        if (StringUtils.equals(username, dto.getUsername())) {
            return loginWithAdmin(dto);
        } else {
            JsonResult<UserVO> result = userServerFeign.findByUsername(dto.getUsername(), dto.getClientId());
            if (!result.requestSuccess()) {
                throw new SecurityException(SecurityError.LOGIN_FAIL);
            }

            UserVO userEntity = result.getData();

            // 检查密码是否正确
            boolean passwordMatches = passwordEncoder.matches(dto.getPassword(), userEntity.getPassword());
            if (!passwordMatches) {
                throw new SecurityException(SecurityError.LOGIN_FAIL);
            }

            // 校验client和user的关系是否正确
            if (StringUtils.isNotBlank(dto.getClientId())) {
                ClientEntity clientEntity = clientRepository.findByClientId(dto.getClientId());
                if (clientEntity == null) {
                    throw new SecurityException(SecurityError.LOGIN_FAIL);
                }

                Boolean existsByUserIdAndClientId = userClientRelationRepository.existsByUserIdAndClientId(userEntity.getId(), clientEntity.getId());
                if (!existsByUserIdAndClientId) {
                    throw new SecurityException(SecurityError.LOGIN_FAIL);
                }
            }

            CertificationImpl certification = new CertificationImpl();
            certification.setId(userEntity.getId());
            certification.setName(userEntity.getUsername());
            certification.setSign(false);
            certification.setClientId(dto.getClientId());

            String content = JSON.toJSONString(certification);
            String token = JwtUtils.generateToken(content, privateKey, securityProperties.getTimeout());

            String customizeInfo = userEntity.getCustomizeInfo();
            JSONObject jsonObject = StringUtils.isNotBlank(customizeInfo) ?
                    JSON.parseObject(customizeInfo) : new JSONObject();

            UserInfoDTO userInfoDTO = new UserInfoDTO();
            userInfoDTO.setToken(token);
            userInfoDTO.setName(userEntity.getUsername());

            // 设置描述
            userInfoDTO.setIntroduction(jsonObject.getString("introduction"));
            // 设置角色
            List<ResourceEntity> allResource = resourceRepository.findAllResource(userEntity.getId());
            if (allResource == null) {
                throw new SecurityException(SecurityError.LOGIN_FAIL);
            }
            userInfoDTO.setRoles(allResource.stream().map(ResourceEntity::fullScopeName).collect(Collectors.toList()));
            // 设置头像
            userInfoDTO.setAvatar(jsonObject.containsKey("avatar") ? jsonObject.getString("avatar") : "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");

            authorizeInfoManager.save(token, securityProperties.getTimeout(), JSON.parseObject(customizeInfo, CustomerInfo.class), userInfoDTO.getRoles());
            return userInfoDTO;
        }
    }

    private UserInfoDTO loginWithAdmin(UserLoginDTO dto) {
        String password = DynamicPropertyUtils.getString("admin.password");
        String introduction = DynamicPropertyUtils.getString("admin.introduction");
        String avatar = DynamicPropertyUtils.getString("admin.avatar");

        if (!StringUtils.equals(password, dto.getPassword())) {
            throw new SecurityException(SecurityError.LOGIN_FAIL);
        }

        CertificationImpl certification = new CertificationImpl();
        certification.setId(0L);
        certification.setName(dto.getUsername());
        certification.setSign(false);
        certification.setClientId(dto.getClientId());
        String content = JSON.toJSONString(certification);
        String token = JwtUtils.generateToken(content, privateKey, securityProperties.getTimeout());

        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setToken(token);
        userInfoDTO.setName(dto.getUsername());
        userInfoDTO.setAvatar(avatar);
        userInfoDTO.setIntroduction(introduction);
        userInfoDTO.setRoles(resourceRepository.findAll().stream().map(ResourceEntity::fullScopeName).collect(Collectors.toList()));

        authorizeInfoManager.save(token, securityProperties.getTimeout(), new CustomerInfo(), userInfoDTO.getRoles());
        return userInfoDTO;
    }

    @Override
    public Boolean logout(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);
        return StringUtils.isNotBlank(token);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean create(UserVO vo) {
        // 密码加密
        vo.setPassword(passwordEncoder.encode(vo.getPassword()));
        JsonResult<UserVO> result = userServerFeign.create(vo);
        return result.requestSuccess();
    }

    @Override
    public UserDetailsImpl findById(Long id) {
        Optional<UserEntity> optional = userRepository.findById(id);
        return optional.map(userEntity -> {
            UserDetailsImpl userDetails = new UserDetailsImpl();
            userDetails.setId(userEntity.getId());
            userDetails.setUsername(userEntity.getUsername());
            userDetails.setPassword(userEntity.getPassword());
            userDetails.setAuthorities(Collections.singletonList(new SimpleGrantedAuthority("MoneyManagement:Test:checkAccessCard")));
            return userDetails;
        }).orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean setUserRole(SetUserRoleDTO dto) {
        JsonResult<Boolean> result = userServerFeign.userIsExisted(dto.getUserId());
        if (!result.requestSuccess()) {
            return false;
        }
        ThrowUtils.throwIfTrue(!result.getData(), SecurityError.USER_NOT_FOUND);

        boolean existsById = roleRepository.existsById(dto.getRoleId());
        if (!existsById) {
            return false;
        }

        UserRoleEntity entity = new UserRoleEntity();
        entity.setUserId(dto.getUserId());
        entity.setRoleId(dto.getRoleId());

        userRoleRepository.save(entity);
        return true;
    }
}

package com.gapache.user.server.service.impl;

import com.gapache.commons.model.IPageRequest;
import com.gapache.commons.model.PageResult;
import com.gapache.commons.model.ThrowUtils;
import com.gapache.jpa.FindUtils;
import com.gapache.jpa.PageHelper;
import com.gapache.jpa.SpecificationFactory;
import com.gapache.user.common.model.UserError;
import com.gapache.user.common.model.vo.UserVO;
import com.gapache.user.server.dao.entity.UserCustomizeInfoEntity;
import com.gapache.user.server.dao.entity.UserEntity;
import com.gapache.user.server.dao.repository.UserCustomizeInfoRepository;
import com.gapache.user.server.dao.repository.UserRepository;
import com.gapache.user.server.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author HuSen
 * @since 2021/1/25 1:08 下午
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserCustomizeInfoRepository userCustomizeInfoRepository;

    public UserServiceImpl(UserRepository userRepository, UserCustomizeInfoRepository userCustomizeInfoRepository) {
        this.userRepository = userRepository;
        this.userCustomizeInfoRepository = userCustomizeInfoRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO create(UserVO vo) {
        ThrowUtils.throwIfTrue(userRepository.existsByUsername(vo.getUsername()), UserError.USERNAME_EXISTED);

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(vo.getUsername());
        userEntity.setPassword(vo.getPassword());

        userRepository.save(userEntity);

        String customizeInfo = vo.getCustomizeInfo();
        if (StringUtils.isNotBlank(customizeInfo)) {
            UserCustomizeInfoEntity userCustomizeInfoEntity = new UserCustomizeInfoEntity();
            userCustomizeInfoEntity.setInfo(customizeInfo);
            userCustomizeInfoEntity.setClientId(vo.getClient());
            userCustomizeInfoEntity.setUserId(userEntity.getId());
            userCustomizeInfoRepository.save(userCustomizeInfoEntity);
        }

        vo.setId(userEntity.getId());
        vo.setCreateTime(userEntity.getCreateTime());
        vo.setCreateBy(userEntity.getCreateBy());
        return vo;
    }

    @Override
    public Boolean userIsExisted(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public UserVO findByUsername(String username, String clientId) {
        UserEntity userEntity = userRepository.findByUsername(username);
        ThrowUtils.throwIfTrue(userEntity == null, UserError.USER_NOT_FOUND);

        UserVO vo = new UserVO();
        vo.setUsername(userEntity.getUsername());
        vo.setPassword(userEntity.getPassword());
        vo.setId(userEntity.getId());
        vo.setClient(clientId);
        vo.setCreateTime(userEntity.getCreateTime());
        vo.setCreateBy(userEntity.getCreateBy());
        vo.setLastModifiedBy(userEntity.getLastModifiedBy());
        vo.setLastModifiedTime(userEntity.getLastModifiedTime());

        if (StringUtils.isNotBlank(clientId)) {
            UserCustomizeInfoEntity userCustomizeInfoEntity = userCustomizeInfoRepository.findByUserIdAndClientId(vo.getId(), clientId);
            if (userCustomizeInfoEntity != null) {
                vo.setCustomizeInfo(userCustomizeInfoEntity.getInfo());
            }
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        userRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(UserVO vo) {
        Optional<UserEntity> optional = userRepository.findById(vo.getId());
        ThrowUtils.throwIfTrue(!optional.isPresent(), UserError.USER_NOT_FOUND);

        optional.ifPresent(entity -> {
            UserCustomizeInfoEntity userCustomizeInfoEntity = userCustomizeInfoRepository.findByUserIdAndClientId(entity.getId(), vo.getClient());
            userCustomizeInfoEntity.setInfo(vo.getCustomizeInfo());
            userCustomizeInfoRepository.save(userCustomizeInfoEntity);
        });

        return true;
    }

    @Override
    public PageResult<UserVO> page(IPageRequest<UserVO> iPageRequest) {
        Pageable pageable = PageHelper.of(iPageRequest);
        UserVO params = iPageRequest.getCustomParams();
        Page<UserEntity> page = userRepository.findAll(SpecificationFactory.produce((predicates, root, criteriaBuilder) -> {
            if (params != null) {
                if (StringUtils.isNotBlank(params.getUsername())) {
                    predicates.add(criteriaBuilder.like(root.get("username").as(String.class), FindUtils.allMatch(params.getUsername())));
                }
            }
        }), pageable);
        return PageResult.of(page.getTotalElements(), this::entity2Vo, page.getContent());
    }

    @Override
    public UserVO get(Long id, String clientId) {
        Optional<UserEntity> optional = userRepository.findById(id);
        ThrowUtils.throwIfTrue(!optional.isPresent(), UserError.USER_NOT_FOUND);

        UserEntity userEntity = optional.get();
        UserVO vo = entity2Vo(userEntity);

        if (StringUtils.isNotBlank(clientId)) {
            UserCustomizeInfoEntity customizeInfoEntity = userCustomizeInfoRepository.findByUserIdAndClientId(userEntity.getId(), clientId);
            if (customizeInfoEntity != null) {
                vo.setCustomizeInfo(customizeInfoEntity.getInfo());
            }
        }
        return vo;
    }

    private UserVO entity2Vo(UserEntity userEntity) {
        UserVO vo = new UserVO();
        vo.setId(userEntity.getId());
        vo.setUsername(userEntity.getUsername());
        vo.setCreateTime(userEntity.getCreateTime());
        vo.setLastModifiedTime(userEntity.getLastModifiedTime());
        vo.setCreateBy(userEntity.getCreateBy());
        vo.setLastModifiedBy(userEntity.getLastModifiedBy());
        return vo;
    }
}

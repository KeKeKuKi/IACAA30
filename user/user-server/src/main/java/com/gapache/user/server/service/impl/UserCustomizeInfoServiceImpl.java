package com.gapache.user.server.service.impl;

import com.gapache.user.common.model.vo.UserCustomizeInfoVO;
import com.gapache.user.server.dao.entity.UserCustomizeInfoEntity;
import com.gapache.user.server.dao.repository.UserCustomizeInfoRepository;
import com.gapache.user.server.service.UserCustomizeInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author HuSen
 * @since 2021/1/25 1:09 下午
 */
@Service
public class UserCustomizeInfoServiceImpl implements UserCustomizeInfoService {

    private final UserCustomizeInfoRepository userCustomizeInfoRepository;

    public UserCustomizeInfoServiceImpl(UserCustomizeInfoRepository userCustomizeInfoRepository) {
        this.userCustomizeInfoRepository = userCustomizeInfoRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserCustomizeInfoVO create(UserCustomizeInfoVO vo) {
        UserCustomizeInfoEntity entity = userCustomizeInfoRepository.findByUserIdAndClientId(vo.getUserId(), vo.getClientId());
        if (entity == null) {
            entity = new UserCustomizeInfoEntity();
            entity.setUserId(vo.getUserId());
            entity.setClientId(vo.getClientId());
        }
        entity.setInfo(vo.getInfo());
        userCustomizeInfoRepository.save(entity);
        vo.setId(entity.getId());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(UserCustomizeInfoVO vo) {
        Optional<UserCustomizeInfoEntity> optional = userCustomizeInfoRepository.findById(vo.getId());
        optional.ifPresent(entity -> {
            entity.setUserId(vo.getUserId());
            entity.setClientId(vo.getClientId());
            entity.setInfo(vo.getInfo());
            userCustomizeInfoRepository.save(entity);
        });
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        userCustomizeInfoRepository.deleteById(id);
        return true;
    }

    @Override
    public UserCustomizeInfoVO get(Long id) {
        return userCustomizeInfoRepository.findById(id)
                .map(entity -> {
                    UserCustomizeInfoVO vo = new UserCustomizeInfoVO();
                    BeanUtils.copyProperties(entity, vo);
                    return vo;
                })
                .orElse(null);
    }
}

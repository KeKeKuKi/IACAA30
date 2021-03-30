package com.pzhu.iacaa2_0.service.impl;

import com.pzhu.iacaa2_0.entity.GradRequirement;
import com.pzhu.iacaa2_0.entityVo.GradRequirementVo;
import com.pzhu.iacaa2_0.mapper.GradRequirementMapper;
import com.pzhu.iacaa2_0.service.IGradRequirementService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ZhaoZezhong
 * @since 2021-01-15
 */
@Service
public class GradRequirementServiceImpl extends ServiceImpl<GradRequirementMapper, GradRequirement> implements IGradRequirementService {

    @Override
    public List<GradRequirement> list(GradRequirement gradRequirement) {
        return baseMapper.getByEntity(gradRequirement);
    }

    @Override
    public List<GradRequirementVo> voList(GradRequirementVo vo) {
        return baseMapper.voList(vo);
    }
}

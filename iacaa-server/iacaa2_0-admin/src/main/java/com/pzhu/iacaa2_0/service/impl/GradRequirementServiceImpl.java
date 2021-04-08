package com.pzhu.iacaa2_0.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pzhu.iacaa2_0.entity.GradRequirement;
import com.pzhu.iacaa2_0.entityVo.GradRequirementVo;
import com.pzhu.iacaa2_0.mapper.GradRequirementMapper;
import com.pzhu.iacaa2_0.service.IGradRequirementService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pzhu.iacaa2_0.service.ITargetService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    @Autowired
    ITargetService targetService;

    @Override
    public List<GradRequirement> list(GradRequirementVo vo) {
        return baseMapper.listByVo(vo);
    }

    @Override
    public List<GradRequirementVo> voList(GradRequirementVo vo) {
        return baseMapper.voList(vo);
    }

    @Override
    @Transactional
    public Boolean summaryThisYearReqGrade() {
        //首先统计本年度指标点分数
        targetService.summaryThisYearTargetsGrade();

        //其次统计本年度毕业要求分数
        QueryWrapper<GradRequirement> gradRequirementQueryWrapper = new QueryWrapper<>();
        gradRequirementQueryWrapper.eq("year", LocalDateTime.now().getYear());
        List<GradRequirement> gradRequirements = baseMapper.selectList(gradRequirementQueryWrapper);
        gradRequirements.forEach(i -> {
            baseMapper.summaryByReqId(i.getId());
        });
        return true;
    }
}

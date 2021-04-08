package com.pzhu.iacaa2_0.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pzhu.iacaa2_0.entity.CourseTarget;
import com.pzhu.iacaa2_0.entity.Target;
import com.pzhu.iacaa2_0.entityVo.TargetVo;
import com.pzhu.iacaa2_0.mapper.TargetMapper;
import com.pzhu.iacaa2_0.service.ICourseTaskService;
import com.pzhu.iacaa2_0.service.ITargetService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class TargetServiceImpl extends ServiceImpl<TargetMapper, Target> implements ITargetService {
    @Autowired
    ICourseTaskService courseTaskService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean summaryThisYearTargetsGrade() {
        //首先统计课程目标分数
        courseTaskService.summaryCourseTask();

        //其次统计指标点分数
        QueryWrapper<Target> targetQueryWrapper = new QueryWrapper<>();
        targetQueryWrapper.eq("year", LocalDateTime.now().getYear());

        List<Target> targets = baseMapper.selectList(targetQueryWrapper);
        targets.forEach(i -> {
            baseMapper.summaryByTargetId(i.getId());
        });

        return true;
    }

    @Override
    public List<Target> list(TargetVo vo) {
        return baseMapper.list(vo);
    }
}

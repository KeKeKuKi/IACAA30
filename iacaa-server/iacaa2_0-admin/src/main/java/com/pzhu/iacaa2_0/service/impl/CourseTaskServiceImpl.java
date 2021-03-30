package com.pzhu.iacaa2_0.service.impl;

import com.pzhu.iacaa2_0.common.ActionResult;
import com.pzhu.iacaa2_0.entity.CourseTask;
import com.pzhu.iacaa2_0.entityVo.CourseTaskVo;
import com.pzhu.iacaa2_0.mapper.CourseTaskMapper;
import com.pzhu.iacaa2_0.service.ICourseTaskService;
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
public class CourseTaskServiceImpl extends ServiceImpl<CourseTaskMapper, CourseTask> implements ICourseTaskService {

    @Override
    public List<CourseTaskVo> voList(CourseTask courseTask) {
        return baseMapper.voList(courseTask);
    }
}

package com.pzhu.iacaa2_0.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pzhu.iacaa2_0.entity.CourseTarget;
import com.pzhu.iacaa2_0.entity.Target;
import com.pzhu.iacaa2_0.mapper.TargetMapper;
import com.pzhu.iacaa2_0.service.ITargetService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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
}

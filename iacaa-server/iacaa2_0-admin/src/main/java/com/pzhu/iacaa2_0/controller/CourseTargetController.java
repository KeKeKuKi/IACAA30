package com.pzhu.iacaa2_0.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pzhu.iacaa2_0.common.ActionResult;
import com.pzhu.iacaa2_0.entity.CourseTarget;
import com.pzhu.iacaa2_0.entity.CourseTask;
import com.pzhu.iacaa2_0.entityVo.CourseTargetVo;
import com.pzhu.iacaa2_0.service.ICourseTargetService;
import com.pzhu.iacaa2_0.service.ICourseTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ZhaoZezhong
 * @since 2021-01-15
 */
@RestController
@RequestMapping("/courseTarget")
public class CourseTargetController {
    @Autowired
    ICourseTargetService courseTargetService;

    @Autowired
    ICourseTaskService courseTaskService;

    @RequestMapping("/list")
    public ActionResult list(@RequestBody CourseTarget courseTarget){
        QueryWrapper<CourseTarget> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(courseTarget.getTargetId())){
            wrapper.like("target_id",courseTarget.getTargetId());
        }
        if(!StringUtils.isEmpty(courseTarget.getCourseId())){
            wrapper.like("course_id",courseTarget.getCourseId());
        }
        List<CourseTarget> list = courseTargetService.list(wrapper);
        return ActionResult.ofSuccess(list);
    }

    @RequestMapping("/voList")
    public ActionResult voList(@RequestBody CourseTarget courseTarget){
        List<CourseTargetVo> volist = courseTargetService.volist(courseTarget);
        return ActionResult.ofSuccess(volist);
    }

    @RequestMapping("/saveOrUpdate")
    public ActionResult saveOrUpdate(@RequestBody List<CourseTargetVo> vos){
        List<CourseTarget> courseTargets = new ArrayList<>();
        AtomicReference<Float> totalMix = new AtomicReference<>((float) 0);
        Map<String,String> checkName = new HashMap<>();
        AtomicBoolean nameOk = new AtomicBoolean(true);
        vos.forEach(i -> {
            if(checkName.get(i.getCourse().getId().toString()) != null){
                nameOk.set(false);
            }else {
                checkName.put(i.getCourse().getId().toString(),"have");
            }
            totalMix.updateAndGet(v -> (float) (v + i.getMix()));
            CourseTarget courseTarget = new CourseTarget();
            courseTarget.setUpdateDate(LocalDateTime.now());
            if(null != i.getId()){
                courseTarget.setId(i.getId());
            }else {
                courseTarget.setCreatedDate(LocalDateTime.now());
            }
            courseTarget.setCourseId(i.getCourse().getId());
            courseTarget.setTargetId(i.getTarget().getId());
            courseTarget.setMix(i.getMix());
            courseTargets.add(courseTarget);
        });
        if(!nameOk.get()){
            return ActionResult.ofFail(200,"课程重复");
        }
        if(totalMix.get() > 1.000001){
            return ActionResult.ofFail(200,"权重总和不能大于1");
        }
        if(totalMix.get() < 0){
            return ActionResult.ofFail(200,"权重总和不能小于0");
        }
        boolean b = courseTargetService.saveOrUpdateBatch(courseTargets);
        return b ? ActionResult.ofSuccess() : ActionResult.ofFail(200,"后台异常，更新失败");
    }

    @RequestMapping("/deleteOne")
    public ActionResult deleteOne(@RequestBody CourseTargetVo courseTargetVo){
        CourseTarget target = courseTargetService.getById(courseTargetVo.getId());
        QueryWrapper<CourseTask> courseTaskQueryWrapper = new QueryWrapper<>();
        courseTaskQueryWrapper.eq("course_id",target.getCourseId());
        courseTaskQueryWrapper.eq("target_id",target.getTargetId());
        boolean remove = courseTaskService.remove(courseTaskQueryWrapper);
        boolean b = courseTargetService.removeById(courseTargetVo.getId());
        return b && remove ? ActionResult.ofSuccess() : ActionResult.ofFail("删除失败");
    }
}

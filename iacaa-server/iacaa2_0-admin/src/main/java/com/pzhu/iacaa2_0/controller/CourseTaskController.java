package com.pzhu.iacaa2_0.controller;


import com.pzhu.iacaa2_0.common.ActionResult;
import com.pzhu.iacaa2_0.entity.CourseTask;
import com.pzhu.iacaa2_0.entityVo.CheckLinkVo;
import com.pzhu.iacaa2_0.entityVo.CourseTaskVo;
import com.pzhu.iacaa2_0.service.ICourseTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
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
@RequestMapping("/courseTask")
public class CourseTaskController {
    @Autowired
    ICourseTaskService courseTaskService;

    @RequestMapping("/voList")
    public ActionResult voList(@RequestBody CourseTask courseTask){
        List<CourseTaskVo> courseTaskVos = courseTaskService.voList(courseTask);
        return ActionResult.ofSuccess(courseTaskVos);
    }

    @RequestMapping("/delete")
    public ActionResult delete(@RequestBody CourseTask courseTask){
        boolean b = courseTaskService.removeById(courseTask.getId());
        return b ? ActionResult.ofSuccess() : ActionResult.ofFail("删除失败");
    }


    @RequestMapping("/saveOrUpdate")
    public ActionResult saveOrUpdate(@RequestBody List<CourseTaskVo> courseTasks){
        List<CourseTask> tasks = new ArrayList<>();
        Map<String,Double> checkMap = new HashMap();
        AtomicReference<Boolean> able = new AtomicReference<>(true);
        courseTasks.forEach(i -> {
            String key = String.format("%S%S",i.getCourse().getId(),i.getTarget().getId());
            if(checkMap.get(key) == null){
                checkMap.put(key,i.getMix());
            }else {
                checkMap.put(key, i.getMix() + checkMap.get(key));
                if(checkMap.get(key) > 1.01D){
                    able.set(false);
                }
                if(checkMap.get(key) < 0.01D){
                    able.set(false);
                }
            }
            CourseTask courseTask = new CourseTask();
            courseTask.setUpdateDate(LocalDateTime.now());
            if(i.getCourseId() != null){
                courseTask.setId(i.getId());
            }else {
                courseTask.setCreatedDate(LocalDateTime.now());
            }
            courseTask.setCourseId(i.getCourse().getId().intValue());
            courseTask.setMix(i.getMix());
            courseTask.setTargetId(i.getTarget().getId().intValue());
            courseTask.setDescribes(i.getDescribes());
            courseTask.setYear(LocalDateTime.now().getYear());
            tasks.add(courseTask);
        });
        if(able.get()){
            boolean b = courseTaskService.saveOrUpdateBatch(tasks);
            return b ? ActionResult.ofSuccess() : ActionResult.ofFail(200,"更新失败");
        }else {
            return ActionResult.ofFail(200,"所支撑单个指标点权重总和需小于1大于0");
        }
    }
}

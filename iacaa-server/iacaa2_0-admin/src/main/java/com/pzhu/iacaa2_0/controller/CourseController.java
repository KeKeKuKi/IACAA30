package com.pzhu.iacaa2_0.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pzhu.iacaa2_0.base.PageBaseController;
import com.pzhu.iacaa2_0.common.ActionResult;
import com.pzhu.iacaa2_0.entity.Course;
import com.pzhu.iacaa2_0.entityVo.CourseVo;
import com.pzhu.iacaa2_0.entityVo.IdsVo;
import com.pzhu.iacaa2_0.service.ICourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ZhaoZezhong
 * @since 2021-01-15
 */
@RestController
@RequestMapping("/course")
public class CourseController extends PageBaseController {

    @Autowired
    ICourseService courseService;

    @RequestMapping("/list")
    public ActionResult list(@RequestBody Course course) throws Exception{
        List<Course> list = courseService.list();
        return ActionResult.ofSuccess(list);
    }

    @RequestMapping("/voList")
    public ActionResult list(@RequestBody CourseVo vo) throws Exception{

        PageHelper.startPage(vo.getPageNum(),vo.getPageSize());
        List<CourseVo> list = courseService.voList(vo);
        PageInfo page = new PageInfo(list);
        return ActionResult.ofSuccess(page);
    }

    @RequestMapping("/save")
    public ActionResult save(@RequestBody Course course){
        course.setCreatedDate(LocalDateTime.now());
        course.setUpdateDate(LocalDateTime.now());
        boolean update = courseService.save(course);
        return update ? ActionResult.ofSuccess() : ActionResult.ofFail(200,"添加失败");
    }

    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("/del")
    public ActionResult del(@RequestBody IdsVo ids){
        for (String id : ids.getIds()) {
            courseService.removeById(id);
        }
        return ActionResult.ofSuccess();
    }
}

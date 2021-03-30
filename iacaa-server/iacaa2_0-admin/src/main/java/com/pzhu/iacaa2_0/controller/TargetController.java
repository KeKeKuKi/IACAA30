package com.pzhu.iacaa2_0.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pzhu.iacaa2_0.common.ActionResult;
import com.pzhu.iacaa2_0.entity.GradRequirement;
import com.pzhu.iacaa2_0.entity.Target;
import com.pzhu.iacaa2_0.entityVo.GradRequirementVo;
import com.pzhu.iacaa2_0.entityVo.IdsVo;
import com.pzhu.iacaa2_0.entityVo.TargetVo;
import com.pzhu.iacaa2_0.service.IGradRequirementService;
import com.pzhu.iacaa2_0.service.ITargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
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
@RequestMapping("/target")
public class TargetController {
    @Autowired
    ITargetService targetService;

    @RequestMapping("/pageList")
    public ActionResult list(@RequestBody TargetVo vo){
        QueryWrapper<Target> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(vo.getWord())){
            wrapper.like("discribe",vo.getWord());
        }
        if(!StringUtils.isEmpty(vo.getYear())){
            wrapper.eq("year",vo.getYear());
        }
        if(!StringUtils.isEmpty(vo.getReqId())){
            wrapper.eq("req_id",vo.getReqId());
        }
        PageHelper.startPage(vo.getPageNum(),vo.getPageSize());
        List<Target> list = targetService.list(wrapper);
        PageInfo page = new PageInfo(list);
        return ActionResult.ofSuccess(page);
    }

    @RequestMapping("/list")
    public ActionResult pageList(@RequestBody TargetVo vo){
        QueryWrapper<Target> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(vo.getWord())){
            wrapper.like("discribe",vo.getWord());
        }
        if(!StringUtils.isEmpty(vo.getYear())){
            wrapper.eq("year",vo.getYear());
        }
        if(!StringUtils.isEmpty(vo.getReqId())){
            wrapper.eq("req_id",vo.getReqId());
        }
        List<Target> list = targetService.list(wrapper);
        return ActionResult.ofSuccess(list);
    }

    @RequestMapping("/update")
    public ActionResult update(@RequestBody Target target){
        target.setUpdateDate(LocalDateTime.now());
        UpdateWrapper<Target> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",target.getId());
        boolean update = targetService.update(target, updateWrapper);
        return update ? ActionResult.ofSuccess() : ActionResult.ofFail(200,"更新失败");
    }

    @RequestMapping("/save")
    public ActionResult save(@RequestBody Target target){
        if(target.getYear() == null){
            target.setYear(LocalDate.now().getYear());
        }
        target.setCreatedDate(LocalDateTime.now());
        target.setUpdateDate(LocalDateTime.now());
        boolean update = targetService.save(target);
        return update ? ActionResult.ofSuccess() : ActionResult.ofFail(200,"添加失败");
    }

    @RequestMapping("/del")
    public ActionResult del(@RequestBody IdsVo ids){
        for (String id : ids.getIds()) {
            targetService.removeById(id);
        }
        return ActionResult.ofSuccess();
    }

    @RequestMapping("/deleteOne")
    public ActionResult deleteOne(@RequestBody Target target){
        boolean b = targetService.removeById(target.getId());
        return b ? ActionResult.ofSuccess() : ActionResult.ofFail("删除失败");
    }
}

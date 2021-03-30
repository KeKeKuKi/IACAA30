package com.pzhu.iacaa2_0.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pzhu.iacaa2_0.common.ActionResult;
import com.pzhu.iacaa2_0.entity.CheckLink;
import com.pzhu.iacaa2_0.entityVo.CheckLinkVo;
import com.pzhu.iacaa2_0.service.ICheckLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
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
@RequestMapping("/checkLink")
public class CheckLinkController {
    @Autowired
    ICheckLinkService checkLinkService;

    @RequestMapping("/list")
    public ActionResult list(@RequestBody CheckLinkVo vo) throws Exception{
        List<CheckLink> list = checkLinkService.list(vo);
        return ActionResult.ofSuccess(list);
    }

    @RequestMapping("/delete")
    public ActionResult delete(@RequestBody CheckLinkVo vo){
        boolean b = checkLinkService.removeById(vo.getId());
        return b ? ActionResult.ofSuccess() : ActionResult.ofFail(200,"删除失败");
    }

    @RequestMapping("/pageList")
    public ActionResult pageList(@RequestBody CheckLinkVo vo) {
        PageHelper.startPage(vo.getPageNum(),vo.getPageSize());
        List<CheckLink> list = checkLinkService.list(vo);
        PageInfo page = new PageInfo(list);
        return ActionResult.ofSuccess(page);
    }
    @RequestMapping("/saveOrUpdate")
    public ActionResult saveOrUpdate(@RequestBody List<CheckLink> checkLinks){
        AtomicReference<Float> mixAll = new AtomicReference<>(0F);
        checkLinks.forEach(i -> {
            mixAll.set((float)(mixAll.get() + i.getMix()));
            if(i.getId() == null){
                i.setCreatedDate(LocalDateTime.now());
            }
            i.setUpdateDate(LocalDateTime.now());
        });

        if(mixAll.get() < 0.01f || mixAll.get() > 1.01f){
            return ActionResult.ofFail("权重系数不能大于1或小于0");
        }
        checkLinkService.saveOrUpdateBatch(checkLinks);
        return ActionResult.ofSuccess();
    }
}

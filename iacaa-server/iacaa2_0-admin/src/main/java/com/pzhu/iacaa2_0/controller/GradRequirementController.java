package com.pzhu.iacaa2_0.controller;


import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.gapache.security.annotation.AuthResource;
import com.gapache.security.annotation.NeedAuth;
import com.github.pagehelper.PageInfo;
import com.pzhu.iacaa2_0.common.ActionResult;
import com.pzhu.iacaa2_0.entity.GradRequirement;
import com.pzhu.iacaa2_0.entity.Target;
import com.pzhu.iacaa2_0.entityVo.GradRequirementVo;
import com.pzhu.iacaa2_0.entityVo.IdsVo;
import com.pzhu.iacaa2_0.service.IGradRequirementService;
import com.pzhu.iacaa2_0.service.ITargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ZhaoZezhong
 * @since 2021-01-15
 */
@RestController
@RequestMapping("/gradRequirement")
@NeedAuth("GradRequirement")
public class GradRequirementController{

    @Autowired
    IGradRequirementService gradRequirementService;
    @Autowired
    ITargetService targetService;

    @RequestMapping("/list")
    @SentinelResource("list")
    @AuthResource(scope = "list", name = "毕业要求列表")
    public ActionResult list(@RequestBody GradRequirementVo vo) {
        QueryWrapper<GradRequirement> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(vo.getWord())) {
            wrapper.like("name", vo.getWord()).or()
                    .like("discrible", vo.getWord());
        }
        if (!StringUtils.isEmpty(vo.getYear())) {
            wrapper.eq("year", vo.getYear());
        }
        wrapper.orderByDesc("year", "update_date");
        List<GradRequirement> list = gradRequirementService.list(wrapper);
        return ActionResult.ofSuccess(list);
    }

    @RequestMapping("/pageList")
    @AuthResource(scope = "pageList", name = "毕业要求分页列表")
    public ActionResult pageList(@RequestBody GradRequirementVo vo) {
        QueryWrapper<GradRequirement> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(vo.getWord())) {
            wrapper.like("name", vo.getWord()).or()
                    .like("discrible", vo.getWord());
        }
        if (!StringUtils.isEmpty(vo.getYear())) {
            wrapper.eq("year", vo.getYear());
        }
        wrapper.orderByDesc("year", "update_date");
//        PageHelper.startPage(vo.getPageNum(), vo.getPageSize());
        List<GradRequirement> list = gradRequirementService.list(wrapper);
        PageInfo page = new PageInfo(list);
        return ActionResult.ofSuccess(page);
    }


    @RequestMapping("/voList")
    @AuthResource(scope = "voList", name = "毕业要求Vo列表")
    public ActionResult voList(@RequestBody GradRequirementVo vo) {
//        PageHelper.startPage(vo.getPageNum(), vo.getPageSize());
        List<GradRequirementVo> list = gradRequirementService.voList(vo);
        PageInfo page = new PageInfo(list);
        return ActionResult.ofSuccess(page);
    }

    @RequestMapping("/voListAll")
    @AuthResource(scope = "voListAll", name = "毕业要求Vo全部列表")
    public ActionResult voListAll(@RequestBody GradRequirementVo vo) {
        List<GradRequirementVo> list = gradRequirementService.voList(vo);
        return ActionResult.ofSuccess(list);
    }

    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("/update")
    @AuthResource(scope = "update", name = "更新毕业要求")
    public ActionResult update(@RequestBody GradRequirementVo vo) {
        List<Target> targets = vo.getTargets();
        targets.forEach(i -> {
            if (i.getId() == null) {
                i.setCreatedDate(LocalDateTime.now());
            }
            i.setYear(LocalDate.now().getYear());
            i.setUpdateDate(LocalDateTime.now());
        });
        targetService.saveOrUpdateBatch(targets);
        vo.setUpdateDate(LocalDateTime.now());
        UpdateWrapper<GradRequirement> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", vo.getId());
        boolean update = gradRequirementService.update(vo, updateWrapper);
        return update ? ActionResult.ofSuccess() : ActionResult.ofFail(200, "更新失败");
    }

    @RequestMapping("/save")
    @AuthResource(scope = "save", name = "保存毕业要求")
    public ActionResult save(@RequestBody GradRequirement gradRequirement) {
        if (gradRequirement.getYear() == null) {
            gradRequirement.setYear(LocalDate.now().getYear());
        }
        gradRequirement.setCreatedDate(LocalDateTime.now());
        gradRequirement.setUpdateDate(LocalDateTime.now());
        boolean update = gradRequirementService.save(gradRequirement);
        return update ? ActionResult.ofSuccess() : ActionResult.ofFail(200, "添加失败");
    }

    @RequestMapping("/deleteList")
    @AuthResource(scope = "deleteList", name = "删除毕业要求列表")
    public ActionResult deleteList(@RequestBody IdsVo ids) {
        boolean b = gradRequirementService.removeByIds(ids.getIds());
        return b ? ActionResult.ofSuccess() : ActionResult.ofFail("删除失败");
    }

    @RequestMapping("/deleteOne")
    @AuthResource(scope = "deleteOne", name = "删除单个毕业要求")
    public ActionResult deleteOne(@RequestBody GradRequirement gradRequirement) {
        boolean b = gradRequirementService.removeById(gradRequirement.getId());
        return b ? ActionResult.ofSuccess() : ActionResult.ofFail("删除失败");
    }

    @RequestMapping("/exportTemplate")
    @AuthResource(scope = "exportTemplate", name = "导出模板")
    public void exportTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        File file = new File("D:/doc/" + "import" + ".xlsx");
        if (!file.exists()) {
            throw new IOException(file.getPath() + "文件不存在！");
        }
        InputStream fis = null;
        fis = new BufferedInputStream(new FileInputStream(file));
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        fis.close();
        response.reset();
        response.setContentType("application/octet-stream");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("指标点模板" + ".xlsx", "UTF-8"));
        OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
        toClient.write(buffer);
        toClient.flush();
        toClient.close();
        fis.close();

//        ExportFileUtils.export(new HashMap<>(0),"classpath:/doc","import.xlsx",response);
    }

    @RequestMapping("/summaryAll")
    public ActionResult summaryAll() {
        Boolean aBoolean = gradRequirementService.summaryThisYearReqGrade();
        return aBoolean ? ActionResult.ofSuccess() : ActionResult.ofFail("统计失败");
    }
}

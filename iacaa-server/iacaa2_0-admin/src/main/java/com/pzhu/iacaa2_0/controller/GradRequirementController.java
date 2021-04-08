package com.pzhu.iacaa2_0.controller;


import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.gapache.security.annotation.AuthResource;
import com.gapache.security.annotation.NeedAuth;
import com.pzhu.iacaa2_0.common.ActionResult;
import com.pzhu.iacaa2_0.entity.*;
import com.pzhu.iacaa2_0.entityVo.GradRequirementVo;
import com.pzhu.iacaa2_0.entityVo.IdsVo;
import com.pzhu.iacaa2_0.entityVo.TargetVo;
import com.pzhu.iacaa2_0.service.*;
import org.apache.poi.ss.formula.functions.T;
import org.checkerframework.checker.units.qual.C;
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
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Autowired
    ICourseTargetService courseTargetService;

    @Autowired
    ICourseTaskService courseTaskService;

    @Autowired
    ICheckLinkService checkLinkService;

    @RequestMapping("/list")
    @SentinelResource("list")
    @AuthResource(scope = "list", name = "毕业要求列表")
    public ActionResult list(@RequestBody GradRequirementVo vo) {
        List<GradRequirement> list = gradRequirementService.list(vo);
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
        return ActionResult.ofSuccess(list);
    }


    @RequestMapping("/voList")
    @AuthResource(scope = "voList", name = "毕业要求Vo列表")
    public ActionResult voList(@RequestBody GradRequirementVo vo) {
        List<GradRequirementVo> list = gradRequirementService.voList(vo);
        return ActionResult.ofSuccess(list);
    }

    @RequestMapping("/getOne")
    @AuthResource(scope = "getOne", name = "获取单个毕业要求")
    public ActionResult getOne(@RequestBody GradRequirementVo vo) {
        List<GradRequirementVo> list = gradRequirementService.voList(vo);
        return list == null ? ActionResult.ofFail(500,"没有该数据") : ActionResult.ofSuccess(list.get(0));
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
    }

    @RequestMapping("/summaryAll")
    public ActionResult summaryAll() {
        Boolean aBoolean = gradRequirementService.summaryThisYearReqGrade();
        return aBoolean ? ActionResult.ofSuccess() : ActionResult.ofFail("统计失败");
    }

    @RequestMapping("/randData")
    public void randData() {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        for (int year = 2019; year < 2022; year++) {
            GradRequirementVo vo = new GradRequirementVo();
            vo.setYear(year);
            List<GradRequirement> list = gradRequirementService.list(vo);
            int finalYear = year;
            list.forEach(req -> {
                int count = (int)((Math.random()) * 3 + 2);
                List<Target> targets = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    Target target = new Target();
                    target.setYear(finalYear);
                    target.setReqId(req.getId().intValue());
                    target.setDiscribe(getString());
                    targets.add(target);
                    target.setCreatedDate(LocalDateTime.now());
                    target.setUpdateDate(LocalDateTime.now());
                }
                targets.forEach(target -> {
                    targetService.save(target);
                });
            });

            TargetVo query = new TargetVo();
            query.setYear(year);
            List<Target> list1 = targetService.list(query);
            int finalYear1 = year;
            list1.forEach(target -> {
                List<CourseTarget> courseTargets = new ArrayList<>();
                Double allMix = 1D;
                long courseId = (long)(Math.random() * 49 + 1);
                while (allMix > 0.1){
                    double mix = Double.parseDouble(nf.format(Math.random()/2 + 0.1));
                    CourseTarget courseTarget = new CourseTarget();
                    courseTarget.setTargetId(target.getId());
                    courseTarget.setCourseId(courseId%50+1);
                    courseTarget.setCreatedDate(LocalDateTime.now());
                    courseTarget.setUpdateDate(LocalDateTime.now());
                    if(allMix - mix > 0.1){
                        allMix -= mix;
                        courseTarget.setMix(mix);
                    }else {
                        courseTarget.setMix(Double.parseDouble(nf.format(allMix)));
                        allMix -= mix;
                    }
                    courseTargets.add(courseTarget);
                    List<CourseTask> courseTasks = new ArrayList<>();
                    Double allMix2 = 1D;
                    while (allMix2 > 0.1){
                        double mix2 = Double.parseDouble(nf.format(Math.random()/2 + 0.3));
                        CourseTask courseTask = new CourseTask();
                        courseTask.setYear(finalYear1);
                        courseTask.setCourseId((int)courseId%50+1);
                        courseTask.setDescribes(getString());
                        courseTask.setCreatedDate(LocalDateTime.now());
                        courseTask.setUpdateDate(LocalDateTime.now());
                        courseTask.setTargetId(target.getId().intValue());
                        if(allMix2 - mix2 > 0.1){
                            courseTask.setMix(mix2);
                            allMix2 -= mix2;
                        }else {
                            courseTask.setMix(Double.parseDouble(nf.format(allMix2)));
                            allMix2 -= mix2;
                        }
                        courseTasks.add(courseTask);
                    }
                    courseTaskService.saveBatch(courseTasks);
                    courseId += (Math.random() * 6 + 1);
                }
                courseTargetService.saveBatch(courseTargets);
            });
        }
        List<CourseTask> tasks = courseTaskService.list();
        tasks.forEach(courseTask -> {
            List<CheckLink> checkLinks = new ArrayList<>();
            Double allMix = 1D;
            while (allMix > 0.1){
                double mix = Double.parseDouble(nf.format(Math.random()/3 + 0.1));
                CheckLink checkLink = new CheckLink();
                checkLink.setCreatedDate(LocalDateTime.now());
                checkLink.setUpdateDate(LocalDateTime.now());
                checkLink.setTargetScore(100D);
                checkLink.setName(getString().substring(0,5));
                checkLink.setAverageScore(Math.random()*50 + 40);
                checkLink.setTaskId(courseTask.getId().intValue());
                if(allMix - mix > 0.1){
                    checkLink.setMix(mix);
                    allMix -= mix;
                }else {
                    checkLink.setMix(Double.parseDouble(nf.format(allMix)));
                    allMix -= mix;
                }
                checkLinks.add(checkLink);
            }
            checkLinkService.saveBatch(checkLinks);
        });
    }

    private String getString(){
        int count = (int)((Math.random())/1 * 30 + 12);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            stringBuilder.append((char)((int)((Math.random())/1 * (40869 - 19968) + 19968)));
        }
        return stringBuilder.toString();
    }
}

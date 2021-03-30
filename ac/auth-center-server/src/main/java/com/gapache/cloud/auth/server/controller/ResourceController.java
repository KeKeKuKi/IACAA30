package com.gapache.cloud.auth.server.controller;

import com.gapache.security.interfaces.ResourceReceiver;
import com.gapache.commons.model.JsonResult;
import com.gapache.security.model.ResourceReportDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HuSen
 * @since 2020/8/6 6:02 下午
 */
@RestController
@RequestMapping("/api/resource")
public class ResourceController {

    private final ResourceReceiver resourceReceiver;

    public ResourceController(ResourceReceiver resourceReceiver) {
        this.resourceReceiver = resourceReceiver;
    }

    @PostMapping("/receiveReportData")
    public JsonResult<Boolean> receiveReportData(@RequestBody ResourceReportDTO reportData) {
        return JsonResult.of(resourceReceiver.receiveReportData(reportData));
    }
}

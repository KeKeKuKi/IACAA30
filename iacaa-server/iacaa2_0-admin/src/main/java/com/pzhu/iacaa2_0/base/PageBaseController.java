package com.pzhu.iacaa2_0.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

public class PageBaseController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final String PageSize = "20";

    protected final String PageNum = "1";

    @ExceptionHandler
    @ResponseBody
    public void ExceHandler(Exception e){
        e.printStackTrace();
        logger.error(e.getMessage());
    }
}

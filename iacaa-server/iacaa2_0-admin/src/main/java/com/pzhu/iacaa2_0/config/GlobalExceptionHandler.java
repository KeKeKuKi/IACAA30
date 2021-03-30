package com.pzhu.iacaa2_0.config;


import com.pzhu.iacaa2_0.common.ActionResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author MrZhao
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = ShiroException.class)
    public ActionResult handler(ShiroException e) {
        log.error("运行时异常：----------------{}", e);
        return ActionResult.ofFail(401,"权限异常");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ActionResult handler(MethodArgumentNotValidException e) {
        log.error("实体校验异常：----------------{}", e);
        BindingResult bindingResult = e.getBindingResult();
        ObjectError objectError = bindingResult.getAllErrors().stream().findFirst().get();

        return ActionResult.ofFail(1001,"参数异常");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ActionResult handler(IllegalArgumentException e) {
        log.error("Assert异常：----------------{}", e);
        return ActionResult.ofFail(1002,"Assert异常");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = RuntimeException.class)
    public ActionResult handler(RuntimeException e) {
        log.error("运行时异常：----------------{}", e);
        return ActionResult.ofFail(500,"系统异常");
    }

}

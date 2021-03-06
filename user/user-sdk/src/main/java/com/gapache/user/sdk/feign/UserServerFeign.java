package com.gapache.user.sdk.feign;

import com.gapache.commons.model.JsonResult;
import com.gapache.user.common.model.vo.UserVO;
import com.gapache.user.sdk.fallback.UserServerFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author HuSen
 * @since 2020/8/26 9:33 上午
 */
@FeignClient(value = "user-server", fallback = UserServerFeignFallback.class)
public interface UserServerFeign {

    @PostMapping("/api/user")
    JsonResult<UserVO> create(@RequestBody UserVO vo);

    @GetMapping("/api/user/userIsExisted/{id}")
    JsonResult<Boolean> userIsExisted(@PathVariable Long id);

    @GetMapping("/api/user/findByUsername/{username}")
    JsonResult<UserVO> findByUsername(@PathVariable String username, @RequestParam(required = false) String clientId);

    @GetMapping("/test/degrade2")
    JsonResult<String> degrade2(@RequestParam int a);
}

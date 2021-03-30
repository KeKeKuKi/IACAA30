package com.gapache.user.common.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author HuSen
 * @since 2021/1/25 1:17 下午
 */
@Setter
@Getter
@ToString
public class UserVO implements Serializable {

    private static final long serialVersionUID = -4233986831030120126L;

    private Long id;

    /**
     * 用户名
     */
    @NotBlank
    private String username;

    /**
     * 密码
     */
    @NotBlank
    private String password;

    /**
     * 申请创建用户的client
     */
    @NotBlank
    private String client;

    /**
     * 自定义信息
     */
    private String customizeInfo;

    private LocalDateTime createTime;

    private LocalDateTime lastModifiedTime;

    private Long createBy;

    private Long lastModifiedBy;
}

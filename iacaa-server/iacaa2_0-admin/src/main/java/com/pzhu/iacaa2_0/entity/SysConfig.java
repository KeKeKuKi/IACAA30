package com.pzhu.iacaa2_0.entity;

import com.pzhu.iacaa2_0.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author ZhaoZezhong
 * @since 2021-01-15
 */
@Data
@EqualsAndHashCode()
@TableName("t_sys_config")
public class SysConfig {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 配置名
     */
    @TableField("config_name")
    private String configName;

    /**
     * 配置值
     */
    @TableField("config_data")
    private Integer configData;


}

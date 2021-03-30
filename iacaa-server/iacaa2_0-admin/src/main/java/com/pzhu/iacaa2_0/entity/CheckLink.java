package com.pzhu.iacaa2_0.entity;

import com.pzhu.iacaa2_0.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
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
@EqualsAndHashCode(callSuper = true)
@TableName("t_check_link")
public class CheckLink extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 对应课程目标
     */
    @TableField("task_id")
    private Integer taskId;

    /**
     * 标题
     */
    @TableField("name")
    private String name;

    /**
     * 目标成绩
     */
    @TableField("target_score")
    private Double targetScore;

    /**
     * 平均成绩
     */
    @TableField("average_score")
    private Double averageScore;

    /**
     * 占比
     */
    @TableField("mix")
    private Double mix;

}

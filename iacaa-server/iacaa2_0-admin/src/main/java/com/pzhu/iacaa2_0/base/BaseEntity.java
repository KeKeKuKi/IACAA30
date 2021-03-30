package com.pzhu.iacaa2_0.base;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseEntity {
    /**
     * 唯一标识
     */
    //去除Myatisplus生成id
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    @TableField("created_date")
    private LocalDateTime createdDate;

    /**
     * 更新时间
     */
    @TableField("update_date")
    private LocalDateTime updateDate;
//
//    private int updateId;
//
//    private Date udpateTime;
//
//    private int createId;
//
//    private Date createTime;
}

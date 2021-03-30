package com.pzhu.iacaa2_0.entityVo;

import com.pzhu.iacaa2_0.entity.Target;
import lombok.Data;

/**
 * @author MrZhao
 */
@Data
public class TargetVo extends Target {
    private String reqName;
    private Integer pageNum = 1;
    private Integer pageSize = 20;
    private String word;
}

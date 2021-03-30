package com.pzhu.iacaa2_0.entityVo;

import com.pzhu.iacaa2_0.entity.Course;
import com.pzhu.iacaa2_0.entity.CourseTask;
import lombok.Data;
import java.util.List;

/**
 * @author MrZhao
 */
@Data
public class CourseVo extends Course {
    private Integer pageNum = 1;
    private Integer pageSize = 20;
    private String word;
    List<CourseTask> courseTasks;
}

package com.mmall.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @Author: zhangyu
 * @Description: 部门参数类
 * @Date: in 2019/8/25 17:00
 */
@Getter
@Setter
@ToString
public class DeptParam {

    private Integer id;

    @NotBlank(message = "部门名称不能为空")
    @Length(max = 15, min = 1, message = "部门名称的长度需要在1-15之间")
    private String name;

    private Integer parentId = 0;

    @NotNull(message = "部门排序号不能为空")
    private Integer seq;

    @Length(max = 150, message = "备注的长度需要在150以内")
    private String remark;

}

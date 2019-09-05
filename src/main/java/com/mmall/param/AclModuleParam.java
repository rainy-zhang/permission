package com.mmall.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Author: zhangyu
 * @Description: 权限模块参数类
 * @Date: in 2019/9/2 18:51
 */
@Getter
@Setter
@ToString
public class AclModuleParam {

    private Integer id;

    @NotBlank(message = "权限模块的名称不可以为空")
    @Length(min = 1, max = 20, message = "权限模块名称的长度需要在1-20个字之间")
    private String name;

    private Integer parentId = 0;

    @NotNull(message = "权限模块的排序号不可以为空")
    private Integer seq;

    @NotNull(message = "权限模块的状态不可以为空")
    @Max(value = 1, message = "权限模块的状态不合法")
    @Min(value = 0, message = "权限模块的状态不合法")
    private Integer status;

    @Length(max = 200,message = "权限模块的备注需要在200个字以内")
    private String remark;

}

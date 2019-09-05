package com.mmall.param;

import lombok.Builder;
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
 * @Description: 权限点参数类
 * @Date: in 2019/9/3 15:21
 */
@Getter
@Setter
@ToString
public class AclParam {

    private Integer id;

    @NotBlank(message = "权限名称不可以为空")
    @Length(min = 1, max = 20, message = "权限名称的长度在1-20之间")
    private String name;

    @NotNull(message = "必须制定权限模块")
    private Integer aclModuleId;

    @NotBlank(message = "权限url不可以为空")
    @Length(min = 1, max = 100, message = "权限url的长度在1-100之间")
    private String url;

    @NotNull(message = "权限类型不可以为空")
    @Min(value = 1, message = "权限类型不合法")
    @Max(value = 3, message = "权限类型不合法")
    private Integer type;

    @NotNull(message = "权限状态不可以为空")
    @Min(value = 0, message = "权限状态不合法")
    @Max(value = 1, message = "权限状态不合法")
    private Integer status;

    @NotNull(message = "权限排序号不可以为空")
    private Integer seq;

    @Length(max = 200,message = "权限备注的长度在200个字以内")
    private String remark;

}

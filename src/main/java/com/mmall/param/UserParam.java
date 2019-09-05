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
 * @Description: 用户参数类
 * @Date: in 2019/8/27 21:39
 */
@Getter
@Setter
@ToString
public class UserParam {

    private Integer id;

    @NotBlank(message = "用户名不能为空")
    @Length(min = 1, max = 20, message = "用户名长度需要在1-20以内")
    private String username;

    @NotBlank(message = "电话不能为空")
    @Length(min = 5, max = 11, message = "请检查电话长度是否正确")
    private String telephone;

    @NotBlank(message = "邮箱不能为空")
    @Length(min = 5, max = 50, message = "邮箱长度需要在5-50以内")
    private String email;

    @NotNull(message = "所在部门为空, 请选择所在部门")
    private Integer deptId;

    @NotNull(message = "用户状态不能为空")
    @Min(message = "用户状态不合法",value = 0)
    @Max(message = "用户状态不合法", value = 2)
    private Integer status;



}

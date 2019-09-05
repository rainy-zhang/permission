package com.mmall.param;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @Author: zhangyu
 * @Description: 角色参数类
 * @Date: in 2019/9/4 9:15
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleParam {

    private Integer id;

    @NotBlank(message = "角色名称不可以为空")
    @Length(min = 1, max = 20, message = "角色名称的长度在1-20之间")
    private String name;

    @Min(value = 1, message = "角色类型不合法")
    @Max(value = 2, message = "角色类型不合法")
    private Integer type = 1;

    @Min(value = 0, message = "角色状态不合法")
    @Max(value = 1, message = "角色状态不合法")
    private Integer status = 1;

    @Length(max = 200, message = "角色备注长度在200字以内")
    private String remark;

}

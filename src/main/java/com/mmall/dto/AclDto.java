package com.mmall.dto;

import com.mmall.model.SysAcl;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

/**
 * @Author: zhangyu
 * @Description:
 * @Date: in 2019/9/4 15:02
 */
@Getter
@Setter
@ToString
public class AclDto extends SysAcl {

    private boolean checked = false;    //是否默认选中

    private boolean hashAcl = false;    //是否有权限操作

    public static AclDto adapt(SysAcl acl){
        AclDto dto = new AclDto();
        BeanUtils.copyProperties(acl, dto);
        return dto;
    }

}

package com.mmall.dto;

import com.google.common.collect.Lists;
import com.mmall.model.SysAclModule;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @Author: zhangyu
 * @Description:
 * @Date: in 2019/9/2 20:05
 */
@Getter
@Setter
public class AclModuleLevelDto extends SysAclModule {

    //用来存储下级的aclModule
    private List<AclModuleLevelDto> aclModuleDtoList = Lists.newArrayList();

    //用来存储权限模块下的权限点列表
    private List<AclDto> aclDtoList = Lists.newArrayList();

    public static AclModuleLevelDto adapt(SysAclModule aclModule){
        AclModuleLevelDto aclModuleDto = new AclModuleLevelDto();
        BeanUtils.copyProperties(aclModule, aclModuleDto);
        return aclModuleDto;
    }

}

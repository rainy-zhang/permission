package com.mmall.dto;

import com.google.common.collect.Lists;
import com.mmall.model.SysDept;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @Author: zhangyu
 * @Description: dept适配类. 用于封装部门树
 * @Date: in 2019/8/25 17:32
 */
@Getter
@Setter
@ToString
public class DeptLevelDto extends SysDept {

    /**
     * 用于存放所有的下级部门
     */
    private List<DeptLevelDto> deptList = Lists.newArrayList();

    /**
     * 将dept转为deptLevelDto
     */
    public static DeptLevelDto adept(SysDept dept){
        DeptLevelDto dto = new DeptLevelDto();
        BeanUtils.copyProperties(dept, dto);
        return dto;
    }

}

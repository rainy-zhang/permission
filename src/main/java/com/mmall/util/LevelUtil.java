package com.mmall.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author: zhangyu
 * @Description: 用于处理部门level字段
 * @Date: in 2019/8/25 17:13
 */
public class LevelUtil {

    /**
     * level的分隔符
     */
    public final static String SEPARATOR = ".";

    /**
     * 顶级部门的level
     */
    public final static String ROOT = "0";

    /**
     * 根据level和上级部门的ID返回[ level.parentID ] 形式的字符串
     */
    public static String calculateLevel(String parentLevel, int parentId){
        if(StringUtils.isBlank(parentLevel)){
            return ROOT;
        }else{
            return StringUtils.join(parentLevel, SEPARATOR, parentId);
        }
    }

}

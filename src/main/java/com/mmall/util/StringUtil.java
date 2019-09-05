package com.mmall.util;

import com.google.common.base.Splitter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: zhangyu
 * @Description: 
 * @Date: in 2019/9/4 18:42
 */
public class StringUtil {

    /**
     * 将字符串转为Int集合
     */
    public static List<Integer> splitToListInt(String str){
        List<String> strList = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(str);
        return strList.stream().map(strItem -> Integer.parseInt(strItem)).collect(Collectors.toList());
    }

}

package com.mmall.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mmall.param.SearchLogParam;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * @Author: zhangyu
 * @Description:
 * @Date: in 2019/9/9 19:48
 */
@Getter
@Setter
@ToString
public class SearchLogDto {

    private Integer type; //LogType

    private String beforeSeg;

    private String afterSeg;

    private String operator;

    private Date fromTime;    //yyyy-MM-dd HH:mm:ss

    private Date toTime;

    public static SearchLogDto adapt (SearchLogParam param) {
        SearchLogDto dto = new SearchLogDto();
        BeanUtils.copyProperties(param, dto);
        return dto;
    }

}

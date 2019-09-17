package com.mmall.controller;

import com.mmall.beans.PageQuery;
import com.mmall.common.JsonData;
import com.mmall.param.SearchLogParam;
import com.mmall.service.SysLogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * @Author: zhangyu
 * @Description:
 * @Date: in 2019/9/8 10:33
 */
@Controller
@RequestMapping("/sys/log")
public class SysLogController {

    @Resource
    private SysLogService logService;

    @RequestMapping("/log.page")
    public ModelAndView page() {
        return new ModelAndView("log");
    }

    @ResponseBody
    @RequestMapping("/recover.json")
    public JsonData recover(@RequestParam("id") int id) {
        logService.recover(id);
        return JsonData.success();
    }

    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData searchPage(SearchLogParam param, PageQuery page) {
        return JsonData.success(logService.searchPageList(param, page));
    }

}

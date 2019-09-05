package com.mmall.controller;

import com.google.common.collect.Maps;
import com.mmall.beans.PageQuery;
import com.mmall.beans.PageResult;
import com.mmall.common.JsonData;
import com.mmall.model.SysAcl;
import com.mmall.model.SysRole;
import com.mmall.param.AclParam;
import com.mmall.service.SysAclService;
import com.mmall.service.SysRoleService;
import com.mmall.service.SysUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: zhangyu
 * @Description: 权限controller
 * @Date: in 2019/9/2 18:49
 */
@Controller
@RequestMapping("/sys/acl")
public class SysAclController {

    @Resource
    private SysAclService aclService;
    @Resource
    private SysRoleService roleService;

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveAcl(AclParam param){
        aclService.save(param);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateAcl(AclParam param){
        aclService.update(param);
        return JsonData.success();
    }

    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData page(@RequestParam("aclModuleId") Integer aclModuleId, PageQuery page){
        PageResult<SysAcl> result = aclService.getPageByAclModuleId(aclModuleId, page);
        return JsonData.success(result);
    }

    @RequestMapping("/acls.json")
    @ResponseBody
    public JsonData acls(@RequestParam("id") int id){
        List<SysRole> roleList = roleService.getRoleListByAclId(id);
        HashMap<String, Object> result = Maps.newHashMap();
        result.put("roles", roleList);
        result.put("users", roleService.getUserListByRoleList(roleList));
        return JsonData.success(result);
    }

}

package com.mmall.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.JsonData;
import com.mmall.model.SysUser;
import com.mmall.param.RoleParam;
import com.mmall.service.*;
import com.mmall.util.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @Author: zhangyu
 * @Description: 角色controller
 * @Date: in 2019/9/4 9:13
 */
@Controller
@RequestMapping("/sys/role")
public class SysRoleController {

    @Resource
    private SysRoleService roleService;
    @Resource
    private SysTreeService treeService;
    @Resource
    private SysRoleAclService roleAclService;
    @Resource
    private SysRoleUserService roleUserService;
    @Resource
    private SysUserService userService;

    @RequestMapping("/role.page")
    public ModelAndView page(){
        return new ModelAndView("role");
    }


    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveRole(RoleParam param){
        roleService.save(param);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateRole(RoleParam param){
        roleService.update(param);
        return JsonData.success();
    }

    @RequestMapping("/list.json")
    @ResponseBody
    public JsonData listRole(){
        return JsonData.success(roleService.getAll());
    }

    @RequestMapping("/roleTree.json")
    @ResponseBody
    public JsonData roleTree(@RequestParam("roleId") Integer roleId){
        return JsonData.success(treeService.roleTree(roleId));
    }

    @RequestMapping("/changeAcls.json")
    @ResponseBody
    public JsonData changeAcls(@RequestParam("roleId") Integer roleId, @RequestParam(value = "aclIds", required = false, defaultValue = "") String aclIds){
        List<Integer> aclIdList = StringUtil.splitToListInt(aclIds);
        roleAclService.changeRoleAcls(roleId, aclIdList);
        return JsonData.success();
    }

    @RequestMapping("/users.json")
    @ResponseBody
    public JsonData users(@RequestParam("roleId") Integer roleId){
        List<SysUser> selectedUserList = roleUserService.getUserListByRoleId(roleId);
        List<SysUser> allUserList = userService.getAll();

        List<SysUser> unselectedUserList = Lists.newArrayList();
        Set<Integer> selectedUserIdList = selectedUserList.stream().map(user -> user.getId()).collect(Collectors.toSet());
        for (SysUser sysUser : allUserList) {
            if(sysUser.getStatus() == 1 && !selectedUserIdList.contains(sysUser.getId())){
                unselectedUserList.add(sysUser);
            }
        }

        selectedUserList = selectedUserList.stream().filter(user -> user.getStatus() == 1).collect(Collectors.toList());

        HashMap<String,List<SysUser>> result =  Maps.newHashMap();
        result.put("selected", selectedUserList);
        result.put("unselected", unselectedUserList);
        return JsonData.success(result);
    }

    @RequestMapping("/changeUsers.json")
    @ResponseBody
    public JsonData changeUsers(@RequestParam("roleId") Integer roleId, @RequestParam(value = "userIds", required = false, defaultValue = "") String userIds){
        List<Integer> userIdList = StringUtil.splitToListInt(userIds);
        roleUserService.changeRoleUsers(roleId, userIdList);
        return JsonData.success();
    }


}

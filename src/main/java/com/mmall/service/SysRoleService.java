package com.mmall.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysRoleAclMapper;
import com.mmall.dao.SysRoleMapper;
import com.mmall.dao.SysRoleUserMapper;
import com.mmall.dao.SysUserMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysRole;
import com.mmall.model.SysUser;
import com.mmall.param.RoleParam;
import com.mmall.util.BeanValidator;
import com.mmall.util.IpUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: zhangyu
 * @Description: 角色Service
 * @Date: in 2019/9/4 9:14
 */
@Service
public class SysRoleService {

    @Resource
    private SysRoleMapper roleMapper;
    @Resource
    private SysRoleUserMapper roleUserMapper;
    @Resource
    private SysRoleAclMapper roleAclMapper;
    @Resource
    private SysUserMapper userMapper;

    /**
     * 保存角色
     */
    public void save(RoleParam param){
        BeanValidator.check(param);
        if(checkNameExist(param.getName(), param.getId())){
            throw new ParamException("角色名称不能重复!");
        }
        SysRole role = setRole(param);
        roleMapper.insertSelective(role);
    }

    /**
     * 更新角色
     */
    public void update(RoleParam param){
        BeanValidator.check(param);
        if(checkNameExist(param.getName(), param.getId())){
            throw new ParamException("角色名称不能重复!");
        }
        SysRole beforeRole = roleMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(beforeRole, "待更新的角色不存在!");
        SysRole afterRole = setRole(param);
        roleMapper.updateByPrimaryKeySelective(afterRole);
    }

    /**
     * 获取全部的角色列表
     */
    public List<SysRole> getAll() {
        return roleMapper.getAll();
    }

    /**
     * 根据userId获取用户的角色列表
     */
    public List<SysRole> getRoleListByUserId(int userId) {
        List<Integer> roleIdList = roleUserMapper.getRoleIdListByUserId(userId);
        if(CollectionUtils.isEmpty(roleIdList)){
            return Lists.newArrayList();
        }
        return roleMapper.getByIdList(roleIdList);
    }

    /**
     * 根据权限Id获取拥有这个权限的角色列表
     */
    public List<SysRole> getRoleListByAclId(int aclId){
        List<Integer> roleIdList = roleAclMapper.getRoleIdListByAclId(aclId);
        if(CollectionUtils.isEmpty(roleIdList)){
            return Lists.newArrayList();
        }
        return roleMapper.getByIdList(roleIdList);
    }

    /**
     * 根据角色列表获取拥有这些角色的用户列表
     */
    public List<SysUser> getUserListByRoleList(List<SysRole> roleList) {
        if(CollectionUtils.isEmpty(roleList)){
            return Lists.newArrayList();
        }
        List<Integer> roleIdList = roleList.stream().map(role -> role.getId()).collect(Collectors.toList());
        List<Integer> userIdList = roleUserMapper.getUserIdListByRoleIdList(roleIdList);
        if(CollectionUtils.isEmpty(userIdList)) {
            return Lists.newArrayList();
        }
        return userMapper.getByIdList(userIdList);
    }

    //检查角色名称是否存在
    private boolean checkNameExist(String name, Integer id){
        return roleMapper.countByName(name, id) > 0;
    }

    //将参数类中的数据设置到实体类中
    private SysRole setRole(RoleParam param){
        SysRole role = SysRole.builder()
                .id(param.getId())
                .name(param.getName())
                .type(param.getType())
                .status(param.getStatus())
                .remark(param.getRemark())
                .operator(RequestHolder.getCurrentUser().getUsername())
                .operatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()))
                .operatorTime(new Date())
                .build();
        return role;
    }

}

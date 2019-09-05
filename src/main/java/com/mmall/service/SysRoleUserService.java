package com.mmall.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysRoleUserMapper;
import com.mmall.dao.SysUserMapper;
import com.mmall.model.SysRoleUser;
import com.mmall.model.SysUser;
import com.mmall.util.IpUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * @Author: zhangyu
 * @Description: 处理角色与用户管理关系的service
 * @Date: in 2019/9/5 9:39
 */
@Service
public class SysRoleUserService {

    @Resource
    private SysRoleUserMapper roleUserMapper;
    @Resource
    private SysUserMapper userMapper;

    /**
     * 根据roleId获取该角色下所有的用户列表
     */
    public List<SysUser> getUserListByRoleId(int roleId){
        List<Integer> userIdList = roleUserMapper.getUserIdListByRoleId(roleId);
        if(CollectionUtils.isEmpty(userIdList)){
            return Lists.newArrayList();
        }
        return userMapper.getByIdList(userIdList);
    }

    public void changeRoleUsers(Integer roleId, List<Integer> userIdList) {
        List<Integer> originUserIdList = roleUserMapper.getUserIdListByRoleId(roleId);
        if(userIdList.size() == originUserIdList.size()){
            HashSet<Integer> originUserIdSet = Sets.newHashSet(originUserIdList);
            HashSet<Integer> userIdSet = Sets.newHashSet(userIdList);
            originUserIdSet.removeAll(userIdSet);
            if(CollectionUtils.isEmpty(originUserIdSet)){
                return;
            }
        }
        updateRoleUsers(roleId, userIdList);
    }

    @Transactional
    protected void updateRoleUsers(Integer roleId, List<Integer> userIdList){
        roleUserMapper.deleteByRoleId(roleId);
        if(CollectionUtils.isEmpty(userIdList)){
            return;
        }
        List<SysRoleUser> roleUserList = Lists.newArrayList();
        for (Integer userId : userIdList) {
            SysRoleUser roleUser = SysRoleUser.builder()
                    .roleId(roleId)
                    .userId(userId)
                    .operator(RequestHolder.getCurrentUser().getUsername())
                    .operatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()))
                    .operatorTime(new Date())
                    .build();
            roleUserList.add(roleUser);
        }

        roleUserMapper.batchInsert(roleUserList);
    }


}

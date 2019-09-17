package com.mmall.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.beans.LogType;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysLogMapper;
import com.mmall.dao.SysRoleAclMapper;
import com.mmall.model.SysLogWithBLOBs;
import com.mmall.model.SysRoleAcl;
import com.mmall.util.IpUtil;
import com.mmall.util.JsonMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @Author: zhangyu
 * @Description: 用来处理角色与权限点的管理关系
 * @Date: in 2019/9/4 19:04
 */
@Service
public class SysRoleAclService {

    @Resource
    private SysRoleAclMapper roleAclMapper;
    @Resource
    private SysLogMapper logMapper;

    /**
     * 修改角色与权限点的关联关系
     */
    public void changeRoleAcls(Integer roleId, List<Integer> aclIdList){
        List<Integer> originAclIdList = roleAclMapper.getAclIdListByRoleIdList(Lists.newArrayList(roleId));
        if(aclIdList.size() == originAclIdList.size()){
            Set<Integer> originAclIdSet = Sets.newHashSet(originAclIdList);
            Set<Integer> aclIdSet = Sets.newHashSet(aclIdList);
            originAclIdSet.removeAll(aclIdSet);
            if(CollectionUtils.isNotEmpty(originAclIdSet)){
                return;
            }
        }
        updateRoleAcls(roleId, aclIdList);
        saveRoleAclLog(roleId, originAclIdList, aclIdList);
    }

    @Transactional
    protected void updateRoleAcls(int roleId, List<Integer> aclIdList){
        roleAclMapper.deleteByRoleId(roleId);
        if(CollectionUtils.isEmpty(aclIdList)){
            return;
        }
        List<SysRoleAcl> roleAclList = Lists.newArrayList();
        for (Integer aclId : aclIdList) {
            SysRoleAcl roleAcl = SysRoleAcl.builder()
                    .roleId(roleId)
                    .aclId(aclId)
                    .operator(RequestHolder.getCurrentUser().getUsername())
                    .operatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()))
                    .operatorTime(new Date())
                    .build();
            roleAclList.add(roleAcl);
        }
        roleAclMapper.batchInsert(roleAclList);
    }

    /**
     * 保存角色与权限相关操作记录
     * @param before 操作前的
     * @param after 操作后的
     */
    public void saveRoleAclLog(int roleId, List<Integer> before, List<Integer> after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setTargetId(roleId);
        sysLog.setType(LogType.TYPE_ROLE_ACL);
        sysLog.setOldValue(before == null ? "" : JsonMapper.object2String(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.object2String(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperatorTime(new Date());
        sysLog.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setStatus(0);
        logMapper.insertSelective(sysLog);
    }

}

package com.mmall.service;

import com.google.common.collect.Lists;
import com.mmall.common.CacheKeyConstants;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysAclMapper;
import com.mmall.dao.SysRoleAclMapper;
import com.mmall.dao.SysRoleUserMapper;
import com.mmall.model.SysAcl;
import com.mmall.model.SysUser;
import com.mmall.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: zhangyu
 * @Description: 用来处理当前角色和用户已分配的权限点的service
 * @Date: in 2019/9/4 15:07
 */
@Service
public class SysCoreService {

    @Resource
    private SysAclMapper aclMapper;
    @Resource
    private SysRoleUserMapper roleUserMapper;
    @Resource
    private SysRoleAclMapper roleAclMapper;
    @Resource
    private SysCacheService cacheService;


    /**
     * 获取当前登录的用户的权限列表
     */
    public List<SysAcl> getCurrentUserAclList(){
        int userId = RequestHolder.getCurrentUser().getId();
        return getUserAclList(userId);
    }

    /**
     * 根据roleId获取角色已分配的权限点
     */
    public List<SysAcl> getRoleAclList(Integer roleId){
        //获取给定角色的权限列表
        List<Integer> aclIdList = roleAclMapper.getAclIdListByRoleIdList(Lists.newArrayList(roleId));
        if(CollectionUtils.isEmpty(aclIdList)){
            return Lists.newArrayList();
        }
        return aclMapper.getByIdList(aclIdList);
    }

    /**
     * 根据userId获取用户已分配的权限点
     */
    public List<SysAcl> getUserAclList(Integer userId){
        if(isSuperAdmin()){
            return aclMapper.getAll();
        }
        //根据userId获取该用户的角色Id列表
        List<Integer> roleIdList = roleUserMapper.getRoleIdListByUserId(userId);
        if(CollectionUtils.isEmpty(roleIdList)){
            return Lists.newArrayList();
        }
        //根据用户的角色id列表获取用户拥有的权限列表id
        List<Integer> userAclIdList = roleAclMapper.getAclIdListByRoleIdList(roleIdList);
        if(CollectionUtils.isEmpty(userAclIdList)){
            return Lists.newArrayList();
        }
        //根据权限id列表获取权限信息
        return aclMapper.getByIdList(userAclIdList);
    }

    /**
     * 判断是否是超级管理员
     */
    public boolean isSuperAdmin(){
        SysUser user = RequestHolder.getCurrentUser();
        return "admin".equals(user.getUsername());
    }

    /**
     * 根据url判断当前登录用户是否有权限访问
     */
    public boolean hasUrlAcl(String url) {
        if(isSuperAdmin()){
            return true;
        }
        List<SysAcl> aclList = aclMapper.getListByUrl(url);

        //获取当前登录用户的权限列表
        List<SysAcl> userAclList = getCurrentUserAclListFromCache();
        Set<Integer> aclIdSet = userAclList.stream().map(acl -> acl.getId()).collect(Collectors.toSet());

        //当根据url查询出的权限点列表无效时,根据该字段的结果返回true
        boolean hasValidAcl = false;
        for (SysAcl acl : aclList) {
            if(acl == null || acl.getStatus() != 1){
                continue;
            }
            hasValidAcl = true;
            if(aclIdSet.contains(acl.getId())){
                return true;
            }
        }

        if(!hasValidAcl){
            return true;
        }

        return false;
    }

    /**
     * 从cache中获取当前用户的权限列表
     */
    public List<SysAcl> getCurrentUserAclListFromCache(){
        int userId = RequestHolder.getCurrentUser().getId();
        String cacheValue = cacheService.getFromCache(CacheKeyConstants.USER_ACLS, String.valueOf(userId));
        if(StringUtils.isBlank(cacheValue)){
            List<SysAcl> aclList = getUserAclList(userId);
            if(CollectionUtils.isNotEmpty(aclList)){
                cacheService.saveCache(JsonMapper.object2String(aclList), 600, CacheKeyConstants.USER_ACLS, String.valueOf(userId));
            }
            return aclList;
        }
        return JsonMapper.string2Object(cacheValue, new TypeReference<List<SysAcl>>() {});
    }

}

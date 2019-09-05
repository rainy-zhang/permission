package com.mmall.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.beans.Email;
import com.mmall.beans.PageQuery;
import com.mmall.beans.PageResult;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysUserMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysRole;
import com.mmall.model.SysUser;
import com.mmall.param.UserParam;
import com.mmall.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @Author: zhangyu
 * @Description: 用户Service层
 * @Date: in 2019/8/27 21:45
 */
@Service
public class SysUserService {

    @Resource
    private SysUserMapper userMapper;

    /**
     * 新增用户
     */
    public void save(UserParam userParam){
        BeanValidator.check(userParam);
        checkUserByParam(userParam);
        String password = PasswordUtil.randomPassword();

        Set<String> emails = Sets.newConcurrentHashSet();
        emails.add(userParam.getEmail());
        EmailUtil.send(Email.builder().message("您的密码为: "+password + ",请尽快登录系统完善个人信息. ").subject("密码").receivers(emails).build());

        password = "123456";
        String encryptedPassword = MD5Util.encrypt(password);
        SysUser user = setUser(userParam);
        userMapper.insertSelective(user);
    }

    /**
     * 修改用户
     */
    public void update(UserParam userParam){
        BeanValidator.check(userParam);
        checkUserByParam(userParam);
        SysUser beforUser = userMapper.selectByPrimaryKey(userParam.getId());
        Preconditions.checkNotNull(beforUser, "待更新的用户不存在!");
        SysUser afterUser = setUser(userParam);
        userMapper.updateByPrimaryKeySelective(afterUser);
    }


    /**
     * 检查电话是否已使用
     */
    public boolean checkTelePhone(Integer id, String telePhone){
        return userMapper.countByTelePhone(id, telePhone) > 0;
    }

    /**
     * 检查邮箱是否已使用
     */
    public boolean checkEmail(Integer id, String email){
        return userMapper.countByEmail(id, email) > 0;
    }

    /**
     * 根据email或telephone查询用户
     */
    public SysUser findByKeyword(String keyword) {
        return userMapper.findByKeyword(keyword);
    }

    /**
     * 获取分页用户列表
     */
    public PageResult<SysUser> getPageByDeptId(int deptId, PageQuery pageQuery){
        BeanValidator.check(pageQuery);
        int count = userMapper.countByDeptId(deptId);
        if(count > 0){
            List<SysUser> userList = userMapper.getPageByDeptId(deptId, pageQuery);
            return PageResult.<SysUser>builder().data(userList).total(count).build();
        }
        return PageResult.<SysUser>builder().build();
    }

    //校验用户
    private void checkUserByParam(UserParam userParam){
        if(checkTelePhone(userParam.getId(), userParam.getTelephone())){
            throw new ParamException("电话已被使用!");
        }
        if(checkEmail(userParam.getId(), userParam.getEmail())){
            throw new ParamException("邮箱已被使用!");
        }
    }

    //将参数类中的数据设置到user中
    private SysUser setUser(UserParam param){
        SysUser user = SysUser
                .builder()
                .username(param.getUsername())
                .telephone(param.getTelephone())
                .email(param.getEmail())
                .deptId(param.getDeptId())
                .status(param.getStatus())
                .id(param.getId())
                .operator(RequestHolder.getCurrentUser().getUsername())
                .operatorTime(new Date())
                .operatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()))
                .build();
        return user;
    }

    public List<SysUser> getAll() {
        return userMapper.getAll();
    }
}

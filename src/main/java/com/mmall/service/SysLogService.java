package com.mmall.service;

import com.google.common.base.Preconditions;
import com.mmall.beans.LogType;
import com.mmall.beans.PageQuery;
import com.mmall.beans.PageResult;
import com.mmall.common.RequestHolder;
import com.mmall.dao.*;
import com.mmall.dto.SearchLogDto;
import com.mmall.exception.ParamException;
import com.mmall.model.*;
import com.mmall.param.SearchLogParam;
import com.mmall.util.BeanValidator;
import com.mmall.util.IpUtil;
import com.mmall.util.JsonMapper;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author: zhangyu
 * @Description:
 * @Date: in 2019/9/8 10:32
 */
@Service
public class SysLogService {

    @Resource
    private SysLogMapper logMapper;
    @Resource
    private SysDeptMapper deptMapper;
    @Resource
    private SysUserMapper userMapper;
    @Resource
    private SysRoleMapper roleMapper;
    @Resource
    private SysAclModuleMapper aclModuleMapper;
    @Resource
    private SysAclMapper aclMapper;
    @Resource
    private SysRoleAclMapper roleAclMapper;
    @Resource
    private SysRoleUserMapper roleUserMapper;
    @Resource
    private SysRoleAclService roleAclService;
    @Resource
    private SysRoleUserService roleUserService;

    /**
     * 还原
     * @param id
     */
    public void recover(int id) {
        SysLogWithBLOBs log = logMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(log, "未找到待还原的记录");
        switch (log.getType()) {
            case LogType.TYPE_DEPT: //还原部门
                SysDept beforeDept = deptMapper.selectByPrimaryKey(log.getTargetId());
                Preconditions.checkNotNull(beforeDept, "待更新的记录不存在!");
                if(StringUtils.isBlank(log.getNewValue()) || StringUtils.isBlank(log.getOldValue())) {
                    throw new ParamException("新增和删除不做还原!");
                }
                SysDept afterDept = JsonMapper.string2Object(log.getOldValue(), new TypeReference<SysDept>() {
                });
                deptMapper.updateByPrimaryKeySelective(afterDept);
                saveDeptLog(beforeDept,afterDept);
                break;
            case LogType.TYPE_USER: //还原用户
                SysUser beforeUser = userMapper.selectByPrimaryKey(log.getTargetId());
                Preconditions.checkNotNull(beforeUser, "待更新的记录不存在!");
                if(StringUtils.isBlank(log.getNewValue()) || StringUtils.isBlank(log.getOldValue())) {
                    throw new ParamException("新增和删除不做还原!");
                }
                SysUser afterUser = JsonMapper.string2Object(log.getOldValue(), new TypeReference<SysUser>() {
                });
                userMapper.updateByPrimaryKeySelective(afterUser);
                saveUserLog(beforeUser, afterUser);
                break;
            case LogType.TYPE_ACL_MODULE:   //还原权限模块
                SysAclModule beforeAclModule = aclModuleMapper.selectByPrimaryKey(log.getTargetId());
                Preconditions.checkNotNull(beforeAclModule, "待更新的记录不存在!");
                if(StringUtils.isBlank(log.getOldValue()) || StringUtils.isBlank(log.getNewValue())) {
                    throw new ParamException("新增和删除不做还原!");
                }
                SysAclModule afterAclModule = JsonMapper.string2Object(log.getOldValue(), new TypeReference<SysAclModule>() {
                });
                aclModuleMapper.updateByPrimaryKeySelective(afterAclModule);
                saveAclModuleLog(beforeAclModule, afterAclModule);
                break;
            case LogType.TYPE_ACL:  //还原权限点
                SysAcl beforeAcl = aclMapper.selectByPrimaryKey(log.getTargetId());
                Preconditions.checkNotNull(beforeAcl, "待更新的记录不存在!");
                if(StringUtils.isBlank(log.getOldValue()) || StringUtils.isBlank(log.getNewValue())) {
                    throw new ParamException("新增和删除不做还原!");
                }
                SysAcl afterAcl = JsonMapper.string2Object(log.getOldValue(), new TypeReference<SysAcl>() {
                });
                aclMapper.updateByPrimaryKeySelective(afterAcl);
                saveAclLog(beforeAcl, afterAcl);
                break;
            case LogType.TYPE_ROLE: //还原角色
                SysRole beforeRole = roleMapper.selectByPrimaryKey(log.getTargetId());
                Preconditions.checkNotNull(beforeRole, "待更新的记录不存在!");
                if(StringUtils.isBlank(log.getNewValue()) || StringUtils.isBlank(log.getOldValue())) {
                    throw new ParamException("新增和删除不做还原!");
                }
                SysRole afterRole = JsonMapper.string2Object(log.getOldValue(), new TypeReference<SysRole>() {
                });
                roleMapper.updateByPrimaryKeySelective(afterRole);
                saveRoleLog(beforeRole, afterRole);
                break;
            case LogType.TYPE_ROLE_ACL: //还原角色与权限点关联关系
                SysRoleAcl roleAcl = roleAclMapper.selectByPrimaryKey(log.getTargetId());
                Preconditions.checkNotNull(roleAcl, "待更新的角色不存在!");
                roleAclService.changeRoleAcls(log.getTargetId(), JsonMapper.string2Object(log.getOldValue(), new TypeReference<List<Integer>>() {
                }));
                break;
            case LogType.TYPE_ROLE_USER:    //还原角色与用户关联关系
                SysRoleUser roleUser = roleUserMapper.selectByPrimaryKey(log.getTargetId());
                Preconditions.checkNotNull(roleUser, "待更新的角色不存在!");
                roleUserService.changeRoleUsers(log.getTargetId(), JsonMapper.string2Object(log.getOldValue(), new TypeReference<List<Integer>>() {
                }));
                break;
            default:;
        }

    }

    /**
     * 查询日志分页列表
     * @param param
     * @param page
     * @return
     */
    public PageResult<SysLogWithBLOBs> searchPageList (SearchLogParam param, PageQuery page) {
        BeanValidator.check(param);
        SearchLogDto dto = new SearchLogDto();
        dto.setType(param.getType());
        if(StringUtils.isNotBlank(param.getBeforeSeg())) {
            dto.setBeforeSeg("%" + param.getBeforeSeg() + "%");
        }
        if(StringUtils.isNotBlank(param.getAfterSeg())) {
            dto.setAfterSeg("%" + param.getAfterSeg() + "%");
        }
        if(StringUtils.isNotBlank(param.getOperator())) {
            dto.setOperator("%" + param.getOperator() + "%");
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (StringUtils.isNotBlank(param.getFromTime())) {
                dto.setFromTime(sdf.parse(param.getFromTime()));
            }
            if (StringUtils.isNotBlank(param.getToTime())) {
                dto.setToTime(sdf.parse(param.getToTime()));
            }
        } catch (Exception e) {
            throw new ParamException("传入的日期格式不正确, 正确格式为: yyyy-MM-dd HH:mm:ss");
        }

        int count = logMapper.countBySearchDto();
        if (count > 0) {
            List<SysLogWithBLOBs> logList = logMapper.searchPageList(dto, page);
            return PageResult.<SysLogWithBLOBs>builder().data(logList).total(count).build();
        }

        return PageResult.<SysLogWithBLOBs>builder().build();
    }

    /**
     * 保存部门相关操作记录
     * @param before 操作前的部门
     * @param after 操作后的部门
     */
    public void saveDeptLog(SysDept before, SysDept after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setTargetId(after == null ? before.getId() : after.getId());
        sysLog.setType(LogType.TYPE_DEPT);
        sysLog.setOldValue(before == null ? "" : JsonMapper.object2String(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.object2String(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperatorTime(new Date());
        sysLog.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setStatus(0);
        logMapper.insertSelective(sysLog);
    }

    /**
     * 保存用户相关操作记录
     * @param before 操作前的用户
     * @param after 操作后的用户
     */
    public void saveUserLog(SysUser before, SysUser after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setTargetId(after == null ? before.getId() : after.getId());
        sysLog.setType(LogType.TYPE_USER);
        sysLog.setOldValue(before == null ? "" : JsonMapper.object2String(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.object2String(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperatorTime(new Date());
        sysLog.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setStatus(0);
        logMapper.insertSelective(sysLog);
    }

    /**
     * 保存权限模块相关操作记录
     * @param before 操作前的权限模块
     * @param after 操作后的权限模块
     */
    public void saveAclModuleLog(SysAclModule before, SysAclModule after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setTargetId(after == null ? before.getId() : after.getId());
        sysLog.setType(LogType.TYPE_ACL_MODULE);
        sysLog.setOldValue(before == null ? "" : JsonMapper.object2String(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.object2String(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperatorTime(new Date());
        sysLog.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setStatus(0);
        logMapper.insertSelective(sysLog);
    }

    /**
     * 保存权限相关操作记录
     * @param before 操作前的权限点
     * @param after 操作后的权限点
     */
    public void saveAclLog(SysAcl before, SysAcl after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setTargetId(after == null ? before.getId() : after.getId());
        sysLog.setType(LogType.TYPE_ACL);
        sysLog.setOldValue(before == null ? "" : JsonMapper.object2String(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.object2String(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperatorTime(new Date());
        sysLog.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setStatus(0);
        logMapper.insertSelective(sysLog);
    }

    /**
     * 保存角色相关操作记录
     * @param before 操作前的角色
     * @param after 操作后的角色
     */
    public void saveRoleLog(SysRole before, SysRole after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setTargetId(after == null ? before.getId() : after.getId());
        sysLog.setType(LogType.TYPE_ROLE);
        sysLog.setOldValue(before == null ? "" : JsonMapper.object2String(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.object2String(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperatorTime(new Date());
        sysLog.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setStatus(0);
        logMapper.insertSelective(sysLog);
    }







}

package com.mmall.service;

import com.google.common.base.Preconditions;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysAclMapper;
import com.mmall.dao.SysAclModuleMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysAclModule;
import com.mmall.param.AclModuleParam;
import com.mmall.util.BeanValidator;
import com.mmall.util.IpUtil;
import com.mmall.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Author: zhangyu
 * @Description:
 * @Date: in 2019/9/2 19:04
 */
@Service
public class SysAclModuleService {

    @Resource
    private SysAclModuleMapper aclModuleMapper;
    @Resource
    private SysAclMapper aclMapper;
    @Resource
    private SysLogService logService;

    /**
     * 新增权限模块
     */
    public void save(AclModuleParam param){
        BeanValidator.check(param);
        if(checkExist(param.getParentId(), param.getName(), param.getId())){
            throw new ParamException("同一层级下存在相同的权限模块名称");
        }
        SysAclModule sysAclModule = setAclModule(param);
        aclModuleMapper.insertSelective(sysAclModule);
        logService.saveAclModuleLog(null, sysAclModule);
    }

    /**
     * 根据Id删除权限模块
     */
    public void deleteById(int id) {
        SysAclModule aclModule = aclModuleMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(aclModule, "待删除的部门模块不存在!");
        if(aclModuleMapper.countByParentId(id) > 0){
            throw new ParamException("存在下级权限模块,无法删除");
        }
        if(aclMapper.countByAclModuleId(id) > 0){
            throw new ParamException("权限模块中存在权限点,无法删除");
        }
        aclModuleMapper.deleteByPrimaryKey(id);
        logService.saveAclModuleLog(aclModule, null);
    }

    /**
     * 更新权限模块
     */
    public void update(AclModuleParam param){
        BeanValidator.check(param);
        if(checkExist(param.getParentId(), param.getName(), param.getId())){
            throw new ParamException("同一层级下存在相同的权限模块名称");
        }
        SysAclModule before = aclModuleMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before, "待更新的权限模块不存在!");
        SysAclModule after = setAclModule(param);

        updateWithChild(before, after);
        logService.saveAclModuleLog(before, after);
    }

    //更新权限模块及其所有子权限模块
    @Transactional
    protected void updateWithChild(SysAclModule before, SysAclModule after){
        String beforLevel = before.getLevel();
        String afterLevel = after.getLevel();
        if(!beforLevel.equals(afterLevel)){
            List<SysAclModule> aclModuleList = aclModuleMapper.getChildAclModuleListByLevel(LevelUtil.calculateLevel(beforLevel, after.getId()));
            if(CollectionUtils.isNotEmpty(aclModuleList)){
                for (SysAclModule aclModule : aclModuleList) {
                    String level = aclModule.getLevel();
                    if(level.indexOf(beforLevel) == 0){
                        level = afterLevel + beforLevel.substring(beforLevel.length());
                        aclModule.setLevel(level);
                    }
                }
                aclModuleMapper.batchUpdateLevel(aclModuleList);
            }
        }
        aclModuleMapper.updateByPrimaryKeySelective(after);
    }

    //判断同一级权限模块下是否存在相同名称的权限模块
    private boolean checkExist(Integer parentID, String name, Integer id){
        return aclModuleMapper.countByNameAndParent(parentID, name, id) > 0;
    }

    //根据ID获取level
    private String getLevel(Integer id){
        SysAclModule aclModule = aclModuleMapper.selectByPrimaryKey(id);
        if(aclModule == null){
            return null;
        }
        return aclModule.getLevel();
    }

    //将参数类中的数据设置到实体类中
    private SysAclModule setAclModule(AclModuleParam param){
        SysAclModule sysAclModule = SysAclModule.builder()
                .id(param.getId())
                .name(param.getName())
                .parentId(param.getParentId())
                .remark(param.getRemark())
                .seq(param.getSeq())
                .status(param.getStatus())
                .level(LevelUtil.calculateLevel(getLevel(param.getParentId()), param.getParentId()))
                .operator(RequestHolder.getCurrentUser().getUsername())
                .operatorTime(new Date())
                .operatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()))
                .build();
        return sysAclModule;
    }


}

package com.mmall.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mmall.beans.PageQuery;
import com.mmall.beans.PageResult;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysAclMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysAcl;
import com.mmall.param.AclParam;
import com.mmall.util.BeanValidator;
import com.mmall.util.IpUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author: zhangyu
 * @Description:
 * @Date: in 2019/9/3 15:20
 */
@Service
public class SysAclService {

    @Resource
    private SysAclMapper aclMapper;
    @Resource
    private SysLogService logService;

    /**
     * 新增权限
     */
    public void save(AclParam param) {
        BeanValidator.check(param);
        checkAclByParam(param);
        SysAcl acl = setAcl(param);

        aclMapper.insertSelective(acl);
        logService.saveAclLog(null, acl);
    }

    /**
     * 修改权限点
     */
    public void update(AclParam param) {
        BeanValidator.check(param);
        checkAclByParam(param);
        SysAcl before = aclMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before, "待更新的权限点不存在");
        SysAcl after = setAcl(param);

        aclMapper.updateByPrimaryKeySelective(after);
        logService.saveAclLog(before, after);
    }

    //展示权限点的分页列表
    public PageResult<SysAcl> getPageByAclModuleId(int aclModuleId, PageQuery page) {
        BeanValidator.check(page);
        int count = aclMapper.countByAclModuleId(aclModuleId);
        if(count > 0){
            List<SysAcl> aclList = aclMapper.getPageByAclModuleId(aclModuleId, page);
            PageResult<SysAcl> result = PageResult.<SysAcl>builder()
                    .total(count)
                    .data(aclList)
                    .build();
            return result;
        }
        return PageResult.<SysAcl>builder().build();
    }

    //校验同一权限模块下是否存在相同名称的权限点
    private boolean checkExist(Integer aclModuleId, Integer id, String name){
        return aclMapper.countByAclModuleIdAndName(aclModuleId, id, name) > 0;
    }

    //生成权限编码
    private String genderateCode(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date())+ "_" + (int)Math.random() * 100;
    }

    //校验权限点
    private void checkAclByParam(AclParam param){
        if(checkExist(param.getAclModuleId(), param.getId(), param.getName())){
            throw new ParamException("同一模块下存在相同名称的权限");
        }
    }

    //将参数类中的数据设置到实体类中
    private SysAcl setAcl(AclParam param){
        SysAcl acl = SysAcl.builder()
                .id(param.getId())
                .name(param.getName())
                .code(genderateCode())
                .seq(param.getSeq())
                .status(param.getStatus())
                .type(param.getType())
                .url(param.getUrl())
                .aclModuleId(param.getAclModuleId())
                .remark(param.getRemark())
                .operator(RequestHolder.getCurrentUser().getUsername())
                .operatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()))
                .operatorTime(new Date())
                .build();
        return acl;
    }

}

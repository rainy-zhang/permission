package com.mmall.dao;

import com.mmall.model.SysAclModule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclModuleMapper {
    int deleteByPrimaryKey(@Param("id")Integer id);

    int insert(SysAclModule record);

    int insertSelective(SysAclModule record);

    SysAclModule selectByPrimaryKey(@Param("id")Integer id);

    int updateByPrimaryKeySelective(SysAclModule record);

    int updateByPrimaryKey(SysAclModule record);

    int countByNameAndParent(@Param("parentId")Integer parentId, @Param("name")String name, @Param("id") Integer id);

    List<SysAclModule> getChildAclModuleListByLevel(@Param("level")String beforLevel);

    void batchUpdateLevel(@Param("aclModuleList")List<SysAclModule> aclModuleList);

    List<SysAclModule> getAllAclModule();

    int countByParentId(@Param("id") int id);
}
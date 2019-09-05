package com.mmall.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mmall.dao.SysAclMapper;
import com.mmall.dao.SysAclModuleMapper;
import com.mmall.dao.SysDeptMapper;
import com.mmall.dto.AclDto;
import com.mmall.dto.AclModuleLevelDto;
import com.mmall.dto.DeptLevelDto;
import com.mmall.model.SysAcl;
import com.mmall.model.SysAclModule;
import com.mmall.model.SysDept;
import com.mmall.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: zhangyu
 * @Description: 组装树形结构的service
 * @Date: in 2019/8/25 17:35
 */
@Service
public class SysTreeService {

    @Resource
    private SysDeptMapper deptMapper;
    @Resource
    private SysAclModuleMapper aclModuleMapper;
    @Resource
    private SysCoreService coreService;
    @Resource
    private SysAclMapper aclMapper;

    /**
     * 根据用户Id返回用户的权限列表
     */
    public List<AclModuleLevelDto> userAclTree(Integer userId) {
        List<SysAcl> aclList = coreService.getUserAclList(userId);
        ArrayList<AclDto> aclDtoList = Lists.newArrayList();

        for (SysAcl acl : aclList) {
            AclDto dto = AclDto.adapt(acl);
//            dto.setChecked(true);
//            dto.setHashAcl(true);
            aclDtoList.add(dto);
        }

        return aclListToTree(aclDtoList);
    }

    /**
     * 返回角色树列表
     */
    public List<AclModuleLevelDto> roleTree(Integer roleId){
        //1.获取当前登录的用户的权限列表
        List<SysAcl> userAclList = coreService.getCurrentUserAclList();
        //2.获取角色的权限列表
        List<SysAcl> roleAclList = coreService.getRoleAclList(roleId);
        //3.当前系统所有权限点
        List<AclDto> aclDtoList = Lists.newArrayList();

        //用来存储用户已分配的权限的Id集合
        Set<Integer> userAclIdSet = userAclList.stream().map(acl -> acl.getId()).collect(Collectors.toSet());
        //用来存储角色已分配的权限的Id集合
        Set<Integer> roleAclIdSet = roleAclList.stream().map(acl -> acl.getId()).collect(Collectors.toSet());

        List<SysAcl> allAclList = aclMapper.getAll();

        for (SysAcl acl : allAclList) {
            AclDto dto = AclDto.adapt(acl);
            if(userAclIdSet.contains(dto.getId())){ //如果当前用户的角色可以操作这个权限
                dto.setHashAcl(true);
            }
            if(roleAclIdSet.contains(dto.getId())){ //如果选择的角色id已经分配了这个权限
                dto.setChecked(true);
            }
            aclDtoList.add(dto);
        }

        return aclListToTree(aclDtoList);
    }

    /**
     * 将权限点列表组装成树形结构
     */
    public List<AclModuleLevelDto> aclListToTree(List<AclDto> aclDtoList){
        if(CollectionUtils.isEmpty(aclDtoList)){
            return Lists.newArrayList();
        }
        List<AclModuleLevelDto> aclModuleLevelList = aclModuleTree();

        Multimap<Integer, AclDto> moduleIdAclMap = ArrayListMultimap.create();

        for (AclDto aclDto : aclDtoList) {
            if(aclDto.getStatus() == 1){
                moduleIdAclMap.put(aclDto.getAclModuleId(), aclDto);
            }
        }

        bindAclsWithOrder(aclModuleLevelList, moduleIdAclMap);
        return aclModuleLevelList;
    }

    //将权限点绑定到权限树上面
    public void bindAclsWithOrder(List<AclModuleLevelDto> aclModuleLevelList, Multimap<Integer, AclDto> moduleIdAclMap){
        if(CollectionUtils.isEmpty(aclModuleLevelList)){
            return;
        }
        for (AclModuleLevelDto aclModuleDto : aclModuleLevelList) {
            List<AclDto> aclDtoList = (List<AclDto>) moduleIdAclMap.get(aclModuleDto.getId());
            if(CollectionUtils.isNotEmpty(aclDtoList)){
                Collections.sort(aclDtoList, new Comparator<AclDto>() {
                    @Override
                    public int compare(AclDto o1, AclDto o2) {
                        return o1.getSeq() - o2.getSeq();
                    }
                });

                aclModuleDto.setAclDtoList(aclDtoList);
            }
            bindAclsWithOrder(aclModuleDto.getAclModuleDtoList(), moduleIdAclMap);
        }
    }

    /**
     * 返回树形结构的dept
     */
    public List<DeptLevelDto> deptTree(){
        List<SysDept> deptList = deptMapper.getAllDept();
        List<DeptLevelDto> dtoList = Lists.newArrayList();
        for (SysDept dept : deptList) {
            DeptLevelDto deptDto = DeptLevelDto.adept(dept);
            dtoList.add(deptDto);
        }
        return deptListToTree(dtoList);
    }

    //根据传入的部门列表将dept封装成 Map<level, List<DeptLevelDto>> 的形式
    private List<DeptLevelDto> deptListToTree(List<DeptLevelDto> deptLevelList){
        if(CollectionUtils.isEmpty(deptLevelList)){
            return Lists.newArrayList();
        }

        Multimap<String, DeptLevelDto> deptDtoMap = ArrayListMultimap.create();

        List<DeptLevelDto> rootList = Lists.newArrayList();

        for (DeptLevelDto deptDto : deptLevelList) {
            deptDtoMap.put(deptDto.getLevel(), deptDto);
            if(LevelUtil.ROOT.equals(deptDto.getLevel())){
                rootList.add(deptDto);
            }
        }

        //按照seq进行排序
        Collections.sort(rootList, new Comparator<DeptLevelDto>() {
            /**
             * 当返回值为0:相等,正数:降序,负数:升序
             */
            @Override
            public int compare(DeptLevelDto o1, DeptLevelDto o2) {
                return o1.getSeq() - o2.getSeq();
            }
        });

        transformDeptTree(rootList, LevelUtil.ROOT, deptDtoMap);
        return rootList;
    }

    //使用递归将deptList封装成树形结构
    private void transformDeptTree(List<DeptLevelDto> deptDtoList, String level, Multimap<String, DeptLevelDto> deptDtoMap) {
        for (int i = 0; i < deptDtoList.size(); i++) {
            DeptLevelDto deptDto = deptDtoList.get(i);
            //处理当前层级的数据
            String nextLevel = LevelUtil.calculateLevel(level, deptDto.getId());
            //处理下一层
            List<DeptLevelDto> tempDeptList = (List<DeptLevelDto>) deptDtoMap.get(nextLevel);
            if(CollectionUtils.isNotEmpty(tempDeptList)){
                //排序
                Collections.sort(tempDeptList, new Comparator<DeptLevelDto>() {
                    @Override
                    public int compare(DeptLevelDto o1, DeptLevelDto o2) {
                        return o1.getSeq() - o2.getSeq();
                    }
                });
                //设置下一层部门
                deptDto.setDeptList(tempDeptList);
                //进入到下一层部门
                transformDeptTree(tempDeptList, nextLevel, deptDtoMap);
            }
        }
    }

    /**
     * 返回树形结构的aclModule
     */
    public List<AclModuleLevelDto> aclModuleTree(){
        List<SysAclModule> aclModuleList = aclModuleMapper.getAllAclModule();
        List<AclModuleLevelDto> aclModuleDtoList = Lists.newArrayList();
        for (SysAclModule aclModule : aclModuleList) {
            aclModuleDtoList.add(AclModuleLevelDto.adapt(aclModule));
        }
        return aclModuleListToTree(aclModuleDtoList);
    }

    //根据传入的列表封装成 Map<level, List<>> 的形式
    private List<AclModuleLevelDto> aclModuleListToTree(List<AclModuleLevelDto> dtoList) {
        if(CollectionUtils.isEmpty(dtoList)){
            return Lists.newArrayList();
        }

        Multimap<String, AclModuleLevelDto> dtoMap = ArrayListMultimap.create();
        List<AclModuleLevelDto> rootList = Lists.newArrayList();

        for (AclModuleLevelDto aclModuleDto : dtoList) {
            dtoMap.put(aclModuleDto.getLevel(), aclModuleDto);
            if(LevelUtil.ROOT.equals(aclModuleDto.getLevel())){
                rootList.add(aclModuleDto);
            }
        }

        Collections.sort(rootList, new Comparator<AclModuleLevelDto>() {
            @Override
            public int compare(AclModuleLevelDto o1, AclModuleLevelDto o2) {
                return o1.getSeq() - o2.getSeq();
            }
        });

        transformAclModuleTree(rootList, LevelUtil.ROOT, dtoMap);
        return rootList;
    }

    //通过递归将aclModule封装成树形结构
    private void transformAclModuleTree(List<AclModuleLevelDto> dtoList, String level, Multimap<String, AclModuleLevelDto> dtoMap){
        for (int i = 0; i < dtoList.size(); i++) {
            AclModuleLevelDto dto = dtoList.get(i);
            String nextLevel = LevelUtil.calculateLevel(level, dto.getId());

            List<AclModuleLevelDto> tempList = (List<AclModuleLevelDto>) dtoMap.get(nextLevel);

            if(CollectionUtils.isNotEmpty(tempList)){

                Collections.sort(tempList, new Comparator<AclModuleLevelDto>() {
                    @Override
                    public int compare(AclModuleLevelDto o1, AclModuleLevelDto o2) {
                        return o1.getSeq() - o2.getSeq();
                    }
                });

                dto.setAclModuleDtoList(tempList);
                transformAclModuleTree(tempList, nextLevel, dtoMap);
            }
        }
    }



}

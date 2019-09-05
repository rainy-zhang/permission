package com.mmall.service;

import com.google.common.base.Preconditions;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysDeptMapper;
import com.mmall.dao.SysUserMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysDept;
import com.mmall.param.DeptParam;
import com.mmall.util.BeanValidator;
import com.mmall.util.IpUtil;
import com.mmall.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

/**
 * @Author: zhangyu
 * @Description: 部门Service层
 * @Date: in 2019/8/25 17:08
 */
@Service
public class SysDeptService {

    @Resource
    private SysDeptMapper deptMapper;
    @Resource
    private SysUserMapper userMapper;

    /**
     * 新增部门
     */
    public void save(DeptParam deptParam){
        BeanValidator.check(deptParam);
        if(checkExist(deptParam.getParentId(), deptParam.getName(), deptParam.getId())){
            throw new ParamException("同一层级下存在相同名称的部门!");
        }
        SysDept sysDept = setDept(deptParam);
        deptMapper.insertSelective(sysDept);
    }

    /**
     * 根据Id删除部门
     */
    public void deleteById(int id) {
        SysDept dept = deptMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(dept, "要删除的部门不存在!");
        if(deptMapper.countByParentId(id) > 0){
            throw new ParamException("部门下存在子部门,无法删除");
        }
        if(userMapper.countByDeptId(id) > 0){
            throw new ParamException("部门下存在用户,无法删除");
        }
        deptMapper.deleteByPrimaryKey(id);
    }

    /**
     * 更新部门
     */
    public void update(DeptParam deptParam){
        BeanValidator.check(deptParam);
        if(checkExist(deptParam.getParentId(), deptParam.getName(), deptParam.getId())){
            throw new ParamException("同一层级下存在相同名称的部门!");
        }
        //获取更新之前的部门信息
        SysDept beforeDept = deptMapper.selectByPrimaryKey(deptParam.getId());
        Preconditions.checkNotNull(beforeDept, "待更新的部门不存在!");
        SysDept afterDept = setDept(deptParam);
        updateWithChild(beforeDept, afterDept);
    }

    //更新部门及其所有子部门
    @Transactional
    protected void updateWithChild(SysDept beforeDept, SysDept afterDept){
        String newLevelPrefix = beforeDept.getLevel();
        String oldLevelPrefix = afterDept.getLevel();
        //判断更新后的部门Level是否改变,如果变了则子部门的level也要改变
        if(!oldLevelPrefix.equals(newLevelPrefix)){
            List<SysDept> deptList = deptMapper.getChildDeptListByLevel(oldLevelPrefix);
            //当集合不为空时表示该部门下有子部门
            if(CollectionUtils.isNotEmpty(deptList)){
                for (SysDept dept : deptList) {
                    String level = dept.getLevel();
                    if(level.indexOf(oldLevelPrefix) == 0){
                        level = newLevelPrefix + oldLevelPrefix.substring(oldLevelPrefix.length());
                        dept.setLevel(level);
                    }
                }
                //批量修改所有的子部门的level
                deptMapper.batchUpdateLevel(deptList);
            }
        }
        deptMapper.updateByPrimaryKey(afterDept);
    }

    //判断同一级部门下是否存在相同名称的部门
    private boolean checkExist(Integer parentID, String deptName, Integer deptID){
        return deptMapper.countByNameAndParent(parentID, deptName, deptID) > 0;
    }

    //根据部门ID获取level
    private String getLevel(Integer deptID){
        SysDept sysDept = deptMapper.selectByPrimaryKey(deptID);
        if(null == sysDept){
            return null;
        }
        return sysDept.getLevel();
    }

    //将参数类中的数据设置到dept中
    private SysDept setDept(DeptParam param){
        SysDept dept = SysDept.builder()
                .id(param.getId())
                .name(param.getName())
                .parentId(param.getParentId())
                .seq(param.getSeq())
                .remark(param.getRemark())
                .level(LevelUtil.calculateLevel(getLevel(param.getParentId()), param.getParentId()))
                .operator(RequestHolder.getCurrentUser().getUsername())
                .operatorTime(new Date())
                .operatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()))
                .build();
        return dept;
    }

}

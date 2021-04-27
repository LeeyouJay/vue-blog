package com.arslinth.dao;

import com.arslinth.entity.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * @author Arslinth
 * @Description TODO
 * @Date 2021/3/7
 */
@Repository
public interface SysRoleDao extends BaseMapper<SysRole> {

}

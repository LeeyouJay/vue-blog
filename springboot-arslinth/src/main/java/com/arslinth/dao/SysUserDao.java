package com.arslinth.dao;

import com.arslinth.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * @author Arslinth
 * @Description 用户数据层
 * @Date 2021/2/22
 */
@Repository
public interface SysUserDao extends BaseMapper<SysUser> {

    @Update("UPDATE sys_user SET state = #{state} WHERE id = #{id}")
    int setState(SysUser user);

    @Update("UPDATE sys_user SET nick_name=#{nickName}, phone=#{phone},email=#{email},avatar=#{avatar},sex=#{sex},state = #{state} WHERE id = #{id}")
    int changeUserInfo(SysUser user);

    @Update("UPDATE sys_user SET ${platform}=#{uuid} WHERE username =#{username}")
    int updateOAuthLogin(@Param("platform") String platform, @Param("uuid")String uuid, @Param("username")String username);

    @Select("SELECT ${platform} FROM `sys_user` WHERE username = #{username}")
    String selectForUUidByName(@Param("platform")String platform, @Param("username")String username);

    @Select("SELECT * FROM `sys_user` WHERE ${platform}=#{uuid}")
    SysUser findByAuthLogin(@Param("platform")String platform,@Param("uuid")String uuid);
}

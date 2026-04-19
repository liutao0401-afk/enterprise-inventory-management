package com.eims.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eims.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserRepository extends BaseMapper<SysUser> {
}

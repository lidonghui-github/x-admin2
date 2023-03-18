package com.lantu.sys.mapper;

import com.lantu.sys.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lidonghui
 * @since 2023-03-16
 */
public interface UserMapper extends BaseMapper<User> {
    public List<String> getRoleNameByUserid(Integer userid);
}

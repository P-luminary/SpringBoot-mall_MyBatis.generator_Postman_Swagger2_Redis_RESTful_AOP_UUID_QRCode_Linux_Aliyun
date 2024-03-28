package com.imooc.mall.model.dao;

import com.imooc.mall.model.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    // 16. 新增功能 去对应的UserMapper.xml进行描述 117行  BaseResultMap就是一开始自动生成的User对象
    // <include refid="Base_Column_List"/> 选取完整的User对象
    User selectByName(String userName);
//23多个参需要@Param写 进入编写代码123行  24回到UserServiceImpl编写完
    User selectLogin(@Param("userName") String userName, @Param("password")String password);


}
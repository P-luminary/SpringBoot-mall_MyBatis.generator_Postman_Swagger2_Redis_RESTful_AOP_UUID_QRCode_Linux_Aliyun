package com.imooc.mall.service;

import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.model.pojo.User;

import java.security.NoSuchAlgorithmException;

//5.这是抽象的接口 还要让它实现 再创建一个impl 实现接口类 UserServiceImpl.java
public interface UserService {
    User getUser();
// 16.写完接口去实现接口UserServiceImpl.java
    void register(String userName, String password) throws ImoocMallException, NoSuchAlgorithmException;

    //23.写方法判断md5与其匹配
    User login(String userName, String password) throws ImoocMallException;

    // 28写updateUserInfo方法 不需要返回任何信息 只需要提醒更行成功即可
    void updateInformation(User user) throws ImoocMallException;

    //  32.拿到用户  role=1是普通用户   role=2是管理员用户
    boolean checkAdminRole(User user);
}

package com.imooc.mall.service.impl;

import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.dao.UserMapper;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.service.UserService;
import com.imooc.mall.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

/**
 * UserService实现类
 */
@Service
//5.重写里面的方法  @Autowired引入一个mapper去查询数据库返回真正的信息 6返回UserController补全return
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public User getUser() {
        //通过主键来查询一个对象
        return userMapper.selectByPrimaryKey(1);
    }

    @Override
    public void register(String userName, String password) throws ImoocMallException, NoSuchAlgorithmException {
//    16.查询用户名是否存在, 不允许重名 用userMapper去查数据
//    但未编写功能 去手动编写UserMapper.java
        User result = userMapper.selectByName(userName);
        if (result != null) {
//    17. 用户已存在 在Service层不能直接return但是controller可以直接返回
            // 创建一个异常类 com/imooc/mall/exception/ImoocMallException.java
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        // 通过检测 允许写入数据库
        User user = new User();
        user.setUsername(userName);
//        user.setPassword(password);
        //21.重写密码 22登录login接口的开发
        user.setPassword(MD5Utils.getMD5Str(password));
        //先判断是不是空 不是空才修改  18实现完以后回到controller层进行调用
        int count = userMapper.insertSelective(user);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.INSERT_FAILED);
        }
    }

    //23.写方法判断md5与其匹配
    @Override
    public User login(String userName, String password) throws ImoocMallException {
        String md5Password = null;
        try {
            md5Password = MD5Utils.getMD5Str(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
//   23写方法判断md5与其匹配 UserMapper中写 login
        //25.匹配 且去接口增加
        User user = userMapper.selectLogin(userName, md5Password);
        if (user == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.WRONG_PASSWORD);
        }//能找到就返回用户 直接从上面写@Override再自动修复就可以自动生成login的接口
        //26.返回UserController写完login接口
        return user;
    }
// 28写updateUserInfo方法 不需要返回任何信息 只需要提醒更行成功即可
    @Override
    public void updateInformation(User user) throws ImoocMallException {
//    更新个性签名
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>1){
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILD);
        }
        //快速使用@Override 快速补全接口代码 29返回UserController补全代码
    }
//  32.拿到用户  role=1是普通用户   role=2是管理员用户
    //33.回到Controller
    @Override
    public boolean checkAdminRole(User user){
        return user.getRole().equals(2);
    }
}

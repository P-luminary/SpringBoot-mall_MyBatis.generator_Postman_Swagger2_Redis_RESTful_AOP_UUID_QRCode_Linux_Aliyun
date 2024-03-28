package com.imooc.mall.controller;

import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;

/**
 * 用户控制器
 */
@Controller
public class UserController {
    //4.返回对象的基本信息 return里面会写和service相关的  5建立Service层
    @Autowired
    UserService userService;

    @GetMapping("/test")
    @ResponseBody //返回Json格式内容
    public User personalPage() {
//  6.补全return 7告诉mapper在哪里怎么去找 去application.properties编写 mybatis.mapper-locations:......
//  去主类里编写@MapperScan(basePackages = "com.imooc.mall.model.dao") 以防找不到mapper 8.去配置端口8083
//   9.加入log4j2.xml  增加aop的pom
        return userService.getUser();
    }

    /**
     * 注册接口
     * @param userName
     * @param password
     * @return
     * @throws ImoocMallException
     * @throws NoSuchAlgorithmException
     */
    // 12.创造一个统一返回对象 com/imooc/mall/common/ApiRestResponse.java
    // 15.参数加在请求中所以加上@RequestParam 编写校验  16去UserService增加register接口
    @PostMapping("/register")
    @ResponseBody
    public ApiRestResponse register(@RequestParam("userName") String userName, @RequestParam("password") String password) throws ImoocMallException, NoSuchAlgorithmException {
//    字符串为空 || 符合参数
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD_NAME);
        }
//   防止用户密码长度设置简单 密码长度不能少于8
        if (password.length() < 8) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        //18.搞全操作  19进行统一处理异常[对前端安全考虑] GlobalExceptionHandler.java
        userService.register(userName, password);
        return ApiRestResponse.success();
    }

    /**
     * 登录接口
     * @param userName
     * @param password
     * @param session
     * @return
     * @throws ImoocMallException
     * @throws NoSuchAlgorithmException
     */
    //22登录login接口的开发 23返回UserServiceImpl写login
    @PostMapping("/login")
    @ResponseBody
    public ApiRestResponse login(@RequestParam("userName") String userName, @RequestParam("password") String password, HttpSession session) throws ImoocMallException, NoSuchAlgorithmException {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD_NAME);
        }//26.编写完毕login接口  27更新个性签名接口
        User user = userService.login(userName, password);
        //保存用户信息时，不保存密码
        user.setPassword(null);
        //把对象放入session中 KEY
        session.setAttribute(Constant.IMOOC_MALL_USER, user);
        return ApiRestResponse.success(user);
    }

    /**
     * 更新个性签名
     * @param session
     * @param signature
     * @return
     * @throws ImoocMallException
     */
    //  27.个性签名接口 28编写UserServiceImpl的updateUserInfo方法
    @PostMapping("/user/update")
    @ResponseBody
    public ApiRestResponse updateUserInfo(HttpSession session, @RequestParam String signature) throws ImoocMallException {
        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        if (currentUser == null) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
        }
        User user = new User();
        user.setId(currentUser.getId());
        user.setPersonalizedSignature(signature);
        //29.搞全代码
        userService.updateInformation(user);
        return ApiRestResponse.success();
    }

    /**
     * 登出且清除session
     * @param session
     * @return
     */
    //30.退出登录接口
    @PostMapping("/user/logout")
    @ResponseBody
    public ApiRestResponse logout(HttpSession session) {
        session.removeAttribute(Constant.IMOOC_MALL_USER);
        return ApiRestResponse.success();
    }

    /**
     * 登录接口
     * @param userName
     * @param password
     * @param session
     * @return
     * @throws ImoocMallException
     * @throws NoSuchAlgorithmException
     */
    //  31.管理员登录接口 思路可以借鉴
    @PostMapping("/adminLogin")
    @ResponseBody
    public ApiRestResponse adminLogin(@RequestParam("userName") String userName, @RequestParam("password") String password, HttpSession session) throws ImoocMallException, NoSuchAlgorithmException {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD_NAME);
        }//31.拿到用户名和密码的时候 对其进行校验是否为管理员
        //数据库中 role=1是普通用户   role=2是管理员用户
        // 32去serviceImpl写一个方法
        User user = userService.login(userName, password);
        //33.判断是否为管理员
//        userService.checkAdminRole(user).if
        if (userService.checkAdminRole(user)) {
            //是管理员
            //保存用户信息时，不保存密码
            user.setPassword(null);
            //把对象放入session中 KEY
            session.setAttribute(Constant.IMOOC_MALL_USER, user);
            return ApiRestResponse.success(user);
        }else {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN);
        }
    }
//  34.创建分类接口 CategoryController
}

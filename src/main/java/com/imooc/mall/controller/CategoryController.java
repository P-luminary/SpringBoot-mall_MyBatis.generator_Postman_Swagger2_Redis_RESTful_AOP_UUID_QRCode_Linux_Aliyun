package com.imooc.mall.controller;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.pojo.Category;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.model.request.AddCategoryReq;
import com.imooc.mall.model.request.UpdateCategoryReq;
import com.imooc.mall.model.vo.CategoryVO;
import com.imooc.mall.service.CategoryService;
import com.imooc.mall.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

//  34.创建分类接口 CategoryController
@Controller
public class CategoryController {
    @Autowired
    UserService userService;
    @Autowired
    CategoryService categoryService;

    /**
     * 后台添加目录
     *
     * @param session
     * @param addCategoryReq
     * @return
     */
    @ApiOperation("后台添加目录")
    @PostMapping("admin/category/add")
    @ResponseBody
    public ApiRestResponse addCategory(HttpSession session, @Valid @RequestBody AddCategoryReq addCategoryReq) {
        //登录且必须为管理员才可以 参数中需要添加很多元素 可以使用封装来搞
        //com/imooc/mall/model/model/AddCategoryReq.java
//        if (addCategoryReq.getName() == null || addCategoryReq.getType() == null || addCategoryReq.getParentId() == null || addCategoryReq.getOrderNum() == null) {
//            return ApiRestResponse.error(ImoocMallExceptionEnum.PARA_NOT_NULL);
//        }
        //对身份进行校验 用session获取当前的用户
        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        if (currentUser == null) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
        }//校验管理员 userService写过直接引用过来
        boolean adminRole = userService.checkAdminRole(currentUser);
        if (adminRole) { //35.创建一个CategoryService 分类目录Service
            //37.补全代码 是管理员  在上面添加@RequestBody 去postman Body->(raw/JSON)里测试接口
            categoryService.add(addCategoryReq);
            return ApiRestResponse.success();
        } else {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN);
        }//38.简化参数校验过程 || || || ||  @Valid  @Size(AddCategoryReq) @NotNull
        //39.增加GlobalExceptionHandler.java中的提示代码 不仅仅只提供20000 系统异常 handleMethodArg。。。

    }

    //    45.创建新增接口   46在CategoryServiceImpl.java中新增更新方法
    @ApiOperation("后台更新目录")
    @PostMapping("admin/category/update")
    @ResponseBody
    public ApiRestResponse updateCategory(@Valid @RequestBody UpdateCategoryReq updateCategoryReq, HttpSession session) {
        //对身份进行校验 用session获取当前的用户
        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        if (currentUser == null) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
        }//校验管理员 userService写过直接引用过来
        boolean adminRole = userService.checkAdminRole(currentUser);
        if (adminRole) {
            //补全代码 是管理员  在上面添加@RequestBody 去postman Body->(raw/JSON)里测试接口
            //46.补全接口代码
            Category category = new Category(); //复制过来
            BeanUtils.copyProperties(updateCategoryReq, category);
            categoryService.update(category);
            return ApiRestResponse.success();
        } else {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN);
        }//47.为了统一接口校验管理员身份 NEED_LOGIN NEED_ADMIN
        // 创建一个com/imooc/mall/filter/AdminFilter.java
    }

    @ApiOperation("后台删除目录")
    @PostMapping("admin/category/delete")
    @ResponseBody //没有在接口里做权限校验
    //49.编写delete接口  再去CategoryServiceImpl.java 写delete方法
    public ApiRestResponse deleteCategory(@RequestParam Integer id) {
        categoryService.delete(id);
        return ApiRestResponse.success();
    }

    //50.写后台查询商品分类列表的接口
    @ApiOperation("后台目录列表")
    @GetMapping("admin/category/list")
    @ResponseBody //没有在接口里做权限校验
    public ApiRestResponse listCategoryForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        //51.去CategoryServiceImpl编写该接口的实现类
        PageInfo pageInfo = categoryService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    //54.用户分类列表接口开发 去Impl补写实现类  55.导入Redis的pom
    @ApiOperation("前台目录列表")
    @GetMapping("category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForCustomer(){
        List<CategoryVO> categoryVOS = categoryService.listCategoryForCustomer(0);
        return ApiRestResponse.success(categoryVOS);
    }
}

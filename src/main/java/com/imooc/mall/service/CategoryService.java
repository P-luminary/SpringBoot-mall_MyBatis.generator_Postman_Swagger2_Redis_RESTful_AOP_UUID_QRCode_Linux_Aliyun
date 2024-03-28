package com.imooc.mall.service;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.model.pojo.Category;
import com.imooc.mall.model.request.AddCategoryReq;
import com.imooc.mall.model.vo.CategoryVO;

import java.util.List;

//35.创建一个CategoryService 分类目录Service 36创建一个它的实现类CategoryServiceImpl
public interface CategoryService {
    void add(AddCategoryReq addCategoryReq);

    //47.快速生成
    // 46在CategoryServiceImpl.java中新增更新方法
    void update(Category updateCategory);

    //49.写delete方法     @Override快速更新service
    void delete(Integer id);

    //51.创建vo[转换过后反应给前端的一个类]  pageInfo里面蕴藏着一个List<Category>
    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    //52.用户分类列表接口开发
    List<CategoryVO> listCategoryForCustomer(Integer parentId);


}

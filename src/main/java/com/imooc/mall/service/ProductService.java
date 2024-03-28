package com.imooc.mall.service;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.request.AddProductReq;
import com.imooc.mall.model.request.ProductListReq;

/**
 * 59.商品Service 再创建一个实现类ProductServiceImpl
 */
public interface ProductService {

    void add(AddProductReq addProductReq);

    //67.写updateProduct实现类
    void update(Product updateProduct);

    //68.删除
    void delete(Integer id);

    //69.去ProductAdminController增加batchUpdateSellStaus接口
    PageInfo batchUpdateSellStatus(Integer[] ids, Integer sellStatus);

    //70.后台商品列表接口
    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    //72.商品详情接口开发
    Product detail(Integer id);

    //73.完成实现类
    PageInfo list(ProductListReq productListReq);
}

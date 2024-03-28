package com.imooc.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.dao.ProductMapper;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.query.ProductListQuery;
import com.imooc.mall.model.request.AddProductReq;
import com.imooc.mall.model.request.ProductListReq;
import com.imooc.mall.model.vo.CategoryVO;
import com.imooc.mall.service.CategoryService;
import com.imooc.mall.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 59.商品服务实现类
 */
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductMapper productMapper;//60.ProductMapper SQL通过名字查找product是否存在 151行
    @Autowired
    CategoryService categoryService;

    @Override
    public void add(AddProductReq addProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(addProductReq, product);//赋值
        //61.商品重名情况  返回ProductAdminController补全代码
        Product productOld = productMapper.selectByName(addProductReq.getName());
        if (productOld != null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        int count = productMapper.insertSelective(product);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.CREATE_FAILED);
        }
    }

    //67.写updateProduct实现类 @Override自动导入
    @Override
    public void update(Product updateProduct) {
        Product productOld = productMapper.selectByName(updateProduct.getName());
        //同名且不同id，不能继续修改
        if (productOld != null && productOld.getId().equals(updateProduct.getId())) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        int count = productMapper.updateByPrimaryKeySelective(updateProduct);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILD);
        }
    }

    //68.删除实现类  @Override自动导入
    @Override
    public void delete(Integer id) {
        Product productOld = productMapper.selectByPrimaryKey(id);
        //查不到该记录，无法删除
        if (productOld == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
        int count = productMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
    }
    //69.去ProductAdminController增加batchUpdateSellStatus接口
    @Override
    public PageInfo batchUpdateSellStatus(Integer[] ids, Integer sellStatus){
        productMapper.batchUpdateSellStatus(ids, sellStatus);
        return null;
    }

    //70.后台商品列表接口 71ProductController.java
    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize); //在mapper里写查询sql
        List<Product> products = productMapper.selectListForAdmin();
        PageInfo pageInfo = new PageInfo(products);
        return pageInfo;
    }
    //72.商品详情接口开发 再去ProductController调用
    @Override
    public Product detail(Integer id){
        Product product = productMapper.selectByPrimaryKey(id);
        return product;
    }
    //73.完成实现类
    @Override
    public PageInfo list(ProductListReq productListReq){
        //复杂查询就构建一个querry对象 ProductListQuery.java
        ProductListQuery productListQuery = new ProductListQuery();
        //☆☆搜索处理☆☆  拼接且转换成字符串去数据库查找
        if (!StringUtils.isEmpty(productListReq.getKeyword())){
            String keyword = new StringBuilder().append("%").append(productListReq.getKeyword()).append("%").toString();
            productListQuery.setKeyword(keyword);
        }
        //☆☆目录处理☆☆：如果查某个目录下的商品，不仅是需要查出该目录下的，还要把所有子目录的所有商品都查出来，所以要拿到一个目录id的List
        if (productListReq.getCategoryId() != null){
            //要拿到子目录 引用CategoryService   CategoryVO原本是给前台目录用的 需要重构一下 不是所有目录而是指定目录
            //改动代码 CategoryServiceImpl.java中的listCategoryForCustomer 传入的参数是Integer parentId
            //productListReq获取了所有根节点的list  因为点开List<CategoryVO>里面包括一个  private List<CategoryVO>递归结构
            List<CategoryVO> categoryVOList = categoryService.listCategoryForCustomer(productListReq.getCategoryId());
            ArrayList<Integer> categoryIds = new ArrayList<>(); //拿过来之后存储的list
            categoryIds.add(productListReq.getCategoryId());
            getCategoryIds(categoryVOList,categoryIds);
            productListQuery.setCategoryIds(categoryIds);
        }
        //74.排序能力 错误:前端传什么就 就传到sql中排序  这样是不安全的 要提前处理好
        //☆☆排序处理☆☆：去Constant定义支持的排序模式和手段
        String orderBy = productListReq.getOrderBy();
        if (Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize(), orderBy);
        }else {//前端不一定包含数据的话就不排序了
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize());
        }//75.去写Mapper 最后回到ProductController写调用 76增加购物车模块CartController.java
        List<Product> productList = productMapper.selectList(productListQuery);
        PageInfo pageInfo = new PageInfo(productList);
        return pageInfo;
    }
    //74.写一个方法拿到所有的id 拿到参数之后往哪里存放
    private void getCategoryIds(List<CategoryVO>categoryVOList, ArrayList<Integer> categoryIds){
        for (int i = 0; i < categoryVOList.size(); i++) {
            CategoryVO categoryVO =  categoryVOList.get(i);
            if (categoryVO != null){
                categoryIds.add(categoryVO.getId());
                //递归子节点 子子节点
                getCategoryIds(categoryVO.getChildCategory(), categoryIds); //去上面调用方法 传入对象
            }
            
        }
    }
}

package com.imooc.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.dao.CategoryMapper;
import com.imooc.mall.model.pojo.Category;
import com.imooc.mall.model.request.AddCategoryReq;
import com.imooc.mall.model.vo.CategoryVO;
import com.imooc.mall.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 36.目录分类Service实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
//   去Mapper里增加selectbyName 117行
    CategoryMapper categoryMapper;
    public void add(AddCategoryReq addCategoryReq) {
        Category category = new Category();
//        category.setName(addCategoryReq.getName());
        //字段类型一样 字段名一样的话可以自动拷贝进去
        BeanUtils.copyProperties(addCategoryReq,category);
        Category categoryOld = categoryMapper.selectByName(addCategoryReq.getName());
        if (categoryOld != null){ //重名目录 不允许创建 (优化)将ImoocMallException中的extends换一个
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        int count = categoryMapper.insertSelective(category);
        if (count == 0){
            throw new ImoocMallException(ImoocMallExceptionEnum.CREATE_FAILED);
        }//37.回到CategoryController.java
    }

    @Override //47.快速生成
    // 46在CategoryServiceImpl.java中新增更新方法 46返回CategoryController补全代码
    public void update(Category updateCategory){
        if (updateCategory.getName() != null){
            Category categoryOld = categoryMapper.selectByName(updateCategory.getName());
            if (categoryOld != null && !categoryOld.getId().equals(updateCategory.getId())){ //不能为空且和原来的名字不一样
                throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
            }
        }
        categoryMapper.updateByPrimaryKeySelective(updateCategory); //根据主键更新
// 不和其他名字冲突
    }

    //49.写delete方法     @Override快速更新service
    @Override
    public void delete(Integer id){
        Category categoryOld = categoryMapper.selectByPrimaryKey(id);
        //查不到记录，无法删除，删除失败
        if (categoryOld == null){
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
        int count = categoryMapper.deleteByPrimaryKey(id);
        if (count == 0){
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
    }

    //51.创建vo[转换过后反应给前端的一个类]  pageInfo里面蕴藏着一个List<Category>
    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize){
        //把分页的功能包裹在List后 而不是直接返回
        //52.引入分页查询的pom 续写分页代码
        PageHelper.startPage(pageNum,pageSize,"type,order_num");
        //53.写一个查询的mapper新的sql语句 CategoryMapper.java => List<Category> selectList();
        //54.返回Categorycontroller补全代码
        List<Category> categoryList = categoryMapper.selectList();
        PageInfo pageInfo = new PageInfo(categoryList);
        return pageInfo;

    }

    //54.用户分类列表接口开发
    @Override //56.在下方加想用Redis的注解  57.创建一个对于缓存的配置类com/imooc/mall/config/CachingConfig.java
    @Cacheable(value = "listCategoryForCustomer")
    public List<CategoryVO> listCategoryForCustomer(Integer parentId){
        ArrayList<CategoryVO> categoryVOList = new ArrayList<>();
        recursivelyFindCategories(categoryVOList, parentId);
        //55.去Mapper.java 和 Mapper.xml写方法
        return categoryVOList;
    }

    private void recursivelyFindCategories(List<CategoryVO> categoryVOList, Integer parentId){
        //递归获取所有子类别并组合 合成一个"目录树"
        List<Category> categoryList = categoryMapper.selectCategoriesByParentId(parentId);
        if (!CollectionUtils.isEmpty(categoryList)){
            //空 或 有无元素   itli
            for (int i = 0; i < categoryList.size(); i++) {
                Category category =  categoryList.get(i);
                CategoryVO categoryVO = new CategoryVO();
                //拷贝链接  比原来的多一个childCategory这个字段未被赋值
                BeanUtils.copyProperties(category, categoryVO);
                categoryVOList.add(categoryVO);
                //拿到childCategory字段并赋值 再从上面return categoryVOList;
                recursivelyFindCategories(categoryVO.getChildCategory(), categoryVO.getId());
            }
        }
    }
}

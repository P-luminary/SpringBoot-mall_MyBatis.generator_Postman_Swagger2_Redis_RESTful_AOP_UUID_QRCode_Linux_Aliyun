package com.imooc.mall.service;

import com.imooc.mall.model.vo.CartVO;

import java.util.List;

/**
 * 79.购物车service 增加CartServiceImpl
 */
public interface CartService {

    //87.补全list实现类
    List<CartVO> list(Integer userId);

    List<CartVO> add(Integer userId, Integer productId, Integer count);

    List<CartVO> update(Integer userId, Integer productId, Integer count);

    List<CartVO> delete(Integer userId, Integer productId);

    List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected);

    List<CartVO> selectAllOrNot(Integer userId, Integer selected);
}

package com.imooc.mall.service;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.model.request.CreateOrderReq;
import com.imooc.mall.model.vo.OrderVO;

/**
 * 订单Service
 */
public interface OrderService {

    String create(CreateOrderReq createOrderReq);

    /* 95.继续实现Service实现类 拷贝一个Order成OrderVo 新增两个属性
            private String orderStatusName;
            private List<OrderItemVo> orderItemVoList;
            因为没有<OrderItemVo>要根据需求去创建
            拷贝OrderItem成OrderItemVO 更改属性
            之后回来补写代码
         */
    OrderVO detail(String orderNo);

    //97.前台订单实现类 搞个OrderMapper中的selectForCustomer
    PageInfo listForCustomer(Integer pageNum, Integer pageSize);

    //98.写一个订单取消实现类
    void cancel(String orderNo);

    //99.生成支付二维码 pom.xml生成新的二维码依赖 util新建一个QRCodeGenerator类
    String qrcode(String orderNo);

    //100.实现类
    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    //101.支付
    void pay(String orderNo);

    //102.管理订单实现类开发
    void deliver(String orderNo);

    //103.完结订单
    void finish(String orderNo);
}

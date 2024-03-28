package com.imooc.mall.common;

import com.google.common.collect.Sets;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;


/**
 * 21.SALT常量值 写完以后去UserServiceImpl重写密码代码
 */
@Component //让spring帮注入value
public class Constant {
    public static final String SALT = "aSp[PCx,aw.xq246}";
    public static final String IMOOC_MALL_USER = "imooc_mall_user";
    public static String FILE_UPLOAD_DIR;
    //64.为了解决上传图片系统异常报错 注入失败的原因是上方是static普通变量 set方法把静态变量赋值
    @Value("${file.upload.dir}")
    public void setFileUploadDir(String fileUploadDir){
        FILE_UPLOAD_DIR = fileUploadDir;
    }//65.打开ImoocMallWebMvcConfig 加一个映射规则

    //74.排序处理 去Constant定义支持的排序模式和手段
    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price desc","price asc");
    }
    //83.创建商品上下架的状态 创建CartMapper[Cart selectCartByUserIdAndProductId] 写sql 84再去CartServiceImpl
    public interface SaleStatus{
        int NOT_SALE = 0; //商品下架状态
        int SALE = 1; //商品上架状态
    }
    public interface Cart{
        int UN_CHECKED = 0; //购物车未选中状态
        int CHECKED = 1; //购物车选中状态
    }
    public enum OrderStatusEnum{
        CANCELED(0, "用户已取消"),
        NOT_PAID(10, "未付款"),
        PAID(20, "已付款"),
        DELIVERED(30, "已发货"),
        FINISHED(40,"交易完成")
        ;

        private String value;
        private int code;
        //94.去Constant定义订单状态
        OrderStatusEnum(int code,String value){
            this.value = value;
            this.code = code;
        }
        public static OrderStatusEnum codeOf(int code){
            for (OrderStatusEnum orderStatusEnum : values()){
                if (orderStatusEnum.getCode() == code){
                    return orderStatusEnum;
                }
            }
            throw new ImoocMallException(ImoocMallExceptionEnum.NO_ENUM);
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }
}

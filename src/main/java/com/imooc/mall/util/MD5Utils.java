package com.imooc.mall.util;

import com.imooc.mall.common.Constant;
import org.apache.tomcat.util.codec.binary.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//20.不具备解密的协议 哈希算法  因为有破解MD5的网站所以需要加盐 21com/imooc/mall/common/Constant.java
public class MD5Utils {
    public static String getMD5Str(String strValue) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return Base64.encodeBase64String(md5.digest((strValue+ Constant.SALT).getBytes()));
    }
//  用这个方法测试生成MD5的值
    public static void main(String[] args) throws NoSuchAlgorithmException {
        String md5Str = getMD5Str("12345");
        System.out.println(md5Str);
    }
}

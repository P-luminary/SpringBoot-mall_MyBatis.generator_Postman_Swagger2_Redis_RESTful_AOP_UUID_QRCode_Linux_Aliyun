package com.imooc.mall.controller;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.request.AddProductReq;
import com.imooc.mall.model.request.UpdateProductReq;
import com.imooc.mall.service.ProductService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * 58.后台商品管理Controller  pojo的product复制一份到request变成AddProductReq  59.需要ProductService.java
 */
@RestController
public class ProductAdminController {
    @Autowired
    ProductService productService;
    @PostMapping("admin/product/add")
    public ApiRestResponse addProduct(@Valid @RequestBody AddProductReq addProductReq){
    //61.补全代码  目前图片上传还未开发成功
        productService.add(addProductReq);
        return ApiRestResponse.success();
    }
    //62.图片上传接口
    @PostMapping("admin/upload/file")
    public  ApiRestResponse upload(HttpServletRequest httpServletRequest, @RequestParam("file") MultipartFile file){
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));//用文件后面的名字
        //生成文件名称UUID
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid.toString() + suffixName;
        //创建文件 放在常量类中 Constant.java
        //63.application.properties配置file.upload.dir
//        new File()
        File fileDirectory = new File(Constant.FILE_UPLOAD_DIR);
        File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);
        if (!fileDirectory.exists()){
            if (!fileDirectory.mkdir()){//新建文件夹
                throw new ImoocMallException(ImoocMallExceptionEnum.MKDIR_FAILED);
            }
        }
        try {
            file.transferTo(destFile); //传进来的写到空的方法中去
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            return ApiRestResponse.success(getHost(new URI(httpServletRequest.getRequestURL()+""))+"/images/"+newFileName); //生成的路径IP和端口号
        } catch (URISyntaxException e) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.UPLOAD_FAILED);
        }
    }
    private URI getHost(URI uri){
        URI effectiveURI;
        try {
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(),null,null,null);
        } catch (URISyntaxException e) {
            effectiveURI = null; //如果新建失败 就返回回去
        }
        return effectiveURI;
    }
    //66. 接口 复制一个request中的AddProductReq 之后回来补全代码
    @ApiOperation("后台更新商品")
    @PostMapping("/admin/product/update")
    public ApiRestResponse updateProduct(@Valid @RequestBody UpdateProductReq updateProductReq){
        Product product = new Product();
        BeanUtils.copyProperties(updateProductReq, product);
        //67.进入ProductServiceImpl编写接口实现类
        productService.update(product);
        return ApiRestResponse.success();
    }
    //68. 搞一个删除的接口 同理也在ProductServiceImpl中写实现类 之后回来补全代码
    @ApiOperation("后台删除商品")
    @PostMapping("/admin/product/delete")
    public ApiRestResponse deleteProduct(@RequestParam Integer id){
        productService.delete(id);
        return ApiRestResponse.success();
    }
    //69. 批量上下架接口 同理也在ProductServiceImpl中写实现类 Napper中增加批量上下架的SQL
    // 之后回来补全代码
    @ApiOperation("后台批量上下架接口")
    @PostMapping("/admin/product/batchUpdateSellStatus")
    public ApiRestResponse batchUpdateSellStatus(@RequestParam Integer[] ids, @RequestParam Integer sellStatus){
        productService.batchUpdateSellStatus(ids,sellStatus);
        return ApiRestResponse.success();
    }
    //70.后台商品列表接口 同理也在ProductServiceImpl中写实现类 Napper中增加批量上下架的SQL 补全代码
    //71.开发与前台商品ProductController.java
    @ApiOperation("后台商品列表接口")
    @PostMapping("/admin/product/list")
    public ApiRestResponse list(@RequestParam Integer[] pageNum, @RequestParam Integer pageSize){
        PageInfo pageInfo = productService.batchUpdateSellStatus(pageNum,pageSize);
        return ApiRestResponse.success(pageInfo);
    }
}

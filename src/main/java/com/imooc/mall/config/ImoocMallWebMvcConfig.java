package com.imooc.mall.config;

import com.imooc.mall.common.Constant;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 42.配置地址映射 43去CategoryController.java 加一个 @ApiOperation("后台添加目录")
 * 44.新增一个目录的updateCategory的参数 [UpdateCategoryReq.java  ]
 */
@Configuration  //代表是一个配置
public class ImoocMallWebMvcConfig implements WebMvcConfigurer {
    public void addResourceHandles(ResourceHandlerRegistry registry){
        //104.根据路由配置相关文件 以admin开头的文件会被路由到下面的Locations
        registry.addResourceHandler("/admin/**").addResourceLocations("classpath:/static/admin");
        //65.增加一个registry  66.新增接口继续开发 ProductAdminController
        registry.addResourceHandler("/images/**").addResourceLocations("file:" + Constant.FILE_UPLOAD_DIR);
//     把地址给到对应的目录下
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations(
                "classpath:/META-INF/resources/webjars");
    }
}

// package com.uddco.config;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
// import org.springframework.web.servlet.resource.PathResourceResolver;

// @Configuration
// public class WebMvcConfig implements WebMvcConfigurer {

//     @Value("${file.upload-dir}")
//     private String uploadDir;

//     @Override
//     public void addResourceHandlers(ResourceHandlerRegistry registry) {
//         registry.addResourceHandler("/uploads/**")
//                 .addResourceLocations("file:" + uploadDir + "/")
//                 .setCachePeriod(3600) // 1 hour cache
//                 .resourceChain(true)
//                 .addResolver(new PathResourceResolver());
                
//     }
// }
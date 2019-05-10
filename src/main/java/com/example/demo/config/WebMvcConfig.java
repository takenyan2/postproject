package com.example.demo.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.demo.controller.AuthInterceptor;

/**
 * AuthInterceptorを Spring MVC に登録
 * spring bootの場合は、 @EnableWebMvc をつけたらだめ。
 * Spring Boot MVCの機能を維持し、インターセプタ、フォーマット、ビュー・コントローラなどの追加設定を行う場合は、@EnableWebMvcを使用せず、
 * WebMvcConfigurerAdapterの@Beanクラスを定義しなければいけない。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Bean
    public AuthInterceptor authInterceptor() {
        return new AuthInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor()).addPathPatterns("/posts/**");
    }


}

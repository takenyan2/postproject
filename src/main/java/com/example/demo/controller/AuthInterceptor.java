package com.example.demo.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.example.demo.entiteis.User;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.services.OAuthService;

public class AuthInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    OAuthService oAuthService;

    @Value("${auth.status}")
    private String authorization;

    @Value("${auth.appToken.expiration}")
    private long tokenExpiration;

    @Override
    //　HandlerInterceptorAdapterクラスをOverrideさせるときは、型が決まっていて、boolean型の時は
    // (HttpServletRequest request, HttpServletResponse response, Object handler)を引数に持たせる。
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler){
        //フィルタは、リクエストヘッダーへのアクセスを利用して、リクエストがターゲットリソースに送信される前にユーザーに承認が与えられているかどうかを調べる。
        // request.getHeaderはヘッダを取得する記述。Authorizationは、承認ヘッダーを調べるフィルタの設定方法。
        String appToken = request.getHeader(authorization);
        if (appToken == null){
            throw new UnauthorizedException("認証に失敗しました。トークンが確認できません");
        }

        Optional<User> user = Optional.ofNullable(oAuthService.findUserByAppToken(appToken.replaceFirst("Bearer ","")));
        if (user == null){
            throw new UnauthorizedException("認証に失敗しました。トークンが正しくありません");
        }
        return true;
    }
}

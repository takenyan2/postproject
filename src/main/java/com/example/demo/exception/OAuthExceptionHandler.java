package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.demo.controller.OAuthController;

@ControllerAdvice(assignableTypes = OAuthController.class)
public class OAuthExceptionHandler extends ResponseEntityExceptionHandler {

    //「認証してから出直してこいよ」ってエラー。(401)
    @org.springframework.web.bind.annotation.ExceptionHandler(HttpClientErrorException.class)
    public String httpClientErrorException(HttpClientErrorException e) {
        logger.error(e.getMessage(), e);
        return "error/401";
    }

    //その他エラーをキャッチ(500)
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String allException(Exception e) {
        logger.error(e.getMessage(), e);
        return "error/500";
    }


}

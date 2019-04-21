package com.example.demo.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

public class PostExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    //ResponseEntity<T>の形で書くと、HTTPステータスを指定できる
    public ResponseEntity<Object> handleNotFoundException(NotFoundException e, WebRequest request){
        Map<String, String> errorMessages = new HashMap<>();
        errorMessages.put("messages","指定した投稿は見つかりませんでした。");
        return super.handleExceptionInternal(e,errorMessages,null, HttpStatus.BAD_REQUEST,request);
    }

    //500エラーの例外クラス


}

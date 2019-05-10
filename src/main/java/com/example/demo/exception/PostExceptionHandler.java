package com.example.demo.exception;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.HttpHeadersReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class PostExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    //ResponseEntity<T>の形で書くと、HTTPステータスを指定できる。
    public ResponseEntity<Object> handleNotFoundException(NotFoundException e, WebRequest request) {
        Map<String, String> errorMessages = new HashMap<>();
        errorMessages.put("messages", "指定した投稿は見つかりませんでした。");
        return super.handleExceptionInternal(e, errorMessages, null, HttpStatus.BAD_REQUEST, request);
    }

    //500エラーの例外クラス
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handle500(Exception e, WebRequest request) {
        Map<String, String> error500 = new HashMap<>();
        error500.put("messages", "サーバーエラーです");
        return super.handleExceptionInternal(e, error500, null, HttpStatus.BAD_REQUEST, request);
    }


    /**
     * トークン認証失敗時のハンドリング(401)
     * 不正トークン、トークンと一致するユーザーなし
     *
     * @param e       UnauthorizedExceptionにthrowされたエラー
     * @param request 現在のリクエスト
     * @return エラーレスポンスデータを返却
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException e, WebRequest request) {
        Map<String, String> errorMessages = new HashMap<>();
        errorMessages.put("messages", "トークンが正しくありません 再度ログインしてください");
        return super.handleExceptionInternal(e, errorMessages, null, HttpStatus.UNAUTHORIZED, request);
    }


    //バリデーション発生時のエラークラス
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> validatedErrorMessages = new LinkedHashMap<>();
        validatedErrorMessages.put("message", "バリデーションエラーです");
        //exにバリデーションの例外が代入されている。そこから、getBindingResultで入力チェックを行なって、
        // getFieldErrorsで一旦全てのプロパティからエラーを引っ張る。getDefaultMessageでエラーメッセージを取得し、それをeachで回す。
        ex.getBindingResult().getFieldErrors().forEach(err -> {
            validatedErrorMessages.put(err.getField(), err.getDefaultMessage());
        });
        return super.handleExceptionInternal(ex, validatedErrorMessages, null, HttpStatus.BAD_REQUEST, request);
    }
}

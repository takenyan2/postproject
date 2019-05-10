package com.example.demo.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super("対象の投稿は見つかりませんでした");
    }
}

package com.example.demo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entiteis.Post;
import com.example.demo.services.PostService;


/**
 * JsonやXML等を返すWebAPI用のコントローラ(戻り値がレスポンスのコンテンツになりViewに遷移しない)
 */
@RestController
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService){
        this.postService = postService;
    }

    //投稿一覧表示
    @GetMapping("post")
    @ResponseStatus(HttpStatus.OK)
    public List<Post> getPosts(){
        return postService.findAll();
    }

    //投稿一件参照
    @GetMapping("post/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Post getPost(@PathVariable Long id){
        return postService.findById(id);
    }

    //新規投稿
    @PostMapping("post")
    @ResponseStatus(HttpStatus.CREATED)
    public Post createPost(@RequestBody @Validated Post post){
        return postService.create(post);
    }

    //投稿更新
    @PutMapping("post/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Post updatePost(@PathVariable Long id, @RequestBody @Validated Post post){
        return postService.update(id, post);
    }

    //投稿削除
    @DeleteMapping("post/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable Long id){
        postService.delete(id);
    }

    //画像ファイルアップロード
    @PostMapping("imageUpload/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Post postImage(@PathVariable Long id,@RequestParam("image") MultipartFile multipartFile)throws IOException {
        if(multipartFile.isEmpty()){
            throw new MultipartException("画像が未選択です");
        }
        return postService.uploadImage(id, multipartFile);
    }

    //投稿を検索
    @GetMapping("search")
    @ResponseStatus(HttpStatus.OK)
    public List<Post> searchPost(String keyword){
        return postService.search(keyword);
    }

}

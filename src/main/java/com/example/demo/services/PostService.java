package com.example.demo.services;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.swing.text.html.Option;

import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entiteis.Post;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repositories.PostRepository;

import javafx.geometry.Pos;

@Service
public class PostService {
    private final PostRepository postRepository;

    @Value("{imagesPath}")
    private String imagesPath;
    //@Value("{imagesDirectory}")
    //private String imagesDirectory;


    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    //投稿内容全件取得
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    //投稿データ1件取得
    public Post findById(Long id) {
        return postRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    //新規投稿
    public Post create(Post post) {
        Post createPost = new Post();
        createPost.setContent(post.getContent());
        return postRepository.save(createPost);
    }

    //投稿更新
    public Post update(Long id, Post post) {
        Optional<Post> pastPost = postRepository.findById(id);
        Post updatePost = pastPost.orElseThrow(NotFoundException::new);
        updatePost.setContent(post.getContent());
        return postRepository.save(updatePost);
    }

    //投稿削除
    public void delete(Long id) {
        Optional<Post> pastPost = postRepository.findById(id);
        Post deletePost = pastPost.orElseThrow(NotFoundException::new);

        postRepository.delete(deletePost);
    }

    //画像アップロード
    public Post uploadImage(Long id, MultipartFile multipartFile) throws IOException {
        Optional<Post> originalPost = postRepository.findById(id);
        Post updateImage = originalPost.orElseThrow(NotFoundException::new);
        //画像のファイル名を作成するために、日付を取得。
        LocalDateTime timeNow = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-ss");
        //ファイルの拡張子取得。そもそも拡張子があるかどうか？→あったら拡張子を切り取って取得するという流れ。
        //Objects.requireNonNullメソッドは引数が null なら NullPointerException を投げ、そうでないなら引数をそのまま返す。
        int position = Objects.requireNonNull(multipartFile.getOriginalFilename()).lastIndexOf(".");
        String extension = multipartFile.getOriginalFilename().substring(position);

        //画像保存用のディレクトリとファイルを作成。最後にさっき取得した拡張子を追加することを忘れない。
        String renameFile = id + "-" + timeNow.format(dateTimeFormatter) + extension;
        File uploadFile = new File("src/main/resources/static/images" + "/" + renameFile);
        FileOutputStream fos = new FileOutputStream(uploadFile);

        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fos);

        //新たな画像ファイルをバイナリーデータへ書き込む。
        try (BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(uploadFile))) {
            bo.write(multipartFile.getBytes());
        }
        if (updateImage.getImage() != null) {
            deleteImage(updateImage);
        }

        String imageURL = imagesPath + "/" + renameFile;
        updateImage.setImage(imageURL);
        return postRepository.save(updateImage);
    }

    //画像削除
    public void deleteImage(Post post) {
        File deletePath = new File("src/main/resource/static" + (post.getImage().substring(22)));
        deletePath.delete();
        postRepository.save(post);
    }

    //検索
    public List<Post> search(String keyword) {
        return postRepository.findByContent(keyword);
    }

}
#RESTfulpost_API


###使用した技術要素
***
- JDK1.8.0_202
- Spring Boot_v2.1.3
- MySQL_5.7.25
- gradle_2.1.3
- SwaggerEditorOpenAPI_2.0


###DB設計
***
データベース名：demo


Post

| Field | Type  | Null | kye | Default | Extra |
|:-----------|------------:|:------------:|:------------:|:------------:|:------------:|
| id       |        bigint(20) |     NO     |     PRI     |     Null     |     auto_increment     |
| content     |      varchar(500) |    NO    |          |     Null     |          |
| image       |        varchar(255) |     YES     |          |     Null     |          |


###全体の設計 構成_画面
***

#####ディレクトリ
- main
    - java
        - com/example/restapi
            - config
                - JpaAuditorAwareConfig
                - WebMvcConfig
            - controllers
                - interceptors
                - PostController.java
            - entities
                - Post.java
            - exception
                - NotFoundException.java
            - repositories
                - PostRepository.java
            - services
                - PostService.java
                - RestapiApplication.java
    - resources
        - static/images
            - 1-2019-04-15-16-44-23.jpg
            - 2-2019-04-15-16-49-42.jpg
            - 7-2019-04-16-13-40-10.jpg
        - templates
        - application.properties
        
        
###開発環境のセットアップ手順
***

Java8 インストール

`$brew cask install java8`

MySQL, gradle インストール 

`$brew install mysql@5.7` 

`$brew install gradle`

環境変数設定

`$ vi ~/.bash_profile ~/.bash_profile`に下記を追記

`export JAVA_HOME=`/usr/libexec/java_home -v 1.8` export PATH=$JAVA_HOME/bin:/usr/local/bin:$PATH export PATH="/usr/local/opt/mysql@5.7/bin:$PATH"
$source ~/.bash_profile`


MySQLでdatabase”demo”作成

`$mysql -uroot`

`mysql> create database demo`

アプリケーション起動後の確認

・下記のURLへアクセス

`http://localhost:8080`
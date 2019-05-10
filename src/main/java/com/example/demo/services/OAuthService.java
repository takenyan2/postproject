package com.example.demo.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.GitHubUserProfile;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.social.github.connect.GitHubConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Service;

import com.example.demo.entiteis.User;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.repositories.UserRepository;

@Service
public class OAuthService {

    private final UserRepository userRepository;

    @Value("${github.clientId}")
    private String clientId;

    @Value("${github.clientSecret}")
    private String clientSecret;

    @Value("${github.callbackUrl}")
    private String callbackUrl;

    @Autowired
    public OAuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * GitHubConnectionFactoryでclientId,clientSecretをもとにGitHubへ接続
     * getOAuthOperations()で承認フローを実行
     * buildAuthorizeUrl（GrantType、OAuth2Parameters）を呼び出して、権限承認用URLを作成
     *
     * @return URLにパラメータとしてcode(認証コード)を渡す
     */
    public String getGitHubConnectionUrl() {
        GitHubConnectionFactory connectionFactory = new GitHubConnectionFactory(clientId, clientSecret);
        OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();
        return oauthOperations.buildAuthenticateUrl(GrantType.AUTHORIZATION_CODE, params);
    }

    /*
     * codeをaccessTokenを含んでいるaccessGrantに交換
     *  GitHubConnectionFactoryでclientId,clientSecretをもとにGitHubへ接続
     *  getOAuthOperations()で承認フローを実行
     * @param code 　　認証コード
     * @return accessTokenを含んだaccessGrantを返却
     */
    public AccessGrant getAccessGrant(String code) {
        GitHubConnectionFactory connectionFactory = new GitHubConnectionFactory(clientId, clientSecret);
        OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
        //exchangeForAccess　codeをaccessGrantに変えるためのメソッド。
        //callbackUrl　で認証したらこのURLに帰ってきてねー！って記述
        return oauthOperations.exchangeForAccess(code, callbackUrl, null);
    }

    /**
     * accessTokenを使ってGitHubからユーザー情報を取得する
     *
     * @param accessToken 　アクセストークン
     * @return 認証ユーザーに関する基本的なプロファイル情報
     */
    public GitHubUserProfile getGitHubUserProfile(String accessToken) {
        GitHub github = new GitHubTemplate(accessToken);
        return github.userOperations().getUserProfile();
    }


    /**
     * アプリ内で新しく発行したトークンとユーザー情報を
     * Userエンティティに保存
     *
     * @param accessToken 　アクセストークン
     */
    public void createUser(String accessToken, String appToken) {
        GitHubUserProfile userProfile = getGitHubUserProfile(accessToken);
        User user = new User();
        user.setId(userProfile.getId());
        user.setUserName(userProfile.getUsername());
        user.setAccessToken(accessToken);
        user.setAppToken(appToken);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * 不正トークンかどうかの、認証の際に使用
     *
     * @param accessToken 　アプリ内で新しく発行したトークン
     * @return 該当するuserを返す
     */

    //ユーザーのaccesTokenを見つけてくる記述。なかったら認証失敗のエラーを投げる。
    public User findUserByAccessToken(String accessToken) {
        return userRepository.findByAccessToken(accessToken).orElseThrow(() -> new UnauthorizedException("認証に失敗しました"));
    }

    //accessTokenがあるか調べて無ければ削除。
    public void deleteUser(String accessToken) {
        userRepository.findByAccessToken(accessToken).ifPresent(userRepository::delete);
    }


    /**
     * 不正トークンかどうかの、認証の際に使用
     *
     * @param appToken 　アプリ内で新しく発行したトークン
     * @return 該当するuserを返す
     */
    public Optional<User> findUserByAppToken(String appToken) {
        return userRepository.findByAppToken(appToken);
    }

    //appTokenがあるか調べて無ければ削除。
    public void deleteUserByAppToken(String appToken) {
        userRepository.findByAppToken(appToken).ifPresent(userRepository::delete);
    }


}

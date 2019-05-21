package com.example.demo.controller;

import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.github.api.GitHubUserProfile;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entiteis.User;
import com.example.demo.services.AuthService;

@Controller
public class OAuthController {
    private final AuthService authService;

    @Autowired
    public OAuthController(AuthService authService) {
        this.authService = authService;
    }

    @Value("${login.status}")
    private String login;

    @GetMapping("/")
    public String top() {
        return "top";
    }

    /**
     * 権限承認後アプリ側で認可コードを受け取る
     * ユーザーがgithubにログインしたら、リソースサーバーから認可コードを受け取る。
     * つまり、ログイン済みのユーザーは認可コードを持っているはず。
     * セッション開始するからこのアプリに認可コードちょうだいー！って記述。
     *
     * @param session HttpSession
     * @return code(認可コード)を含んだurl
     */

    @GetMapping("github/getcode")
    public String getCode(HttpSession session) {
        if (session.getAttribute(login) != null) {
            return "redirect:/home";
        }
        String url = authService.getGitHubConnectionUrl();
        return "redirect:" + url;
    }

    /**
     * accessGrantからaccessToken取得
     * Userにユーザー情報を保存
     *
     * @param session HttpSession
     * @param code    認証コード
     * @return home画面へ
     */

    @GetMapping("github/gettoken")
    public String getToken(HttpSession session, @RequestParam String code) {
        if (code == null) {
            return "error/401";
        }
        AccessGrant accessGrant = authService.getAccessGrant(code);
        String accessToken = accessGrant.getAccessToken();
        if (accessToken == null) {
            return "error/401";
        }
        String appToken = UUID.randomUUID().toString();//文字列をランダムにappTokenに格納
        authService.createUser(accessToken, appToken);//accessTokenとappTokenを使ってuserのcreateを行う。
        session.setAttribute(login, accessToken);//session.setAttributeでsessionに、loginしているユーザーの情報を保存。
        return "redirect:/home";//homeにリダイレクト↓
    }


    //accessTokenを利用してgitのユーザープロフィールから情報を引っ張る。
    //その情報をビューに表示させるためにmodelに格納する。
    @GetMapping("home")
    public String showHome(HttpSession session, Model model) {
        if (session.getAttribute(login) == null) {
            return "error/401";
        }
        String accessToken = session.getAttribute(login).toString();
        User user = authService.findUserByAccessToken(accessToken);
        GitHubUserProfile userProfile = authService.getGitHubUserProfile(accessToken);
        model.addAttribute("user", user);
        model.addAttribute("userProfile", userProfile);
        return "home";
    }

    /**
     * sessionを破棄してlogOut扱いにする
     * session.invalidate で、セッションを無効にする。
     *
     * @param session 　HttpSession
     * @return ログイン(Top)画面
     */
    @GetMapping("github/logout")
    public String logout(HttpSession session) {
        if (session.getAttribute(login) == null) {
            return "error/401";
        }
        String accessToken = session.getAttribute(login).toString();
        authService.deleteUser(accessToken);
        session.invalidate();
        return "redirect:/";
    }

}

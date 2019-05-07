package com.example.demo.controller;

import java.util.UUID;

import javax.jws.soap.SOAPBinding;
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
import com.example.demo.services.OAuthService;

@Controller
public class OAuthController {
    private final OAuthService oAuthService;

    @Autowired
    public OAuthController(OAuthService oAuthService){
        this.oAuthService = oAuthService;
    }

    @Value("${login.status}")
    private String login;

    @GetMapping("/")
    public String top(){
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
    public String getCode(HttpSession session){
        if(session.getAttribute(login) == null){
            return "redirect:/home";
        }
        String url = oAuthService.getGitHubConnectionUrl();
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
    public String getToken(HttpSession session,@RequestParam String code){
        if(code == null){
            return "error/401";
        }
        AccessGrant accessGrant = oAuthService.getAccessGrant(code);
        String accessToken = accessGrant.getAccessToken();
        if (accessToken == null){
            return "error/401";
        }
        String appToken = UUID.randomUUID().toString();
        oAuthService.createUser(accessToken,appToken);
        session.setAttribute(login, accessToken);
        return "redirect:home";
    }


    //accessTokenを利用してgitのユーザープロフィールから情報を引っ張る。
    //その情報をビューに表示させるためにmodelに格納する。
    @GetMapping("home")
    public String showHome(HttpSession session, Model model){
        if(session.getAttribute(login) == null){
            return "error/401";
        }
        String accessToken = session.getAttribute(login).toString();
        User user = oAuthService.findUserByAccessToken(accessToken);
        GitHubUserProfile userProfile = oAuthService.getGitHubUserProfile(accessToken);
        model.addAttribute("user",user);
        model.addAttribute("userProfile", userProfile);
        return "home";
    }

    /**
     * sessionを破棄してlogOut扱いにする
     * session.invalidate で、セッションを無効にする。
     * @param session 　HttpSession
     * @return ログイン(Top)画面
     */
    @GetMapping("logout")
    public String logout(HttpSession session){
        if (session.getAttribute(login) == null){
            return "error/401";
        }
        String acccessToken = session.getAttribute(login).toString();
        oAuthService.deleteUser(acccessToken);
        session.invalidate();
        return "redirect:/";
    }
}

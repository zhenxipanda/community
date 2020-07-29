package life.zhaohuan.community.community.controller;

import life.zhaohuan.community.community.dto.AccessTokenDTO;
import life.zhaohuan.community.community.dto.GithubUser;
import life.zhaohuan.community.community.model.User;
import life.zhaohuan.community.community.provider.GithubProvider;
import life.zhaohuan.community.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
@Slf4j
public class AuthorizedController {
    @Autowired
    public GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserService userService;

    @GetMapping("/callback")
    public String callback(@RequestParam(name="code") String code ,
                           @RequestParam(name="state") String state,
                           HttpServletResponse response){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);
        if(githubUser != null && githubUser.getId() != null){
            // 登录成功，显示cookie，session response中拿到session
            // response 设置 cookie ，request请求cookie
            User user = new User();
            // 自己写 token 代替 session , 每次退出登录后
            // 再点击登录 都会生成不同的 token
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setAvatarUrl(githubUser.getAvatarUrl());
            userService.createOrUpdate(user);
            // 把 token 写入 cookie
            response.addCookie(new Cookie("token" , token));
            // 把网址上的地址去掉，返回首页
            return "redirect:/";

        }else{
            log.error("callback get github error,{}", githubUser);
            return "redirect:/";
            // 登录失败，重新登录
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response){
        request.getSession().removeAttribute("user");  // not "name"
        Cookie cookie = new Cookie("token" , null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }
}

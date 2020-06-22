package life.zhaohuan.community.community.controller;

import life.zhaohuan.community.community.mapper.UserMapper;
import life.zhaohuan.community.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    private UserMapper userMapper;
    @GetMapping("/profile/{action}")  // 因为是get方法，另外希望他访问profile的时候，调用这个地址
    public String profile(HttpServletRequest request,
                          @PathVariable(name = "action") String action,
                          Model model){
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length != 0){
            for (Cookie cookie : cookies) {
                if(cookie != null){
                    if(cookie.getName().equals("token")){
                        String token = cookie.getValue();
                        User user = userMapper.findByToken(token);
                        if(user != null){
                            request.getSession().setAttribute("user" , user);
                        }
                        break;
                    }
                }

            }
        }
        if("questions".equals(action)){
            model.addAttribute("section","questions");
            model.addAttribute("sectionName","我的提问");
        }
        else if("replies".equals(action)){
            model.addAttribute("section","replies");
            model.addAttribute("sectionName","最新回复");
        }
        return "profile";
    }
}

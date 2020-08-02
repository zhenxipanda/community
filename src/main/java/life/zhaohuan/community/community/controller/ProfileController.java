package life.zhaohuan.community.community.controller;

import life.zhaohuan.community.community.dto.PaginationDTO;
import life.zhaohuan.community.community.model.User;
import life.zhaohuan.community.community.service.NotificationService;
import life.zhaohuan.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {


    @Autowired
    private QuestionService questionService;

    @Autowired
    private NotificationService notificationService;


    @GetMapping("/profile/{action}")  // 因为是get方法，另外希望他访问profile的时候，调用这个地址
    // 加上action是为了动态切换路径
    public String profile(HttpServletRequest request,
                          @PathVariable(name = "action") String action,
                          Model model,
                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "5") Integer size) {
        User user = (User) request.getSession().getAttribute("user");
        // 如果用户为空，就跳到登录页面
        if (user == null) {
            return "redirect:/";
        }
        if ("questions".equals(action)) {
            // 显示我的问题页面
            model.addAttribute("section", "questions");
            model.addAttribute("sectionName", "我的提问");
//            通过user.id，得到此用户所提的问题
            PaginationDTO paginationDTO = questionService.list(user.getId(), page, size);
            model.addAttribute("pagination", paginationDTO);
        } else if ("replies".equals(action)) {
            // 显示最新回复页面
            PaginationDTO paginationDTO = notificationService.list(user.getId(), page, size);
            model.addAttribute("section", "replies");
            model.addAttribute("pagination", paginationDTO);
            model.addAttribute("sectionName", "最新回复");
        }
        return "profile";
    }
}

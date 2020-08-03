package life.zhaohuan.community.community.controller;

import life.zhaohuan.community.community.cache.TagCache;
import life.zhaohuan.community.community.dto.QuestionDTO;
import life.zhaohuan.community.community.model.Question;
import life.zhaohuan.community.community.model.User;
import life.zhaohuan.community.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {

    @Autowired
    private QuestionService questionService;

//    一点 编辑， 就会路由到这里
    @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name = "id") Long id,
                       Model model){
//        获取这条question，显示到页面
        QuestionDTO question = questionService.getById(id);
        model.addAttribute("title" , question.getTitle());
        model.addAttribute("description" , question.getDescription());
        model.addAttribute("tag" , question.getTag());
        model.addAttribute("id" , question.getId());
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    // 没有修改的时候，进入这个
    @GetMapping("/publish")
    public String publish(Model model){
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    // post 方法，执行请求
    // 接收参数，title,description,tag,id都是publish.html页面有的
    // 这些参数都是前端页面传过来的
//    一点击 发布，就会路由到这个方法
    @PostMapping("/publish")
    public String doPublish(
            @RequestParam(value = "title" , required = false) String title,
            @RequestParam(value = "description" , required = false) String description,
            @RequestParam(value = "tag" , required = false) String tag,
            @RequestParam(value = "id" , required = false) Long id,
            HttpServletRequest request,
            Model model){

        // 接收他们，为了回显到页面上去，保证即便输入内容为空，再次刷新后，内容可不好消失
        model.addAttribute("title" , title);
        model.addAttribute("description" , description);
        model.addAttribute("tag" , tag);
        model.addAttribute("tags", TagCache.get());

        // 若title 为空，就会显示到页面上
        if(title == null || title.equals("")){
            model.addAttribute("error" , "标题不能为空");
            return "publish";
        }
        // 验证是否输入为空
        if(description == null || description.equals("")){
            model.addAttribute("error" , "问题补充不能为空");
            return "publish";
        }
        if(tag == null || tag.equals("")){
            model.addAttribute("error" , "标签不能为空");
            return "publish";
        }

        String invalid = TagCache.filterInvalid(tag);
        if (StringUtils.isNotBlank(invalid)) {
            model.addAttribute("error", "输入非法标签:" + invalid);
            return "publish";
        }

        // 通过session获得用户
        User user = (User) request.getSession().getAttribute("user");
        // 如果输入全部为空，验证用户是否未登录
        if(user == null){
            model.addAttribute("error","用户未登录");
            return "publish";
        }
        // 创建一个新的question对象 并新建 或 更新到数据库中
        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setId(id);
        questionService.createOrUpdate(question);
        // 没有问题就返回首页
        return "redirect:/";
    }
}

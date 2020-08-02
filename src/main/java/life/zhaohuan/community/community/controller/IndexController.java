package life.zhaohuan.community.community.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import life.zhaohuan.community.community.cache.HotTagCache;
import life.zhaohuan.community.community.dto.PaginationDTO;
import life.zhaohuan.community.community.dto.QuestionDTO;
import life.zhaohuan.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private HotTagCache hotTagCache;

    @GetMapping("/")
    // 增加搜索功能，所以再加个参数，search
    public String index(Model model,
                        @RequestParam(name = "page",defaultValue = "1") Integer page,
                        @RequestParam(name = "size",defaultValue = "6") Integer size,
                        @RequestParam(name = "search",required = false) String search,
                        @RequestParam(name = "tag",required = false) String tag){
        // 原始做法
        PaginationDTO pagination = questionService.list(search ,tag, page,size);
        model.addAttribute("pagination",pagination);
        List<String> tags = hotTagCache.getHots();
        // 使用PageHelper 分页
     //   PageInfo<QuestionDTO> p= questionService.getList(search , page,size);
     //   model.addAttribute("pageInfo",p);
        model.addAttribute("search",search);
//        tags 是右侧的热门标签
        model.addAttribute("tags",tags);
//        tag 是问题所属标签
        model.addAttribute("tag",tag);
        return "index";
    }
}

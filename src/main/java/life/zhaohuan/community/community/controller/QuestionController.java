package life.zhaohuan.community.community.controller;

import life.zhaohuan.community.community.dto.CommentDTO;
import life.zhaohuan.community.community.dto.QuestionDTO;
import life.zhaohuan.community.community.enums.CommentTypeEnum;
import life.zhaohuan.community.community.service.CommentService;
import life.zhaohuan.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}") 
//    跳转到Id 为多少的问题
    public String question(@PathVariable(name = "id") Long id,
                           Model model){
        // 去question表中，查询question_id 为 id 的问题
        QuestionDTO questionDTO = questionService.getById(id);
//        为了实现右侧的相关问题
        List<QuestionDTO> relatedQuestions = questionService.selectRelated(questionDTO);
//        获取此问题的评论,QUESTION:1
        List<CommentDTO> comments = commentService.listByTargetId(id , CommentTypeEnum.QUESTION);
        //累加阅读数
        questionService.incView(id);

        // 这样就保证前端页面可以用 question等 ，页面就可以展示question的详细内容了
        model.addAttribute("question" , questionDTO);
        model.addAttribute("comments" , comments);
        model.addAttribute("relatedQuestions" , relatedQuestions);

        return "question";
    }
}

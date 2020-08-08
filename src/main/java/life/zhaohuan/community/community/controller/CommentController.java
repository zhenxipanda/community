package life.zhaohuan.community.community.controller;

import life.zhaohuan.community.community.dto.CommentCreateDTO;
import life.zhaohuan.community.community.dto.CommentDTO;
import life.zhaohuan.community.community.dto.ResultDTO;
import life.zhaohuan.community.community.enums.CommentTypeEnum;
import life.zhaohuan.community.community.exception.CustomizedErrorCode;
import life.zhaohuan.community.community.model.Comment;
import life.zhaohuan.community.community.model.User;
import life.zhaohuan.community.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @ResponseBody //将对象自动序列化成json
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
//    post()的参数就是前段传入来的对象，把传入的json每个key,value赋值到DTO对象上
//    @RequestBody 自动地反序列化成Java对象
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request){
//        先验证用户信息，是否登录
//        通过request拿到session,拿到用户信息
        User user = (User)request.getSession().getAttribute("user");
        if(user == null){
//            传进去错误码
            return ResultDTO.errorOf(CustomizedErrorCode.NO_LOGIN);
        }

        // 如果评论内容为空，提示用户
        if(commentCreateDTO == null || StringUtils.isBlank(commentCreateDTO.getContent())){
            return ResultDTO.errorOf(CustomizedErrorCode.CONTENT_IS_EMPTY);
        }
//        创建一个评论，父id，内容，类型（评论 or 回复），创建时间和修改时间，创建者，点赞数
        Comment comment = new Comment();
//        DTO只有3个属性，父问题的Id,评论的内容，评论的类型，是评论问题，还是评论回复
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setCommentator(user.getId());
        comment.setLikeCount(0L);
        comment.setCommentCount(0);   // 解决评论数为null的问题
//        插入到数据库中
        commentService.insert(comment , user);
//        告诉前端一些信息
        return ResultDTO.okOf();
    }

    @ResponseBody
    @RequestMapping(value = "/comment/{id}", method = RequestMethod.GET)
    public ResultDTO<List> comments(@PathVariable(name = "id") Long id) {
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.okOf(commentDTOS);
    }
}

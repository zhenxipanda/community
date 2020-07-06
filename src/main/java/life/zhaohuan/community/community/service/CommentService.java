package life.zhaohuan.community.community.service;

import life.zhaohuan.community.community.enums.CommentTypeEnum;
import life.zhaohuan.community.community.exception.CustomizedErrorCode;
import life.zhaohuan.community.community.exception.CustomizedException;
import life.zhaohuan.community.community.mapper.CommentMapper;
import life.zhaohuan.community.community.mapper.QuestionExtMapper;
import life.zhaohuan.community.community.mapper.QuestionMapper;
import life.zhaohuan.community.community.model.Comment;
import life.zhaohuan.community.community.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    public void insert(Comment comment) {
       if(comment.getParentId() == null || comment.getParentId() == 0){
           throw new CustomizedException(CustomizedErrorCode.TARGET_PARAM_NOT_FOUND);
       }
       if(comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())){
           throw new CustomizedException(CustomizedErrorCode.TYPE_PARAM_WRONG);
       }
       if(comment.getType() == CommentTypeEnum.COMMENT.getType()){
           // 回复评论
           Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
           if(dbComment == null){
               throw new CustomizedException(CustomizedErrorCode.COMMENT_NOT_FOUND);
           }
            commentMapper.insert(comment);
       }else{
           // 回复问题
           Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
           if(question == null){
               throw new CustomizedException(CustomizedErrorCode.QUESTION_NOT_FOUND);
           }
           commentMapper.insert(comment);
           question.setCommentCount(1);
           questionExtMapper.incCommentCount(question);
       }
    }
}

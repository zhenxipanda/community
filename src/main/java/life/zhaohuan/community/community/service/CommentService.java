package life.zhaohuan.community.community.service;

import life.zhaohuan.community.community.dto.CommentDTO;
import life.zhaohuan.community.community.enums.CommentTypeEnum;
import life.zhaohuan.community.community.enums.NotificationStatusEnum;
import life.zhaohuan.community.community.enums.NotificationTypeEnum;
import life.zhaohuan.community.community.exception.CustomizedErrorCode;
import life.zhaohuan.community.community.exception.CustomizedException;
import life.zhaohuan.community.community.mapper.*;
import life.zhaohuan.community.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentExtMapper commentExtMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Transactional   //事务注解
    public void insert(Comment comment , User commentator) {
//        父 id 为空，提示
       if(comment.getParentId() == null || comment.getParentId() == 0){
           throw new CustomizedException(CustomizedErrorCode.TARGET_PARAM_NOT_FOUND);
       }
//       评论类型为空，提示  评论类型不对，提示
       if(comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())){
           throw new CustomizedException(CustomizedErrorCode.TYPE_PARAM_WRONG);
       }
//       COMMENT : 1
       if(comment.getType() == CommentTypeEnum.COMMENT.getType()){
           // 回复评论
//           因为是回复评论，所以需要去comment表中查找
           Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
           if(dbComment == null){
//               评论不存在
               throw new CustomizedException(CustomizedErrorCode.COMMENT_NOT_FOUND);
           }
           // 回复问题
           Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
           if(question == null){
               throw new CustomizedException(CustomizedErrorCode.QUESTION_NOT_FOUND);
           }
            commentMapper.insert(comment);

           // 增加评论数
           Comment parentComment = new Comment();
           parentComment.setId(comment.getParentId());
           parentComment.setCommentCount(1);
           commentExtMapper.incCommentCount(parentComment);
           // 创建通知
           createNotify(comment, dbComment.getCommentator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_COMMENT, question.getId());

       }else{
           // 回复问题，去question表中查找
           Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
           if(question == null){
               throw new CustomizedException(CustomizedErrorCode.QUESTION_NOT_FOUND);
           }
           // 解决回复评论数 为 null 的问题，一直不显示，因为 null + 1 ,不成功
           // 创建 comment 的时候，初始化为0.因为数据库设置了默认是0，并且 0+ 1也没问题，所以修改这里
        //   comment.setCommentCount(0);
           commentMapper.insert(comment);
//           设置步长，每次自增1
           question.setCommentCount(1);
           questionExtMapper.incCommentCount(question);
           // 创建通知
           createNotify(comment, question.getCreator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_QUESTION, question.getId());
       }
    }

    private void createNotify(Comment comment, Long receiver, String notifierName, String outerTitle, NotificationTypeEnum notificationType, Long outerId) {
        // 如果自己给自己回复，就不用通知
//        if (receiver == comment.getCommentator()) {
//            return;
//        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(notificationType.getType());
        notification.setOuterid(outerId);
        notification.setNotifier(comment.getCommentator());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setReceiver(receiver);
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);
        notificationMapper.insert(notification);
    }

    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {
//        找到父id是传入id的，且评论类型是1的comments
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());
        // 按照创建时间 倒序排序
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);

        if(comments.size() == 0){
            return new ArrayList<>();
        }
//       获取去重的评论人
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList<>();
        userIds.addAll(commentators);

//        获取评论人并转换为Map
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

//       转换 comment 为 commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment , commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOS;
    }

}

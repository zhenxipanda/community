package life.zhaohuan.community.community.mapper;

import life.zhaohuan.community.community.model.Comment;
import life.zhaohuan.community.community.model.CommentExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface CommentExtMapper {
    int incCommentCount(Comment comment);
}
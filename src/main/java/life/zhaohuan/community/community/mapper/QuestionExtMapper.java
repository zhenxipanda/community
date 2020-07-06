package life.zhaohuan.community.community.mapper;

import life.zhaohuan.community.community.model.Question;
import life.zhaohuan.community.community.model.QuestionExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface QuestionExtMapper {
    int incView(Question record);
    int incCommentCount(Question record);

}
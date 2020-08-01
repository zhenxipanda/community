package life.zhaohuan.community.community.schedule;

import life.zhaohuan.community.community.mapper.QuestionMapper;
import life.zhaohuan.community.community.model.Question;
import life.zhaohuan.community.community.model.QuestionExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class HotTagTasks {
    @Autowired
    private QuestionMapper questionMapper;

    @Scheduled(fixedRate = 1000 * 60 * 60 * 3)
    // 使用定时任务将数据库中的问题拿出来
    public void hotTagSchedule(){
        int offset = 0;
        int limit = 5;
        log.info("hotTagSchedule start {}",new Date());
        List<Question> list = new ArrayList<>();
        while(offset == 0 || list.size() == limit ){
            list = questionMapper.selectByExampleWithRowbounds(new QuestionExample() , new RowBounds(offset, limit));
            for(Question question : list){
                log.info("list question : {}",question.getId());
            }
            offset += limit;
        }
        log.info("hotTagSchedule stop {}",new Date());
    }
}

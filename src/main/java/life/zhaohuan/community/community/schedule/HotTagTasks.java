package life.zhaohuan.community.community.schedule;

import life.zhaohuan.community.community.cache.HotTagCache;
import life.zhaohuan.community.community.mapper.QuestionMapper;
import life.zhaohuan.community.community.model.Question;
import life.zhaohuan.community.community.model.QuestionExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class HotTagTasks {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private HotTagCache hotTagCache;

    @Scheduled(fixedRate = 1000 * 60 * 60 * 3)  // 1000是ms ,也就是1s，60s ,60min , - > 1h * 3 = 3h
    // 使用定时任务将数据库中的问题拿出来
    public void hotTagSchedule(){
        int offset = 0;
        int limit = 20;
        log.info("hotTagSchedule start {}",new Date());
        List<Question> list = new ArrayList<>();
        // 创建map 存储标签 及 优先级
        Map<String , Integer> priorities = new HashMap<>();
        while(offset == 0 || list.size() == limit ){
            list = questionMapper.selectByExampleWithRowbounds(new QuestionExample() , new RowBounds(offset, limit));
            for(Question question : list){
                String[] tags = StringUtils.split(question.getTag() , ",");
                for (String tag : tags) {
                    // 得到每个标签的优先级，初始为0
                    Integer priority = priorities.get(tag);
                    if(priority != null){
                        // 标签出现了多少次，就加了多少个5， 5 * question count + comment
                        priorities.put(tag , priority + 5 + question.getCommentCount());
                    }else{
                        priorities.put(tag , 5 + question.getCommentCount());
                    }
                }
            }
            offset += limit;
        }
//        priorities.forEach(
//                (k , v)->{
//                    System.out.println(k);
//                    System.out.println(":");
//                    System.out.println(v);
//                    System.out.println();
//                }
//        );
//        对标签的map，利用最小堆进行排序
        hotTagCache.updateTags(priorities);
        log.info("hotTagSchedule stop {}",new Date());
    }
}

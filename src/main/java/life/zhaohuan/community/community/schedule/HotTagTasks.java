package life.zhaohuan.community.community.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class HotTagTasks {
    @Scheduled(fixedRate = 1000 * 60 * 60 * 3)
    public void hotTagSchedule(){
        log.info("The time is now {}",new Date());
    }
}

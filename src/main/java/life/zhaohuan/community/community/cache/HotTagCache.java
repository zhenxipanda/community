package life.zhaohuan.community.community.cache;

import life.zhaohuan.community.community.dto.HotTagDTO;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Data
public class HotTagCache {
    private List<String> hots = new ArrayList<>();

    public void updateTags(Map<String, Integer> tags) {
//        堆的大小
        int max = 10;
//        最小堆
        PriorityQueue<HotTagDTO> priorityQueue = new PriorityQueue<>(max);

//        循环Map,获取每个tag的名称和优先级，再排序
        tags.forEach((name, priority) -> {
            HotTagDTO hotTagDTO = new HotTagDTO();
            hotTagDTO.setName(name);
            hotTagDTO.setPriority(priority);
            if (priorityQueue.size() < max) {
                priorityQueue.add(hotTagDTO);
            } else {
                HotTagDTO minHot = priorityQueue.peek(); //最小堆，这是最不热门的
                if (hotTagDTO.compareTo(minHot) > 0) {  //传入的 比 堆顶大
                    priorityQueue.poll(); //删堆顶
                    priorityQueue.offer(hotTagDTO); //加入新元素
                }
            }
        });
        List<String> sortedTags = new ArrayList<>();
//        遍历最小堆，将结果放入list中，且add(0 , num)，有序插入
        HotTagDTO poll = priorityQueue.poll();
        while(poll != null){
            sortedTags.add(0 , poll.getName());
            poll = priorityQueue.poll();
        }
        hots = sortedTags;
    }
}

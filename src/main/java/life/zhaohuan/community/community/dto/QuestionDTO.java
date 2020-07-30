package life.zhaohuan.community.community.dto;

import life.zhaohuan.community.community.model.User;
import lombok.Data;

@Data
public class QuestionDTO {
    private Long id;
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
    private Long creator;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    // 与Question 不同之处在于，多了User对象
    private User user;
}

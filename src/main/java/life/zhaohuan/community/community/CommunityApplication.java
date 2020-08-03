package life.zhaohuan.community.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("life.zhaohuan.community.community.mapper")  //这样能自动路由到Mapper文件
//开启 @Scheduled
@EnableScheduling
public class CommunityApplication {

    public static void main(String[] args) { SpringApplication.run(CommunityApplication.class, args);

    }

}

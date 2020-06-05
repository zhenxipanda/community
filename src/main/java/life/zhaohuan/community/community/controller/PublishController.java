package life.zhaohuan.community.community.controller;

import org.hibernate.validator.constraints.CodePointLength;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublishController {
    @GetMapping("/publish")
    public String publish(){
        return "publish";
    }
}

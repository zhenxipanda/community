package life.zhaohuan.community.community.advice;

import life.zhaohuan.community.community.exception.CustomizedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;


@ControllerAdvice
public class CustomizedExceptionHandler {
    @ExceptionHandler(Exception.class)
    ModelAndView handle(Throwable e, Model model) {
            if (e instanceof CustomizedException) {
                model.addAttribute("message", e.getMessage());
            } else {
                model.addAttribute("message", "服务冒烟了，要不然你稍后再试试！！！");
            }
            return new ModelAndView("error");
    }
}

package life.zhaohuan.community.community.advice;

import com.alibaba.fastjson.JSON;
import life.zhaohuan.community.community.dto.ResultDTO;
import life.zhaohuan.community.community.exception.CustomizedErrorCode;
import life.zhaohuan.community.community.exception.CustomizedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@ControllerAdvice
public class CustomizedExceptionHandler {
    @ExceptionHandler(Exception.class)
    ModelAndView handle(Throwable e, Model model, HttpServletRequest request, HttpServletResponse response) {
        String contentType = request.getContentType();
        if("application/json".equals(contentType)){
            //返回JSON
            ResultDTO resultDTO ;
            if (e instanceof CustomizedException) {
                resultDTO =  ResultDTO.errorOf((CustomizedException)e);
            } else {
                resultDTO =  ResultDTO.errorOf(CustomizedErrorCode.SYS_ERROR);
            }
            try {
                response.setContentType("application/json");
                response.setStatus(200);
                response.setCharacterEncoding("utf-8");
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException ioe) {
            }
            return  null;
        }
        else {
            //错误页面跳转
            if (e instanceof CustomizedException) {
                model.addAttribute("message", e.getMessage());
            } else {
                model.addAttribute("message", CustomizedErrorCode.SYS_ERROR.getMessage());
            }
            return new ModelAndView("error");
        }

    }
}

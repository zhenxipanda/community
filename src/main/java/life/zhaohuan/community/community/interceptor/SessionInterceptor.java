package life.zhaohuan.community.community.interceptor;

import life.zhaohuan.community.community.mapper.UserMapper;
import life.zhaohuan.community.community.model.User;
import life.zhaohuan.community.community.model.UserExample;
import life.zhaohuan.community.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service  // 加入这个注解，Spring就会接管，使得Autowired工作
//需要实现三个方法
public class SessionInterceptor implements HandlerInterceptor {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
//    希望在程序处理之前做 拦截器
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 实现持久化登录 ，是指在登录状态下，打开另一个浏览器输入网页，仍然保持登录状态
        // 前后端以 token 进行验证
        // 通过request得到cookie
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length != 0){
            for (Cookie cookie : cookies) {
                if(cookie != null){
                    // 找到 cookie 的 主键(name) 等于 “token”
                    if(cookie.getName().equals("token")){
                        // 获取cookie 的 值(value)
                        String token = cookie.getValue();
                        UserExample userExample = new UserExample();
                        userExample.createCriteria()
                                .andTokenEqualTo(token);
                        // 找到 User 中的 token 属性 的值 等于 cookie 的值 token 的用户
                        List<User> users = userMapper.selectByExample(userExample);
                        if(users.size() != 0){
                            // 将用户写到 session 中，在页面显示
                            request.getSession().setAttribute("user" , users.get(0));
                            Long unreadCount = notificationService.unreadCount(users.get(0).getId());
                            request.getSession().setAttribute("unreadCount" , unreadCount);
                        }
                        break;
                    }
                }

            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}

//package com.example.fw.main.Aspect;
//
//import com.example.fw.base.RequestType;
//import com.example.fw.main.Annotation.AuthToken;
//import com.example.fw.main.b.User;
//import com.example.fw.main.s.JiabiService;
//import com.example.fw.main.s.RequestTypeService;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//
///**
// * @Author 宋宗垚
// * @Date 2019/4/2 9:42
// * @Description TODO
// */
//
//@Aspect
//@Component
//public class AuthTokenAspect {
//
//    @Autowired
//    private RequestTypeService requestTypeService;
//    @Autowired
//    protected JiabiService mJiabiService;
//    /**
//     * Spring中使用@Pointcut注解来定义方法切入点
//     *
//     * @Pointcut 用来定义切点，针对方法
//     * @Aspect 用来定义切面，针对类 后面的增强均是围绕此切入点来完成的
//     * 此处仅配置被我们刚才定义的注解：AuthToken修饰的方法即可
//     *
//     */
//    @Pointcut("@annotation(authToken)")
//    public void doAuthToken(AuthToken authToken) {
//    }
//
//    /**
//     * 此处我使用环绕增强，在方法执行之前或者执行之后均会执行。
//     */
//    @Around("doAuthToken(authToken)")
//    public RequestType doAround(ProceedingJoinPoint pjp, AuthToken authToken) throws Throwable {
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = attributes.getRequest();
//        HttpSession session = request.getSession();
//        User user = (User)session.getAttribute("user");
//        String[] role_name = authToken.role_name();
//        if (user==null || (mJiabiService.getByparameter("uname",user.getUname())==null)){
//            // TODO: 对于用户的登录以及权限控制有待完善
//            return requestTypeService.sendFalse("无用户登录信息，请用户登录");
//        }else {
//            RequestType proceed = (RequestType)pjp.proceed();
//            return requestTypeService.sendTrue(proceed);
//        }
//    }
//
//}

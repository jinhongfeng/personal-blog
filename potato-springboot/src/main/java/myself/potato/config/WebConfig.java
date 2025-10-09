
package myself.potato.config;

import lombok.RequiredArgsConstructor;
import myself.potato.config.interceptor.LogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LogInterceptor logInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor)
                .addPathPatterns("/**")  // 拦截所有以 /api/ 开头的请求
                // 排除一些不需要记录日志的接口
                .excludePathPatterns(
                        "/user/login/**",  // 登录接口
                        "/operationLog/**", // 排除日志查询接口，避免自己记录自己

                        "/lovemessage/**", // 爱情里面的消息
                        "/loveline/**", // 爱情上传的线性日志
                        "/travelImage/**", // 旅游子图片接口
                        "/circleImage/**", // 朋友圈子图片接口
                        "/file/**", // 上传文件接口
                        "/music/**" // 音乐上传接口


                        );
    }
}


package myself.potato.config.interceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import myself.potato.service.impl.PageViewServiceImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class PageViewInterceptor implements HandlerInterceptor {

    private final PageViewServiceImpl pageViewService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求的 URI
        String uri = request.getRequestURI();

        // 定义需要统计的页面规则
        if (uri.endsWith(".html") || uri.startsWith("/blog/") || uri.startsWith("/diary/") || uri.startsWith("/note/")
                || uri.startsWith("/loveline/") || uri.startsWith("/message/") || uri.startsWith("/circle/")
                || uri.startsWith("/concern/") || uri.startsWith("/travel/")) {
            // 调用 Service 记录访问
            pageViewService.recordView(uri);
        }

        // 放行请求
        return true;
    }
}

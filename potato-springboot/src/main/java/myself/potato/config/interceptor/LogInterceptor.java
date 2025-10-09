
package myself.potato.config.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import myself.potato.common.Loggable;
import myself.potato.entity.OperationLog;
import myself.potato.service.IOperationLogService;
import myself.potato.service.impl.OperationLogServiceImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LogInterceptor implements HandlerInterceptor {

    private final OperationLogServiceImpl logService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true; // 必须返回true, 否则请求会被中断
    }

    @SneakyThrows // Lombok注解, 简化try-catch
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 1. 确保handler是一个方法处理器
        if (!(handler instanceof HandlerMethod)) {
            return;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 2. 检查方法上是否有 @Loggable 注解
        Loggable loggable = handlerMethod.getMethodAnnotation(Loggable.class);
        if (loggable == null) {
            return;
        }

        // 3. 如果有注解, 则构建日志对象
        OperationLog log = new OperationLog();

        // 获取用户信息 (这里假设你用Spring Security, 请根据你的实际情况修改)
        // SecurityContextHolder.getContext().getAuthentication().getName();
        String username = "system"; // 临时默认值, 请替换成真实的用户名获取逻辑
        log.setOperator(username);

        // 获取IP地址
        log.setIp(getClientIp(request));

        // 获取请求信息
        log.setUri(request.getRequestURI());
        log.setMethod(request.getMethod());

        // 从注解中获取操作信息
        log.setAction(loggable.action());
        log.setDescription(loggable.description());

        // 获取请求参数
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (!parameterMap.isEmpty()) {
            log.setParams(objectMapper.writeValueAsString(parameterMap));
        }

        // 4. 调用Service保存日志 (Service内部已异步)
        logService.saveLog(log);
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理, X-Forwarded-For的值会有多个, 第一个为真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}

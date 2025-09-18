package myself.potato.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    // 跨域请求最大有效时长（1天）
    private static final long MAX_AGE = 24 * 60 * 60;

    // 从配置文件读取允许的源
    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 添加允许的源
        for (String origin : allowedOrigins) {
            config.addAllowedOriginPattern(origin);
        }

        // 允许的请求头（* 表示所有）
        config.addAllowedHeader("*");

        // 允许的请求方法（* 表示所有）
        config.addAllowedMethod("*");

        // 允许携带认证信息（如 Cookie、Token）
        config.setAllowCredentials(true);

        // 预检请求的缓存时间（秒）
        config.setMaxAge(MAX_AGE);

        // 对所有接口生效
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

package myself.potato;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication

@MapperScan("myself.potato.mapper")
@EnableScheduling // 启用 Spring 的定时任务功能
public class PotatoSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(PotatoSpringbootApplication.class, args);
    }

}


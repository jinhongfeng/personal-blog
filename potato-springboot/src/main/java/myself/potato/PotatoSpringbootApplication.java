package myself.potato;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("myself.potato.mapper")
public class PotatoSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(PotatoSpringbootApplication.class, args);
    }

}


package myself.potato.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 注解可以用在方法上
@Retention(RetentionPolicy.RUNTIME) // 注解在运行时可见, 拦截器才能读取
public @interface Loggable {

    /**
     * 操作类型
     */
    String action();

    /**
     * 操作描述
     */
    String description() default "";
}

package myself.potato.exception;
import myself.potato.common.Constants;
import lombok.Getter;

/**
 * 自定义异常
 */

// 继承自 RuntimeException，方便在业务逻辑中抛出异常
public class ServiceException extends RuntimeException {
    private String code; // 异常对应的状态码

    // 构造函数，接收状态码和异常信息
    public ServiceException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    // 获取状态码的方法
    public String getCode() {
        return code;
    }
}

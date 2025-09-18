package myself.potato.common;

import lombok.Data;

@Data
public class Result {
    private String code;  // 返回的状态码
    private String msg;  // 如果失败，原因是什么
    private Object data;

    // 无参构造函数
    public Result() {
    }

    // 全参构造函数
    public Result(String code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Result success() {
        return new Result(Constants.CODE_200, "", null);
    }

    public static Result success(Object data) {
        return new Result(Constants.CODE_200, "", data);
    }

    public static Result success(String msg ) {
        return new Result(Constants.CODE_200, msg, null);
    }
    public static Result error(String code, String msg) {
        return new Result(code, msg, null);
    }

    public static Result error () {
        return new Result(Constants.CODE_500, "系统错误", null);
    }

    public static Result error (String msg) {
        return new Result(Constants.CODE_500, msg, null);
    }

}


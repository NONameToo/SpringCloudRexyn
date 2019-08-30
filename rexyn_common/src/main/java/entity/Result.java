package entity;

/**
 * @ClassName fushaokai
 * @Description 返回的Json对象
 * @Author Administrator
 * @Date 2019/8/9 0009 10:08
 * @Version 1.0
 **/
public class Result {

    private Boolean success; // 是否成功
    private Integer code;    // 状态码
    private String message;  // 返回信息
    private Object data;     // 返回的数据

    // 三种情况下的构造函数
    public Result() {
    }

    public Result(Boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public Result(Boolean success, Integer code, String message, Object data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }




    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

package entity;

/**
 * @ClassName fushaokai
 * @Description 状态码
 * @Author Administrator
 * @Date 2019/8/9 0009 10:20
 * @Version 1.0
 **/
public class StatusCode {
   public static final Integer OK = 200;       // 成功
   public static final Integer ERROR = 400;    // 失败
   public static final Integer LOGINERROR = 4001;   // 用户名或者密码错误
   public static final Integer ACCESSERROR = 4002;  // 权限不足
   public static final Integer REMOTEERROR = 4003;  // 远程调用失败
   public static final Integer REPTERROR = 4004;    // 重复操作

}

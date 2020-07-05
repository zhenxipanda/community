package life.zhaohuan.community.community.exception;

public class CustomizedException extends RuntimeException {
    private String message;
    private Integer code;
    public CustomizedException(ICustomizedErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}

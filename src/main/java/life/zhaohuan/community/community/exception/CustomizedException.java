package life.zhaohuan.community.community.exception;

public class CustomizedException extends RuntimeException {
    private String message;
    public CustomizedException(ICustomizedErrorCode errorCode) {
        this.message = errorCode.getMessage();
    }

    public CustomizedException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

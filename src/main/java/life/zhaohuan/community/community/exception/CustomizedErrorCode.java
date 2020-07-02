package life.zhaohuan.community.community.exception;

public enum CustomizedErrorCode implements ICustomizedErrorCode {
    QUESTION_NOT_FOUND("你找的问题不在了，换个试试？");
    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    CustomizedErrorCode(String message) {
        this.message = message;
    }
}

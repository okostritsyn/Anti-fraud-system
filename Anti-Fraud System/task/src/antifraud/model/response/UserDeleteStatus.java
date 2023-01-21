package antifraud.model.response;

public enum UserDeleteStatus {
    SUCCESS("Deleted successfully!"),
    USER_NOT_FOUND("User not found!");

    private final String message;

    UserDeleteStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

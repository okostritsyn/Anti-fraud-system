package antifraud.model.enums;

public enum UserDeleteStatus {
    SUCCESS("Deleted successfully!");

    private final String message;

    UserDeleteStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

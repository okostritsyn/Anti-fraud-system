package antifraud.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum UserAccessOperation {
    LOCK("locked"),UNLOCK("unlocked"),
    @JsonProperty("SHOULD NEVER ACTUALLY APPEAR IN REQUEST JSON")
    INVALID("Error");

    private final String message;

    @JsonCreator
    public static UserAccessOperation valueOfOrInvalid(String name) {
        try {
            return UserAccessOperation.valueOf(name);
        } catch (IllegalArgumentException e) {
            return INVALID;
        }
    }


    UserAccessOperation(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

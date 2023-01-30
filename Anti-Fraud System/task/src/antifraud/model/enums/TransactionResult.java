package antifraud.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum TransactionResult {
    ALLOWED,PROHIBITED,MANUAL_PROCESSING,
    @JsonProperty("SHOULD NEVER ACTUALLY APPEAR IN REQUEST JSON")
    INVALID;

    @JsonCreator
    public static TransactionResult valueOfOrInvalid(String name) {
        try {
            return TransactionResult.valueOf(name);
        } catch (IllegalArgumentException e) {
            return INVALID;
        }
    }
}

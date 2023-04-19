package antifraud.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum Region {
    EAP,ECA,HIC,LAC,MENA,SA,SSA,
    @JsonProperty("SHOULD NEVER ACTUALLY APPEAR IN REQUEST JSON")
    INVALID;

    @JsonCreator
    public static Region valueOfOrInvalid(String name) {
        try {
            return Region.valueOf(name);
        } catch (IllegalArgumentException e) {
            return INVALID;
        }
    }
}

package antifraud.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
   MERCHANT,
   ADMINISTRATOR,
   SUPPORT,

    @JsonProperty("SHOULD NEVER ACTUALLY APPEAR IN REQUEST JSON")
    INVALID;

    @JsonCreator
    public static Role valueOfOrInvalid(String name) {
        try {
            return Role.valueOf(name);
        } catch (IllegalArgumentException e) {
            return INVALID;
        }
    }

   @Override
   public String getAuthority() {
        return "ROLE_"+this.name();
    }
}

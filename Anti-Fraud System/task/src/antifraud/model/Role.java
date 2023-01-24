package antifraud.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
   MERCHANT,
   ADMINISTRATOR,
   SUPPORT;

   @Override
   public String getAuthority() {
        return "ROLE_"+this.name();
    }
}

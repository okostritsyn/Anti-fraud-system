package antifraud.model;

import antifraud.model.enums.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter@Setter
@Entity(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name ="username",unique = true)
    private String username;
    @Column(name ="name")
    private String name;
    private String password;
    @ElementCollection(targetClass = Role.class,fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role",
    joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();
    @Column(name ="active")
    private boolean active;
    @Column(name ="dateOfCreation")
    private LocalDateTime dateOfCreation;

    public User() {
        this.dateOfCreation = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    public void addRole(Role currRole) {
        if (currRole == null) return;
        roles.clear();
        roles.add(currRole);
    }

    public Role getFirstRole() {
        var rolesSet = getRoles();
        Role currRole = null;
        if (!rolesSet.isEmpty()) currRole = rolesSet.stream().findFirst().get();
        return currRole;
    }
}

package org.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import jakarta.persistence.*;
import org.example.backend.model.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Table(name = "users")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User implements UserDetails {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private UserRole role;

    @OneToMany(mappedBy = "waiter", cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Order> orders;

    @ManyToMany(mappedBy = "users")
    @JsonIgnore
    private Set<Workstation> workstations;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.role == UserRole.ADMIN)
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_CASHIER"),
                    new SimpleGrantedAuthority("ROLE_WAITER"),
                    new SimpleGrantedAuthority("ROLE_BARMAN"));
        else if (this.role == UserRole.CASHIER)
            return List.of(new SimpleGrantedAuthority("ROLE_CASHIER"),
                    new SimpleGrantedAuthority("ROLE_WAITER"),
                    new SimpleGrantedAuthority("ROLE_BARMAN"));
        else if (this.role == UserRole.WAITER)
            return List.of(new SimpleGrantedAuthority("ROLE_WAITER"));
        else
            return List.of(new SimpleGrantedAuthority("ROLE_BARMAN"));
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
        return true;
    }
}

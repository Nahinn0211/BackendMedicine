package hunre.edu.vn.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email", unique = true),
        @Index(name = "idx_user_phone", columnList = "phone")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id", "email"}, callSuper = false)
public class User extends BaseEntity {
    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 3, max = 100, message = "Họ tên phải từ 3-100 ký tự")
    @Column(name = "full_name", nullable = false, columnDefinition = "nvarchar(255)")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10,12}$", message = "Số điện thoại phải từ 10-12 chữ số")
    @Column(name = "phone", nullable = true)
    private String phone;

    @Column(name = "address", nullable = true, columnDefinition = "nvarchar(MAX)")
    private String address;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "last_login", nullable = true)
    private LocalDateTime lastLogin;

    @Column(name = "count_lock", nullable = false)
    @Min(value = 0, message = "Số lần khóa phải lớn hơn hoặc bằng 0")
    @Max(value = 5, message = "Số lần khóa không được vượt quá 5")
    private Integer countLock = 0;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull(message = "Trạng thái kích hoạt không được trống")
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @NotNull(message = "Trạng thái khóa không được trống")
    @Column(name = "locked", nullable = false)
    private Boolean locked = false;

    @Column(name = "avatar", nullable = true, columnDefinition = "varchar(255) default 'default-avatar.png'")
    private String avatar;

    @Column(name = "reset_password_token", nullable = true)
    private String resetPasswordToken;

    @Getter
    @Column(name = "reset_password_expiry", nullable = true)
    private LocalDateTime resetPasswordExpiry;

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserRole> userRoles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private Set<SocialAccount> socialAccounts = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private PatientProfile patientProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private DoctorProfile doctorProfile;

    public Set<Role> getRoles() {
        if (userRoles == null) {
            return Collections.emptySet();
        }

        Set<Role> roles = new HashSet<>();
        for (UserRole userRole : userRoles) {
            if (userRole.getRole() != null) {
                roles.add(userRole.getRole());
            }
        }
        return roles;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }

    public boolean isEnabled() {
        return enabled && !isDeleted();
    }

    private boolean isDeleted() {
        return this.getIsDeleted();
    }

    public boolean isLocked() {
        return locked;
    }

    public void addRole(Role role) {
        UserRole userRole = new UserRole();
        userRole.setUser(this);
        userRole.setRole(role);
        this.userRoles.add(userRole);
    }

    public void removeRole(Role role) {
        this.userRoles.removeIf(userRole -> userRole.getRole().equals(role));
    }

    public void incrementLoginAttempts() {
        this.countLock++;
        if (this.countLock >= 5) {
            this.locked = true;
        }
    }

    public void resetLoginAttempts() {
        this.countLock = 0;
        this.locked = false;
    }

    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
}

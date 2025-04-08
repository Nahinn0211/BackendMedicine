package hunre.edu.vn.backend.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles", uniqueConstraints = {
        @UniqueConstraint(name = "uc_role_name", columnNames = {"name"})
}, indexes = {
        @Index(name = "idx_role_name", columnList = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id", "name"})
public class Role extends BaseEntity {
    @NotBlank(message = "Quyền không được trống")
    @Size(min = 2, max = 50, message = "Tên quyền cần từ 2 đến 50 ký tự")
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "role",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();

    public void addUserRole(UserRole userRole) {
        if (userRole != null) {
            this.userRoles.add(userRole);
            userRole.setRole(this);
        }
    }

    public void removeUserRole(UserRole userRole) {
        if (userRole != null) {
            this.userRoles.remove(userRole);
            userRole.setRole(null);
        }
    }
}
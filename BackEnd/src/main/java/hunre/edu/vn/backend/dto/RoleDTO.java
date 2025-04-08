package hunre.edu.vn.backend.dto;

import hunre.edu.vn.backend.entity.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO Class for Role
 */
public class RoleDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetRoleDTO {
        private Long id;
        private String name;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetRoleDTOWithoutUserRoles {
        private Long id;
        private String name;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveRoleDTO {
        private Long id; // Optional for update

        @NotBlank(message = "Tên vai trò không được để trống")
        private String name;
    }

    public static GetRoleDTO fromEntity(Role role) {
        if (role == null) {
            return null;
        }

        return GetRoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
//                .userRoles(role.getUserRoles() != null ?
//                        role.getUserRoles().stream()
//                                .map(UserRoleDTO::fromEntity)
//                                .collect(Collectors.toSet()) : null)
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }

    public static GetRoleDTOWithoutUserRoles fromEntityWithoutUserRoles(Role role) {
        if (role == null) {
            return null;
        }

        return GetRoleDTOWithoutUserRoles.builder()
                .id(role.getId())
                .name(role.getName())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
}
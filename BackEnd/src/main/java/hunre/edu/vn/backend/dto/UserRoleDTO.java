package hunre.edu.vn.backend.dto;

import hunre.edu.vn.backend.entity.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO Class for UserRole
 */
public class UserRoleDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetUserRoleDTO {
        private Long id;
        private Long userId;
        private String userFullName;
        private Long roleId;
        private String roleName;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetUserRoleDTOWithoutDetails {
        private Long id;
        private Long userId;
        private Long roleId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveUserRoleDTO {
        private Long id; // Optional for update

        @NotNull(message = "Người dùng không được để trống")
        private Long userId;

        @NotNull(message = "Vai trò không được để trống")
        private Long roleId;
    }

    public static GetUserRoleDTO fromEntity(UserRole userRole) {
        if (userRole == null) {
            return null;
        }

        return GetUserRoleDTO.builder()
                .id(userRole.getId())
                .userId(userRole.getUser() != null ? userRole.getUser().getId() : null)
                .userFullName(userRole.getUser() != null ? userRole.getUser().getFullName() : null)
                .roleId(userRole.getRole() != null ? userRole.getRole().getId() : null)
                .roleName(userRole.getRole() != null ? userRole.getRole().getName() : null)
                .createdAt(userRole.getCreatedAt())
                .updatedAt(userRole.getUpdatedAt())
                .build();
    }

    public static GetUserRoleDTOWithoutDetails fromEntityWithoutDetails(UserRole userRole) {
        if (userRole == null) {
            return null;
        }

        return GetUserRoleDTOWithoutDetails.builder()
                .id(userRole.getId())
                .userId(userRole.getUser() != null ? userRole.getUser().getId() : null)
                .roleId(userRole.getRole() != null ? userRole.getRole().getId() : null)
                .createdAt(userRole.getCreatedAt())
                .updatedAt(userRole.getUpdatedAt())
                .build();
    }
}
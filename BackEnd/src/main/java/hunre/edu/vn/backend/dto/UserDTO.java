package hunre.edu.vn.backend.dto;

import hunre.edu.vn.backend.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO Class for User
 */
public class UserDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetUserDTO {
        private Long id;
        private String fullName;
        private String phone;
        private String address;
        private String email;
        private LocalDateTime lastLogin;
        private Boolean enabled;
        private Boolean locked;
        private String avatar;
        private Integer countLock;
        private List<RoleDTO.GetRoleDTO> roles;
        private List<SocialAccountDTO.GetSocialAccountDTO> socialAccounts;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetUserDTOWithoutDetails {
        private Long id;
        private String fullName;
        private String email;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveUserDTO {
        private Long id; // Optional for update

        @NotBlank(message = "Tên đầy đủ không được để trống")
        private String fullName;

        @Email(message = "Email không hợp lệ")
        @NotBlank(message = "Email không được để trống")
        private String email;
        private String password;
        private String phone;
        private String address;
        private String avatar;
        private Boolean enabled;
        private Boolean locked;
        private Integer countLock;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    public static GetUserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }

        GetUserDTO dto = GetUserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .email(user.getEmail())
                .lastLogin(user.getLastLogin())
                .enabled(user.getEnabled())
                .locked(user.getLocked())
                .avatar(user.getAvatar())
                .countLock(user.getCountLock())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        // Set roles
        if (user.getUserRoles() != null) {
            dto.setRoles(user.getUserRoles().stream()
                    .filter(ur -> !ur.getIsDeleted())
                    .map(ur -> RoleDTO.fromEntity(ur.getRole()))
                    .collect(Collectors.toList()));
        }

        // Set social accounts
        if (user.getSocialAccounts() != null) {
            dto.setSocialAccounts(user.getSocialAccounts().stream()
                    .filter(sa -> !sa.getIsDeleted())
                    .map(SocialAccountDTO::fromEntity)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public static GetUserDTOWithoutDetails fromEntityWithoutDetails(User user) {
        if (user == null) {
            return null;
        }

        return GetUserDTOWithoutDetails.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
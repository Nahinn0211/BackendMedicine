package hunre.edu.vn.backend.dto;

import hunre.edu.vn.backend.entity.SocialAccount;
import hunre.edu.vn.backend.entity.SocialProvider;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO Class for SocialAccount
 */
public class SocialAccountDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetSocialAccountDTO {
        private Long id;
        private Long userId;
        private SocialProvider provider;
        private String providerId;
        private String providerEmail;
        private String name;
        private String avatarUrl;
        private LocalDateTime tokenExpiresAt;
        private LocalDateTime lastSyncedAt;
        private Boolean isActive;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetSocialAccountDTOWithoutDetails {
        private Long id;
        private SocialProvider provider;
        private String name;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveSocialAccountDTO {
        private Long id; // Optional for update

        @NotBlank(message = "Provider ID không được để trống")
        private String providerId;
        private Long userId;
        private SocialProvider provider;
        private String providerEmail;
        private String name;
        private String avatarUrl;
    }

    public static GetSocialAccountDTO fromEntity(SocialAccount socialAccount) {
        if (socialAccount == null) {
            return null;
        }

        return GetSocialAccountDTO.builder()
                .id(socialAccount.getId())
                .userId(socialAccount.getUser() != null ? socialAccount.getUser().getId() : null)
                .provider(socialAccount.getProvider())
                .providerId(socialAccount.getProviderId())
                .providerEmail(socialAccount.getProviderEmail())
                .name(socialAccount.getName())
                .avatarUrl(socialAccount.getAvatarUrl())
                .tokenExpiresAt(socialAccount.getTokenExpiresAt())
                .lastSyncedAt(socialAccount.getLastSyncedAt())
                .isActive(socialAccount.isActive())
                .createdAt(socialAccount.getCreatedAt())
                .updatedAt(socialAccount.getUpdatedAt())
                .build();
    }

    public static GetSocialAccountDTOWithoutDetails fromEntityWithoutDetails(SocialAccount socialAccount) {
        if (socialAccount == null) {
            return null;
        }

        return GetSocialAccountDTOWithoutDetails.builder()
                .id(socialAccount.getId())
                .provider(socialAccount.getProvider())
                .name(socialAccount.getName())
                .createdAt(socialAccount.getCreatedAt())
                .updatedAt(socialAccount.getUpdatedAt())
                .build();
    }
}
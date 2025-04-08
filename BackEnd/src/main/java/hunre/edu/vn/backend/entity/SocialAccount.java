package hunre.edu.vn.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "social_accounts", indexes = {
        @Index(name = "idx_social_account_user", columnList = "user_id"),
        @Index(name = "idx_social_account_provider", columnList = "provider"),
        @Index(name = "idx_social_account_provider_id", columnList = "provider_id"),
        @Index(name = "idx_social_account_expires", columnList = "token_expires_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class SocialAccount extends BaseEntity {
    @NotNull(message = "Người dùng không được trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Loại liên kết xã hội không được trống")
    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private SocialProvider provider;

    @NotBlank(message = "Id liên kết không được tống")
    @Column(name = "provider_id", nullable = false, unique = true)
    private String providerId;

    @Email
    @Column(name = "provider_email", nullable = true)
    private String providerEmail;

    @Size(max = 255, message = "Tên dưới 255 ký tự")
    @Column(name = "name", nullable = true, columnDefinition = "nvarchar(255)")
    private String name;

    @Column(name = "avatar_url", nullable = true)
    private String avatarUrl;

    @Column(name = "access_token", nullable = true, length = 1000)
    private String accessToken;

    @Column(name = "refresh_token", nullable = true, length = 1000)
    private String refreshToken;

    @Column(name = "token_expires_at", nullable = true)
    private LocalDateTime tokenExpiresAt;

    @Column(name = "last_synced_at", nullable = true)
    private LocalDateTime lastSyncedAt;

    public boolean isTokenExpired() {
        return tokenExpiresAt != null && LocalDateTime.now().isAfter(tokenExpiresAt);
    }

    public void updateTokens(String newAccessToken, String newRefreshToken, LocalDateTime expiresAt) {
        this.accessToken = newAccessToken;
        this.refreshToken = newRefreshToken;
        this.tokenExpiresAt = expiresAt;
        this.lastSyncedAt = LocalDateTime.now();
    }

    public void clearTokens() {
        this.accessToken = null;
        this.refreshToken = null;
        this.tokenExpiresAt = null;
    }

    public String getDisplayName() {
        if (name != null && !name.isEmpty()) {
            return name;
        }
        return provider + " Account";
    }

    public boolean isActive() {
        return !isTokenExpired() && accessToken != null;
    }
}
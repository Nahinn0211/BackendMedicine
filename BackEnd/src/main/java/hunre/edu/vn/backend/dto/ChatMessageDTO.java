package hunre.edu.vn.backend.dto;

import hunre.edu.vn.backend.entity.ChatMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {
    private Long id;
    private Long consultationId;
    private Long senderId;
    private ChatMessage.SenderType senderType;
    private String content;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private ChatMessage.MessageType messageType;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetChatMessageDTO {
        private Long id;
        private Long consultationId;
        private Long senderId;
        private ChatMessage.SenderType senderType;
        private String content;
        private LocalDateTime sentAt;
        private LocalDateTime readAt;
        private ChatMessage.MessageType messageType;
        private Boolean isEdited;
        private LocalDateTime editedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveChatMessageDTO {
        private Long id; // Optional for update

        @NotBlank(message = "Nội dung tin nhắn không được trống")
        private String content;

        private Long consultationId;
        private Long senderId;
        private ChatMessage.SenderType senderType;
        private ChatMessage.MessageType messageType;
    }

    // Static method to convert Entity to DTO
    public static GetChatMessageDTO fromEntity(ChatMessage chatMessage) {
        if (chatMessage == null) {
            return null;
        }

        return GetChatMessageDTO.builder()
                .id(chatMessage.getId())
                .consultationId(chatMessage.getConsultation().getId())
                .senderId(chatMessage.getSenderId())
                .senderType(chatMessage.getSenderType())
                .content(chatMessage.getContent())
                .sentAt(chatMessage.getSentAt())
                .readAt(chatMessage.getReadAt())
                .messageType(chatMessage.getMessageType())
                .isEdited(chatMessage.getIsEdited())
                .editedAt(chatMessage.getEditedAt())
                .createdAt(chatMessage.getCreatedAt())
                .updatedAt(chatMessage.getUpdatedAt())
                .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetChatHistoryDto {
        private Long consultationId;
        private LocalDateTime fromTime;
        private Integer limit;
        private Integer page;
    }
}
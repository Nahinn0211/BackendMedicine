package hunre.edu.vn.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages", indexes = {
        @Index(name = "idx_chatmessage_consultation", columnList = "consultation_id"),
        @Index(name = "idx_chatmessage_sender", columnList = "sender_id, sender_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ChatMessage extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "consultation_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Consultation consultation;

    @NotNull(message = "ID người gửi không được trống")
    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @NotNull(message = "Loại người gửi không được trống")
    @Column(name = "sender_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SenderType senderType;

    @NotBlank(message = "Nội dung không được để trống")
    @Size(max = 10000, message = "Nội dung không được vượt quá 10000 ký tự")
    @Column(name = "content", nullable = false, columnDefinition = "nvarchar(MAX)")
    private String content;

    @NotNull(message = "Thời gian gửi không được trống")
    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @NotNull(message = "Loại tin nhắn không được trống")
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;

    @Column(name = "is_edited", nullable = true)
    private Boolean isEdited = false;

    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    public enum SenderType {
        DOCTOR, PATIENT
    }

    public enum MessageType {
        TEXT, VIDEO_START, VIDEO_END, AUDIO_TOGGLE, JOIN, LEAVE, SYSTEM, IMAGE, FILE
    }

    public void markAsRead() {
        if (this.readAt == null) {
            this.readAt = LocalDateTime.now();
        }
    }

    public void editContent(String newContent) {
        this.content = newContent;
        this.isEdited = true;
        this.editedAt = LocalDateTime.now();
    }

    public boolean isSystemMessage() {
        return this.messageType == MessageType.SYSTEM;
    }
}

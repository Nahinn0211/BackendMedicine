package hunre.edu.vn.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consultations", indexes = {
        @Index(name = "idx_consultation_code", columnList = "consultation_code", unique = true),
        @Index(name = "idx_consultation_patient", columnList = "patient_id"),
        @Index(name = "idx_consultation_doctor", columnList = "doctor_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Consultation extends BaseEntity {
    @NotNull(message = "Bệnh nhân không được trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientProfile patient;

    @NotNull(message = "Bác sĩ không được trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorProfile doctor;

    @OneToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "appointment_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Appointment appointment;

    @NotBlank(message = "Mã tư vấn không được để trống")
    @Pattern(regexp = "^[A-Z0-9]{8}$", message = "Mã tư vấn phải có 8 ký tự chữ hoa hoặc số")
    @Column(name = "consultation_code", unique = true)
    private String consultationCode;

    @Column(name = "consultation_link", nullable = true)
    private String consultationLink;

    @NotNull(message = "Trạng thái không được trống")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ConsultationStatus status;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "session_token")
    private String sessionToken;

    @Column(name = "rtc_session_id")
    private String rtcSessionId;

    @Column(name = "is_video_enabled")
    private Boolean isVideoEnabled;

    @Column(name = "notes", columnDefinition = "nvarchar(MAX)")
    private String notes;

    @Column(name = "diagnosis", columnDefinition = "nvarchar(MAX)")
    private String diagnosis;

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<ChatMessage> chatMessages = new ArrayList<>();

    public void addChatMessage(ChatMessage message) {
        chatMessages.add(message);
        message.setConsultation(this);
    }

    public void startSession(String sessionToken, String rtcSessionId) {
        this.sessionToken = sessionToken;
        this.rtcSessionId = rtcSessionId;
        this.startedAt = LocalDateTime.now();
        this.status = ConsultationStatus.IN_PROGRESS;
        this.isVideoEnabled = false;
    }

    public void endSession() {
        this.endedAt = LocalDateTime.now();
        this.status = ConsultationStatus.COMPLETED;
        this.isVideoEnabled = false;
    }

    public Long getDurationMinutes() {
        if (this.startedAt == null || this.endedAt == null) {
            return null;
        }

        return Duration.between(this.startedAt, this.endedAt).toMinutes();
    }

    public boolean isInProgress() {
        return this.status == ConsultationStatus.IN_PROGRESS;
    }

    public boolean isCompleted() {
        return this.status == ConsultationStatus.COMPLETED;
    }

    public void updateDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
}

package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.ChatMessage;
import hunre.edu.vn.backend.entity.Consultation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends BaseRepository<ChatMessage> {

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.consultation.id = :consultationId AND cm.isDeleted = false")
    List<ChatMessage> findByConsultationId(Long consultationId, Sort sort);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.consultation.id = :consultationId AND cm.isDeleted = false")
    List<ChatMessage> findByConsultationId(Long consultationId, Pageable pageable);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.consultation.id = :consultationId AND cm.sentAt < :sentAt AND cm.isDeleted = false")
    List<ChatMessage> findByConsultationIdAndSentAtBefore(
            Long consultationId,
            LocalDateTime sentAt,
            Pageable pageable);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.consultation.id = :consultationId AND cm.senderId <> :senderId AND cm.readAt IS NULL AND cm.isDeleted = false")
    List<ChatMessage> findByConsultationIdAndSenderIdNotAndReadAtIsNull(
            Long consultationId,
            Long senderId);

    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.consultation.id = :consultationId AND cm.senderId <> :senderId AND cm.readAt IS NULL AND cm.isDeleted = false")
    int countByConsultationIdAndSenderIdNotAndReadAtIsNull(
            Long consultationId,
            Long senderId);
}
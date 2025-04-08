package hunre.edu.vn.backend.service;

import hunre.edu.vn.backend.dto.ChatMessageDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageService {
    ChatMessageDTO.GetChatMessageDTO save(ChatMessageDTO.SaveChatMessageDTO messageDto);
    List<ChatMessageDTO.GetChatMessageDTO> findByConsultationId(Long consultationId);
    List<ChatMessageDTO.GetChatMessageDTO> findByConsultationIdPaginated(Long consultationId, LocalDateTime fromTime, int limit);
    void markAsRead(Long consultationId, Long recipientId);
    int countUnreadMessages(Long consultationId, Long recipientId);
    List<ChatMessageDTO.GetChatMessageDTO> getRecentMessages(Long consultationId, int limit);
}
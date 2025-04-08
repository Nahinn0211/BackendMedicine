package hunre.edu.vn.backend.serviceImpl;

import hunre.edu.vn.backend.dto.ChatMessageDTO;
import hunre.edu.vn.backend.entity.ChatMessage;
import hunre.edu.vn.backend.entity.Consultation;
import hunre.edu.vn.backend.mapper.ChatMessageMapper;
import hunre.edu.vn.backend.repository.ChatMessageRepository;
import hunre.edu.vn.backend.repository.ConsultationRepository;
import hunre.edu.vn.backend.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatMessageServiceImpl implements ChatMessageService {
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ConsultationRepository consultationRepository;
    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Override
    @Transactional
    public ChatMessageDTO.GetChatMessageDTO save(ChatMessageDTO.SaveChatMessageDTO dto) {
        Consultation consultation = consultationRepository.findActiveById(dto.getConsultationId())
                .orElseThrow(() -> new RuntimeException("Consultation not found"));

        ChatMessage message = ChatMessage.builder()
                .consultation(consultation)
                .senderId(dto.getSenderId())
                .senderType(dto.getSenderType())
                .content(dto.getContent())
                .messageType(dto.getMessageType())
                .sentAt(LocalDateTime.now())
                .build();
        if (message.getIsEdited() == null) {
            message.setIsEdited(false);
        }

        ChatMessage saved = chatMessageRepository.save(message);
        return chatMessageMapper.toGetChatMessageDTO(saved);
    }

    @Override
    public List<ChatMessageDTO.GetChatMessageDTO> findByConsultationId(Long consultationId) {
        return chatMessageRepository.findByConsultationId(consultationId,Sort.by(Sort.Direction.DESC, "sentAt"))
                .stream()
                .map(chatMessageMapper::toGetChatMessageDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatMessageDTO.GetChatMessageDTO> findByConsultationIdPaginated(Long consultationId,
                                                              LocalDateTime fromTime,
                                                              int limit) {
        LocalDateTime searchTime = fromTime != null ? fromTime : LocalDateTime.now();

        return chatMessageRepository.findByConsultationIdAndSentAtBefore(consultationId, searchTime,
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "sentAt"))).stream()
                .map(chatMessageMapper::toGetChatMessageDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long consultationId, Long recipientId) {
        List<ChatMessage> unreadMessages = chatMessageRepository
                .findByConsultationIdAndSenderIdNotAndReadAtIsNull(
                        consultationId, recipientId);

        LocalDateTime now = LocalDateTime.now();
        unreadMessages.forEach(message -> message.setReadAt(now));

        chatMessageRepository.saveAll(unreadMessages);
    }

    @Override
    public int countUnreadMessages(Long consultationId, Long recipientId) {
        return chatMessageRepository
                .countByConsultationIdAndSenderIdNotAndReadAtIsNull(
                        consultationId, recipientId);
    }

    @Override
    public List<ChatMessageDTO.GetChatMessageDTO> getRecentMessages(Long consultationId, int limit) {
        return chatMessageRepository.findByConsultationId(
                        consultationId,
                        PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "sentAt"))
                ).stream()
                .map(chatMessageMapper::toGetChatMessageDTO)
                .collect(Collectors.toList());
    }
}
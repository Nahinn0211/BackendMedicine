package hunre.edu.vn.backend.service;


import hunre.edu.vn.backend.dto.ChatMessageDTO;
import hunre.edu.vn.backend.dto.ConsultationDTO;

import java.util.List;
import java.util.Optional;

public interface ConsultationService {
    List<ConsultationDTO.GetConsultationDTO> findAll();
    Optional<ConsultationDTO.GetConsultationDTO> findById(Long id);
    ConsultationDTO.GetConsultationDTO saveOrUpdate(ConsultationDTO.SaveConsultationDTO consultationDTO);
    String deleteByList(List<Long> ids);
    List<ConsultationDTO.GetConsultationDTO> findByPatientId(Long patientId);
    ConsultationDTO.GetConsultationDTO startConsultationSession(ConsultationDTO.StartSessionDto startSessionDto);
    ConsultationDTO.GetConsultationDTO endConsultationSession(ConsultationDTO.EndSessionDto endSessionDto);
    void toggleVideoStream(Long consultationId, Long userId, boolean isEnabled);
    ConsultationDTO.GetConsultationDTO getActiveSession(Long consultationId);
    ChatMessageDTO.GetChatMessageDTO sendChatMessage(ChatMessageDTO.SaveChatMessageDTO messageDto);
    List<ChatMessageDTO.GetChatMessageDTO> getChatHistory(ChatMessageDTO.GetChatHistoryDto historyDto);
    void markMessagesAsRead(Long consultationId, Long userId);
    Optional<ConsultationDTO.GetConsultationDTO> getConsultationByAppointmentId(Long id);
    Optional<ConsultationDTO.GetConsultationDTO> getConsultationByCode(String code);
}
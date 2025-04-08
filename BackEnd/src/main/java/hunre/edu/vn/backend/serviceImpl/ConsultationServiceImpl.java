package hunre.edu.vn.backend.serviceImpl;

import hunre.edu.vn.backend.dto.ChatMessageDTO;
import hunre.edu.vn.backend.dto.ConsultationDTO;
import hunre.edu.vn.backend.entity.*;
import hunre.edu.vn.backend.exception.ResourceNotFoundException;
import hunre.edu.vn.backend.mapper.ConsultationMapper;
import hunre.edu.vn.backend.repository.AppointmentRepository;
import hunre.edu.vn.backend.repository.ConsultationRepository;
import hunre.edu.vn.backend.repository.DoctorProfileRepository;
import hunre.edu.vn.backend.repository.PatientProfileRepository;
import hunre.edu.vn.backend.service.ChatMessageService;
import hunre.edu.vn.backend.service.ConsultationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ConsultationServiceImpl implements ConsultationService {
    private final ConsultationRepository consultationRepository;
    private final PatientProfileRepository patientRepository;
    private final DoctorProfileRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ConsultationMapper consultationMapper;

    @Override
    public List<ConsultationDTO.GetConsultationDTO> findAll() {
        return consultationRepository.findAllActive().stream()
                .map(consultation -> {
                    ConsultationDTO.GetConsultationDTO dto = consultationMapper.toGetConsultationDTO(consultation);
                    enrichConsultationDto(dto, consultation);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ConsultationDTO.GetConsultationDTO> findById(Long id) {
        return consultationRepository.findActiveById(id)
                .map(consultation -> {
                    ConsultationDTO.GetConsultationDTO dto = consultationMapper.toGetConsultationDTO(consultation);
                    enrichConsultationDto(dto, consultation);
                    return dto;
                });
    }

    @Override
    @Transactional
    public ConsultationDTO.GetConsultationDTO saveOrUpdate(ConsultationDTO.SaveConsultationDTO dto) {
        Consultation consultation;

        if (dto.getId() != null) {
            consultation = consultationRepository.findActiveById(dto.getId())
                    .orElse(new Consultation());
        } else {
            consultation = new Consultation();
            String consultationCode = generateUniqueConsultationCode();
            String baseVideoCallUrl = "http://localhost:5173/consultation";
            String consultationLink = baseVideoCallUrl + "/" + consultationCode;
            consultation.setConsultationCode(consultationCode);
            consultation.setConsultationLink(consultationLink);
        }

        PatientProfile patient = patientRepository.findActiveById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + dto.getPatientId()));

        DoctorProfile doctor = doctorRepository.findActiveById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + dto.getDoctorId()));

        Appointment appointment = null;
        if (dto.getAppointmentId() != null) {
            appointment = appointmentRepository.findActiveById(dto.getAppointmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + dto.getAppointmentId()));
        }

        consultation.setPatient(patient);
        consultation.setDoctor(doctor);
        consultation.setAppointment(appointment);
        consultation.setStatus(dto.getStatus());

        Consultation saved = consultationRepository.save(consultation);
        ConsultationDTO.GetConsultationDTO resultDto = consultationMapper.toGetConsultationDTO(saved);
        enrichConsultationDto(resultDto, saved);
        return resultDto;
    }

    @Override
    public String deleteByList(List<Long> ids) {
        for (Long id : ids) {
            if (consultationRepository.findActiveById(id).isPresent()) {
                consultationRepository.softDelete(id);
            }
        }
        return "Đã xóa thành công " +ids.size() + " cuộc tư vấn ";
    }

    @Override
    public List<ConsultationDTO.GetConsultationDTO> findByPatientId(Long patientId) {
        return consultationRepository.findByPatientId(patientId).stream()
                .map(consultation -> {
                    ConsultationDTO.GetConsultationDTO dto = consultationMapper.toGetConsultationDTO(consultation);
                    enrichConsultationDto(dto, consultation);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ConsultationDTO.GetConsultationDTO startConsultationSession(ConsultationDTO.StartSessionDto dto) {
        Consultation consultation = consultationRepository.findActiveById(dto.getConsultationId())
                .orElseThrow(() -> new ResourceNotFoundException("Consultation not found with id: " + dto.getConsultationId()));

        String sessionToken = UUID.randomUUID().toString();
        String rtcSessionId = "rtc_" + UUID.randomUUID().toString().substring(0, 8);

        consultation.startSession(sessionToken, rtcSessionId);
        Consultation saved = consultationRepository.save(consultation);

        ChatMessageDTO.SaveChatMessageDTO messageDto = ChatMessageDTO.SaveChatMessageDTO.builder()
                .consultationId(dto.getConsultationId())
                .senderId(0L)
                .senderType(ChatMessage.SenderType.PATIENT)
                .content("Phiên tư vấn đã bắt đầu")
                .messageType(ChatMessage.MessageType.SYSTEM)
                .build();

        chatMessageService.save(messageDto);

        messagingTemplate.convertAndSend(
                "/topic/consultation/" + dto.getConsultationId(),
                "SESSION_STARTED"
        );

        ConsultationDTO.GetConsultationDTO resultDto = consultationMapper.toGetConsultationDTO(saved);
        enrichConsultationDto(resultDto, saved);
        return resultDto;
    }

    @Override
    @Transactional
    public ConsultationDTO.GetConsultationDTO endConsultationSession(ConsultationDTO.EndSessionDto dto) {
        Consultation consultation = consultationRepository.findActiveById(dto.getConsultationId())
                .orElseThrow(() -> new ResourceNotFoundException("Consultation not found with id: " + dto.getConsultationId()));

        consultation.endSession();
        Consultation saved = consultationRepository.save(consultation);

        ChatMessageDTO.SaveChatMessageDTO messageDto = ChatMessageDTO.SaveChatMessageDTO.builder()
                .consultationId(dto.getConsultationId())
                .senderId(dto.getTerminatorId())
                .senderType(ChatMessage.SenderType.valueOf(dto.getTerminatorType()))
                .content("Phiên tư vấn đã kết thúc: " + dto.getTerminationReason())
                .messageType(ChatMessage.MessageType.SYSTEM)
                .build();

        chatMessageService.save(messageDto);

        messagingTemplate.convertAndSend(
                "/topic/consultation/" + dto.getConsultationId(),
                "SESSION_ENDED"
        );

        ConsultationDTO.GetConsultationDTO resultDto = consultationMapper.toGetConsultationDTO(saved);
        enrichConsultationDto(resultDto, saved);
        return resultDto;
    }

    @Override
    @Transactional
    public void toggleVideoStream(Long consultationId, Long userId, boolean isEnabled) {
        Consultation consultation = consultationRepository.findActiveById(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation not found with id: " + consultationId));

        consultation.setIsVideoEnabled(isEnabled);
        consultationRepository.save(consultation);

        messagingTemplate.convertAndSend(
                "/topic/consultation/" + consultationId + "/video",
                isEnabled ? "VIDEO_ENABLED" : "VIDEO_DISABLED"
        );
    }

    @Override
    public ConsultationDTO.GetConsultationDTO getActiveSession(Long consultationId) {
        Consultation consultation = consultationRepository.findActiveById(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation not found with id: " + consultationId));

        if (consultation.getStatus() != ConsultationStatus.IN_PROGRESS) {
            throw new ResourceNotFoundException("No active session for this consultation");
        }

        ConsultationDTO.GetConsultationDTO resultDto = consultationMapper.toGetConsultationDTO(consultation);
        enrichConsultationDto(resultDto, consultation);
        return resultDto;
    }

    @Override
    @Transactional
    public ChatMessageDTO.GetChatMessageDTO sendChatMessage(ChatMessageDTO.SaveChatMessageDTO messageDto) {
        ChatMessageDTO.GetChatMessageDTO savedMessage = chatMessageService.save(messageDto);

        messagingTemplate.convertAndSend(
                "/topic/consultation/" + messageDto.getConsultationId() + "/chat",
                savedMessage
        );

        return savedMessage;
    }

    @Override
    public List<ChatMessageDTO.GetChatMessageDTO> getChatHistory(ChatMessageDTO.GetChatHistoryDto historyDto) {
        return chatMessageService.findByConsultationIdPaginated(
                historyDto.getConsultationId(),
                historyDto.getFromTime(),
                historyDto.getLimit()
        );
    }

    @Override
    @Transactional
    public void markMessagesAsRead(Long consultationId, Long userId) {
        chatMessageService.markAsRead(consultationId, userId);
    }

    @Override
    public Optional<ConsultationDTO.GetConsultationDTO> getConsultationByAppointmentId(Long appointmentId) {
        return consultationRepository.findByAppointmentId(appointmentId)
                .map(consultationMapper::toGetConsultationDTO);
    }

    @Override
    public Optional<ConsultationDTO.GetConsultationDTO> getConsultationByCode(String code) {
        return consultationRepository.findByConsultationCode(code)
                .map(consultationMapper::toGetConsultationDTO);
    }

    private void enrichConsultationDto(ConsultationDTO.GetConsultationDTO dto, Consultation consultation) {
        dto.setPatientName(consultation.getPatient().getUser().getFullName());
        dto.setDoctorName(consultation.getDoctor().getUser().getFullName());
        List<ChatMessageDTO.GetChatMessageDTO> recentMessages = chatMessageService.getRecentMessages(consultation.getId(), 5);
        dto.setRecentMessages(recentMessages);
    }

    private String generateUniqueConsultationCode() {
        String code;
        do {
            // Generate a random 8-character alphanumeric code
            code = UUID.randomUUID().toString().substring(0, 8)
                    .replace("-", "")
                    .toUpperCase();
        } while (consultationRepository.existsByConsultationCode(code));

        return code;
    }
}
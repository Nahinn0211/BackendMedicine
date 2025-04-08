package hunre.edu.vn.backend.controller;

import hunre.edu.vn.backend.dto.ChatMessageDTO;
import hunre.edu.vn.backend.dto.ConsultationDTO;
import hunre.edu.vn.backend.service.ConsultationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultations")
@RequiredArgsConstructor
public class ConsultationController {
    private final ConsultationService consultationService;

    // Get all consultations
    @GetMapping
    public ResponseEntity<List<ConsultationDTO.GetConsultationDTO>> getAllConsultations() {
        return ResponseEntity.ok(consultationService.findAll());
    }

    // Get consultation by ID
    @GetMapping("/{id}")
    public ResponseEntity<ConsultationDTO.GetConsultationDTO> getConsultationById(@PathVariable Long id) {
        return consultationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create or update consultation
    @PostMapping
    public ResponseEntity<ConsultationDTO.GetConsultationDTO> createConsultation(
            @RequestBody ConsultationDTO.SaveConsultationDTO consultationDto) {
        return ResponseEntity.ok(consultationService.saveOrUpdate(consultationDto));
    }

    // Delete consultation
    @DeleteMapping("/{id}")
    public String deleteConsultation(@RequestBody List<Long> ids) {
        return consultationService.deleteByList(ids);
    }

    // Get consultations by patient ID
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ConsultationDTO.GetConsultationDTO>> getConsultationsByPatientId(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(consultationService.findByPatientId(patientId));
    }

    // Start consultation session
    @PostMapping("/start-session")
    public ResponseEntity<ConsultationDTO.GetConsultationDTO> startConsultationSession(
            @RequestBody ConsultationDTO.StartSessionDto startSessionDto) {
        return ResponseEntity.ok(consultationService.startConsultationSession(startSessionDto));
    }

    // End consultation session
    @PostMapping("/end-session")
    public ResponseEntity<ConsultationDTO.GetConsultationDTO> endConsultationSession(
            @RequestBody ConsultationDTO.EndSessionDto endSessionDto) {
        return ResponseEntity.ok(consultationService.endConsultationSession(endSessionDto));
    }

    // Toggle video stream
    @PostMapping("/{consultationId}/toggle-video")
    public ResponseEntity<Void> toggleVideoStream(
            @PathVariable Long consultationId,
            @RequestParam Long userId,
            @RequestParam boolean isEnabled) {
        consultationService.toggleVideoStream(consultationId, userId, isEnabled);
        return ResponseEntity.ok().build();
    }

    // Get active session
    @GetMapping("/{consultationId}/active-session")
    public ResponseEntity<ConsultationDTO.GetConsultationDTO> getActiveSession(
            @PathVariable Long consultationId) {
        return ResponseEntity.ok(consultationService.getActiveSession(consultationId));
    }

    // Send chat message
    @PostMapping("/chat")
    public ResponseEntity<ChatMessageDTO.GetChatMessageDTO> sendChatMessage(
            @RequestBody ChatMessageDTO.SaveChatMessageDTO messageDto) {
        return ResponseEntity.ok(consultationService.sendChatMessage(messageDto));
    }

    // Get chat history
    @PostMapping("/chat-history")
    public ResponseEntity<List<ChatMessageDTO.GetChatMessageDTO>> getChatHistory(
            @RequestBody ChatMessageDTO.GetChatHistoryDto historyDto) {
        return ResponseEntity.ok(consultationService.getChatHistory(historyDto));
    }

    // Mark messages as read
    @PostMapping("/{consultationId}/mark-read")
    public ResponseEntity<Void> markMessagesAsRead(
            @PathVariable Long consultationId,
            @RequestParam Long userId) {
        consultationService.markMessagesAsRead(consultationId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/by-appointment/{id}")
    public ResponseEntity<ConsultationDTO.GetConsultationDTO> getConsultationsByAppointmentId(@PathVariable Long id) {
        return consultationService.getConsultationByAppointmentId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ConsultationDTO.GetConsultationDTO> getConsultationByCode(@PathVariable String code) {
        return  consultationService.getConsultationByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
package hunre.edu.vn.backend.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller xử lý các tin nhắn WebSocket liên quan đến WebRTC và video stream
 * trong hệ thống telemedicine.
 */
@Controller
public class WebRTCSignalingController {
    private static final Logger logger = LoggerFactory.getLogger(WebRTCSignalingController.class);

    /**
     * Xử lý tin nhắn tín hiệu WebRTC (offer, answer, ice candidate)
     *
     * @param consultationId ID của cuộc tư vấn
     * @param message Nội dung tin nhắn WebRTC
     * @return Tin nhắn được chuyển tiếp tới các clients
     */
    @MessageMapping("/consultation/{consultationId}/webrtc")
    @SendTo("/topic/consultation/{consultationId}/webrtc")
    public Object handleWebRTCMessage(@DestinationVariable Long consultationId,
                                      @Payload Object message) {
        logger.debug("Received WebRTC signal for consultation {}: {}", consultationId, message);
        return message;
    }

    /**
     * Xử lý tin nhắn hệ thống như kết nối, ngắt kết nối
     *
     * @param consultationId ID của cuộc tư vấn
     * @param message Nội dung tin nhắn hệ thống
     * @return Tin nhắn được chuyển tiếp tới các clients
     */
    @MessageMapping("/consultation/{consultationId}/system")
    @SendTo("/topic/consultation/{consultationId}")
    public Object handleSystemMessage(@DestinationVariable Long consultationId,
                                      @Payload Object message) {
        logger.debug("Received system message for consultation {}: {}", consultationId, message);
        return message;
    }

    /**
     * Xử lý tin nhắn trạng thái video (bật/tắt)
     *
     * @param consultationId ID của cuộc tư vấn
     * @param message Nội dung tin nhắn trạng thái video
     * @return Tin nhắn được chuyển tiếp tới các clients
     */
    @MessageMapping("/consultation/{consultationId}/video")
    @SendTo("/topic/consultation/{consultationId}/video")
    public Object handleVideoStatusMessage(@DestinationVariable Long consultationId,
                                           @Payload Object message) {
        logger.debug("Received video status message for consultation {}: {}", consultationId, message);
        return message;
    }

    /**
     * Xử lý tin nhắn trạng thái audio (bật/tắt)
     *
     * @param consultationId ID của cuộc tư vấn
     * @param message Nội dung tin nhắn trạng thái audio
     * @return Tin nhắn được chuyển tiếp tới các clients
     */
    @MessageMapping("/consultation/{consultationId}/audio")
    @SendTo("/topic/consultation/{consultationId}/audio")
    public Object handleAudioStatusMessage(@DestinationVariable Long consultationId,
                                           @Payload Object message) {
        logger.debug("Received audio status message for consultation {}: {}", consultationId, message);
        return message;
    }

    /**
     * Xử lý tin nhắn chat
     *
     * @param consultationId ID của cuộc tư vấn
     * @param message Nội dung tin nhắn chat
     * @return Tin nhắn được chuyển tiếp tới các clients
     */
    @MessageMapping("/consultation/{consultationId}/chat")
    @SendTo("/topic/consultation/{consultationId}/chat")
    public Object handleChatMessage(@DestinationVariable Long consultationId,
                                    @Payload Object message) {
        logger.debug("Received chat message for consultation {}", consultationId);
        return message;
    }

    /**
     * Xử lý thông báo typing (đang nhập)
     *
     * @param consultationId ID của cuộc tư vấn
     * @param message Nội dung thông báo typing
     * @return Thông báo được chuyển tiếp tới các clients
     */
    @MessageMapping("/consultation/{consultationId}/typing")
    @SendTo("/topic/consultation/{consultationId}/typing")
    public Object handleTypingMessage(@DestinationVariable Long consultationId,
                                      @Payload Object message) {
        return message;
    }

    /**
     * Xử lý tin nhắn chia sẻ màn hình
     *
     * @param consultationId ID của cuộc tư vấn
     * @param message Nội dung tin nhắn chia sẻ màn hình
     * @return Tin nhắn được chuyển tiếp tới các clients
     */
    @MessageMapping("/consultation/{consultationId}/screen")
    @SendTo("/topic/consultation/{consultationId}/screen")
    public Object handleScreenShareMessage(@DestinationVariable Long consultationId,
                                           @Payload Object message) {
        logger.debug("Received screen sharing status for consultation {}: {}", consultationId, message);
        return message;
    }
}
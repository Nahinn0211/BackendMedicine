package hunre.edu.vn.backend.mapper;

import hunre.edu.vn.backend.dto.ChatMessageDTO;
import hunre.edu.vn.backend.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChatMessageMapper {
    @Mapping(target = "consultationId", source = "consultation.id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ChatMessageDTO.GetChatMessageDTO toGetChatMessageDTO(ChatMessage entity);

    ChatMessage toEntity(ChatMessageDTO.SaveChatMessageDTO dto);
}
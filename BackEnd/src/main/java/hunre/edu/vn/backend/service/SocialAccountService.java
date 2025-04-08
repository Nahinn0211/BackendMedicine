package hunre.edu.vn.backend.service;

import hunre.edu.vn.backend.dto.SocialAccountDTO;

import java.util.List;
import java.util.Optional;

public interface SocialAccountService {
    List<SocialAccountDTO.GetSocialAccountDTO> findAll();
    Optional<SocialAccountDTO.GetSocialAccountDTO> findById(Long id);
    SocialAccountDTO.GetSocialAccountDTO saveOrUpdate(SocialAccountDTO.SaveSocialAccountDTO socialAccountDTO);
    String deleteByList(List<Long> ids);
    List<SocialAccountDTO.GetSocialAccountDTO> findByUserId(Long userId);
    Optional<SocialAccountDTO.GetSocialAccountDTO> findByProviderEmail(String email);
}
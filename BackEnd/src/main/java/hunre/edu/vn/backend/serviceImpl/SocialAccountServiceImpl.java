package hunre.edu.vn.backend.serviceImpl;

import hunre.edu.vn.backend.dto.SocialAccountDTO;
import hunre.edu.vn.backend.entity.SocialAccount;
import hunre.edu.vn.backend.entity.User;
import hunre.edu.vn.backend.mapper.SocialAccountMapper;
import hunre.edu.vn.backend.repository.SocialAccountRepository;
import hunre.edu.vn.backend.repository.UserRepository;
import hunre.edu.vn.backend.service.SocialAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SocialAccountServiceImpl implements SocialAccountService {
    private final SocialAccountMapper socialAccountMapper;
    private final SocialAccountRepository socialAccountRepository;
    private final UserRepository userRepository;

    public SocialAccountServiceImpl(
            SocialAccountRepository socialAccountRepository,
            UserRepository userRepository,
            SocialAccountMapper socialAccountMapper) {
        this.socialAccountRepository = socialAccountRepository;
        this.userRepository = userRepository;
        this.socialAccountMapper = socialAccountMapper;
    }

    @Override
    public List<SocialAccountDTO.GetSocialAccountDTO> findAll() {
        return socialAccountRepository.findAllActive()
                .stream()
                .map(socialAccountMapper::toGetSocialAccountDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SocialAccountDTO.GetSocialAccountDTO> findById(Long id) {
        return socialAccountRepository.findActiveById(id)
                .map(socialAccountMapper::toGetSocialAccountDTO);
    }

    @Override
    @Transactional
    public SocialAccountDTO.GetSocialAccountDTO saveOrUpdate(SocialAccountDTO.SaveSocialAccountDTO socialAccountDTO) {
        SocialAccount socialAccount;

        if (socialAccountDTO.getId() == null || socialAccountDTO.getId() == 0) {
            // INSERT case
            socialAccount = new SocialAccount();
            socialAccount.setCreatedAt(LocalDateTime.now());
            socialAccount.setUpdatedAt(LocalDateTime.now());
        } else {
            // UPDATE case
            Optional<SocialAccount> existingAccount = socialAccountRepository.findById(socialAccountDTO.getId());
            if (existingAccount.isEmpty()) {
                throw new RuntimeException("Social account not found with ID: " + socialAccountDTO.getId());
            }
            socialAccount = existingAccount.get();
            socialAccount.setUpdatedAt(LocalDateTime.now());
        }

        // Xử lý user relationship
        User user = userRepository.findActiveById(socialAccountDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + socialAccountDTO.getUserId()));
        socialAccount.setUser(user);

        // Cập nhật các trường khác
        socialAccount.setProvider(socialAccountDTO.getProvider());
        socialAccount.setProviderId(socialAccountDTO.getProviderId());
        socialAccount.setProviderEmail(socialAccountDTO.getProviderEmail());
        socialAccount.setName(socialAccountDTO.getName());
        socialAccount.setAvatarUrl(socialAccountDTO.getAvatarUrl());

        SocialAccount savedAccount = socialAccountRepository.save(socialAccount);
        return socialAccountMapper.toGetSocialAccountDTO(savedAccount);
    }

    @Override
    public String deleteByList(List<Long> ids) {
        for (Long id : ids) {
            if (socialAccountRepository.existsById(id)) {
                socialAccountRepository.softDelete(id);
            }
        }

        return "Đã xóa " + ids.size() + " tài khoản xã hội liên kết";
    }

    @Override
    public List<SocialAccountDTO.GetSocialAccountDTO> findByUserId(Long userId) {
        return socialAccountRepository.findByUserId(userId)
                .stream()
                .map(socialAccountMapper::toGetSocialAccountDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SocialAccountDTO.GetSocialAccountDTO> findByProviderEmail(String email) {
        return socialAccountRepository.findByProviderEmail(email)
                .map(socialAccountMapper::toGetSocialAccountDTO);
    }
}
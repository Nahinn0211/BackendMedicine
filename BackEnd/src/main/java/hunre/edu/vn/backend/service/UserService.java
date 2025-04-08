package hunre.edu.vn.backend.service;

import hunre.edu.vn.backend.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDTO.GetUserDTO> findAll();
    Optional<UserDTO.GetUserDTO> findById(Long id);
    UserDTO.GetUserDTO saveOrUpdate(UserDTO.SaveUserDTO userDTO);
    String deleteByList(List<Long> ids);
    Optional<UserDTO.GetUserDTO> findByEmail(String email);
    List<UserDTO.GetUserDTO> findByFullNameContaining(String fullName);
    List<UserDTO.GetUserDTO> findByPhone(String phone);
    List<UserDTO.GetUserDTO> findByAddressContaining(String address);
    List<UserDTO.GetUserDTO> findByEnabled(Boolean enabled);
    List<UserDTO.GetUserDTO> findByLocked(Boolean locked);
    String uploadAvatar(MultipartFile file) throws IOException;
    void deleteAvatar(String avatar);
    Optional<UserDTO.GetUserDTO> changePassword(String oldPassword, String newPassword, Long id);

    UserDTO.GetUserDTO register(UserDTO.SaveUserDTO userDTO);

    String login(String email, String password);

    /**
     * Đăng nhập bằng tài khoản Google
     * @param googleAccessToken Mã truy cập từ Google
     * @return Mã JWT
     */
    String loginWithGoogle(String googleAccessToken);

    /**
     * Đăng nhập bằng tài khoản Facebook
     * @param facebookAccessToken Mã truy cập từ Facebook
     * @return Mã JWT
     */
    String loginWithFacebook(String facebookAccessToken);

    default UserDetails loadUserByUsername(String username) {
        throw new UnsupportedOperationException("Method should be implemented by CustomUserDetailsService");
    }
    boolean isUserInRole(Long userId, String roleName);

    Long getDoctorProfileIdByUserId(Long userId);
    Long getPatientProfileIdByUserId(Long userId);
    String generatePasswordResetToken(String email);
    boolean validatePasswordResetToken(String token);
    boolean resetPassword(String token, String newPassword);
}
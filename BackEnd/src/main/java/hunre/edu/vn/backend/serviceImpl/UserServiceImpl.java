package hunre.edu.vn.backend.serviceImpl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import hunre.edu.vn.backend.dto.UserDTO;
import hunre.edu.vn.backend.entity.*;
import hunre.edu.vn.backend.exception.ResourceNotFoundException;
import hunre.edu.vn.backend.exception.ServiceException;
import hunre.edu.vn.backend.mapper.UserMapper;
import hunre.edu.vn.backend.repository.*;
import hunre.edu.vn.backend.service.EmailService;
import hunre.edu.vn.backend.service.UserService;
import hunre.edu.vn.backend.utils.JwtTokenProvider;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final AuthenticationManager authenticationManager;
    @Value("${google.client-id}")
    private String googleClientId;

    @Value("${facebook.app-id}")
    private String facebookAppId;
    @Value("${facebook.app-secret}")
    private String facebookAppSecret;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PatientProfileRepository patientProfileRepository;
    @Autowired
    private DoctorProfileRepository doctorProfileRepository;
    @Autowired
    private EmailService emailService;

    public UserServiceImpl(UserMapper userMapper, UserRepository userRepository, PasswordEncoder passwordEncoder, S3Service s3Service, AuthenticationManager authenticationManager) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.s3Service = s3Service;
        this.authenticationManager = authenticationManager;
    }
    @Override
    public boolean isUserInRole(Long userId, String roleName) {
        return userRepository.findById(userId)
                .map(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName().equals(roleName)))
                .orElse(false);
    }
    @Override
    public UserDTO.GetUserDTO register(UserDTO.SaveUserDTO userDTO) {
        // Kiểm tra email đã tồn tại
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại: " + userDTO.getEmail());
        }

        // Mã hóa mật khẩu
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        // Đặt giá trị mặc định
        userDTO.setEnabled(true);
        userDTO.setLocked(false);
        userDTO.setCreatedAt(LocalDateTime.now());
        userDTO.setUpdatedAt(LocalDateTime.now());

        // Lưu người dùng
        User savedUser = userRepository.save(userDtoToEntity(userDTO));
        Optional<Role> roleOptional = roleRepository.findById(3L);
        Role role = roleOptional.orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò"));

        UserRole userRole = new UserRole();
        userRole.setUser(savedUser);
        userRole.setRole(role);
        userRoleRepository.save(userRole);

        PatientProfile patientProfile = new PatientProfile();
        patientProfile.setUser(savedUser);
        patientProfileRepository.save(patientProfile);

        // Lưu vai trò người dùng
        userRoleRepository.save(userRole);

        return entityToGetUserDTO(savedUser);
    }

    @Override
    public String login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenProvider.generateToken(authentication);
    }

    @Override
    public List<UserDTO.GetUserDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toGetUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO.GetUserDTO> findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toGetUserDTO);
    }

    @Override
    @Transactional
    public UserDTO.GetUserDTO saveOrUpdate(UserDTO.SaveUserDTO userDTO) {
        if (userDTO.getId() == null && userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + userDTO.getEmail());
        }

        User user;

        if (userDTO.getId() == null) {
            user = new User();
            user.setCreatedAt(LocalDateTime.now());

            if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Password is required for new users");
            }
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        } else {
            user = userRepository.findById(userDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userDTO.getId()));

            if (!user.getEmail().equals(userDTO.getEmail()) &&
                    userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already exists: " + userDTO.getEmail());
            }

            if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }
        }

        user.setUpdatedAt(LocalDateTime.now());
        user.setFullName(userDTO.getFullName());
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());
        user.setEmail(userDTO.getEmail());
        user.setEnabled(userDTO.getEnabled() != null ? userDTO.getEnabled() : true);
        user.setLocked(userDTO.getLocked() != null ? userDTO.getLocked() : false);
        user.setCountLock(userDTO.getCountLock() != null ? userDTO.getCountLock() : 0);

        if (userDTO.getAvatar() != null && !userDTO.getAvatar().trim().isEmpty()) {
            user.setAvatar(userDTO.getAvatar());
        } else if (user.getAvatar() == null) {
            user.setAvatar("default-avatar.png");
        }

        User savedUser = userRepository.save(user);
        return userMapper.toGetUserDTO(savedUser);
    }

    @Override
    public String deleteByList(List<Long> ids) {
        for (Long id : ids) {
            if (userRepository.existsById(id)) {
                userRepository.softDelete(id);
            }
        }

        return "Đã xóa " + ids.size() + " tài khoản";
    }

    @Override
    public Optional<UserDTO.GetUserDTO> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toGetUserDTO);
    }

    @Override
    public List<UserDTO.GetUserDTO> findByFullNameContaining(String fullName) {
        return userRepository.findByFullNameContaining(fullName)
                .stream()
                .map(userMapper::toGetUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO.GetUserDTO> findByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .stream()
                .map(userMapper::toGetUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String uploadAvatar(MultipartFile file) throws IOException {
        return s3Service.uploadFile(file);
    }

    @Override
    public void deleteAvatar(String avatar) {
        s3Service.deleteFile(avatar);
    }

    @Override
    public List<UserDTO.GetUserDTO> findByAddressContaining(String address) {
        return userRepository.findByAddressContaining(address)
                .stream()
                .map(userMapper::toGetUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO.GetUserDTO> findByEnabled(Boolean enabled) {
        return userRepository.findByEnabled(enabled)
                .stream()
                .map(userMapper::toGetUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO.GetUserDTO> findByLocked(Boolean locked) {
        return userRepository.findByLocked(locked)
                .stream()
                .map(userMapper::toGetUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String loginWithGoogle(String googleAccessToken) {
        try {
            // Verify Google Access Token
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(googleAccessToken);
            if (idToken == null) {
                throw new IllegalArgumentException("Mã truy cập Google không hợp lệ");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String fullName = (String) payload.get("name");
            String avatarUrl = (String) payload.get("picture");

            // Find or create user
            Optional<User> existingUser = userRepository.findByEmail(email);
            User user;

            if (existingUser.isPresent()) {
                user = existingUser.get();
            } else {
                // Create new user for Google login
                user = new User();
                user.setEmail(email);
                user.setFullName(fullName);
                user.setEnabled(true);
                user.setLocked(false);
                user.setAvatar(avatarUrl);
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());

                // Generate a random password for social login users
                String randomPassword = passwordEncoder.encode(
                        "GOOGLE_" + System.currentTimeMillis()
                );
                user.setPassword(randomPassword);

                user = userRepository.save(user);
            }

            // Create authentication token
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.emptyList()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            return jwtTokenProvider.generateToken(authentication);

        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalArgumentException("Lỗi xác thực Google: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public String loginWithFacebook(String facebookAccessToken) {
        try {
            String facebookGraphUrl = String.format(
                    "https://graph.facebook.com/me?fields=id,name,email&access_token=%s",
                    facebookAccessToken
            );
            
            String email = ""; // Retrieve from Facebook response
            String fullName = ""; // Retrieve from Facebook response

            // Find or create user
            Optional<User> existingUser = userRepository.findByEmail(email);
            User user;

            if (existingUser.isPresent()) {
                user = existingUser.get();
            } else {
                user = new User();
                user.setEmail(email);
                user.setFullName(fullName);
                user.setEnabled(true);
                user.setLocked(false);
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());

                // Generate a random password for social login users
                String randomPassword = passwordEncoder.encode(
                        "FACEBOOK_" + System.currentTimeMillis()
                );
                user.setPassword(randomPassword);

                user = userRepository.save(user);
            }

            // Create authentication token
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.emptyList()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            return jwtTokenProvider.generateToken(authentication);

        } catch (Exception e) {
            throw new IllegalArgumentException("Lỗi xác thực Facebook: " + e.getMessage());
        }
    }

    @Override
    public Optional<UserDTO.GetUserDTO> changePassword(String oldPassword, String newPassword, Long id) {
        User user = userRepository.findById(id).get();
        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        userRepository.save(user);
        return Optional.of(userMapper.toGetUserDTO(user));
    }

    private User userDtoToEntity(UserDTO.SaveUserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setFullName(userDTO.getFullName());
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());
        user.setEnabled(userDTO.getEnabled());
        user.setLocked(userDTO.getLocked());
        user.setAvatar(userDTO.getAvatar());
        user.setCreatedAt(userDTO.getCreatedAt());
        user.setUpdatedAt(userDTO.getUpdatedAt());
        return user;
    }

    private UserDTO.GetUserDTO entityToGetUserDTO(User user) {
        UserDTO.GetUserDTO userDto = new UserDTO.GetUserDTO();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFullName(user.getFullName());
        userDto.setPhone(user.getPhone());
        userDto.setAddress(user.getAddress());
        userDto.setEnabled(user.isEnabled());
        userDto.setLocked(user.isLocked());
        userDto.setAvatar(user.getAvatar());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setUpdatedAt(user.getUpdatedAt());
        return userDto;
    }

    @Override
    public Long getDoctorProfileIdByUserId(Long userId) {
        return doctorProfileRepository.findByUser_Id(userId)
                .map(DoctorProfile::getId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ bác sĩ cho người dùng này"));
    }

    @Override
    public Long getPatientProfileIdByUserId(Long userId) {
        return patientProfileRepository.findByUserId(userId)
                .map(PatientProfile::getId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ bệnh nhân cho người dùng này"));
    }

    @Override
    public String generatePasswordResetToken(String email) {
        // Tìm người dùng theo email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với email: " + email));

        // Tạo token reset mật khẩu
        String resetToken = UUID.randomUUID().toString();

        // Lưu token và thời gian hết hạn (ví dụ: 1 giờ)
        user.setResetPasswordToken(resetToken);
        user.setResetPasswordExpiry(LocalDateTime.now().plusHours(1));

        userRepository.save(user);

        // Gửi email reset mật khẩu
        try {
            emailService.sendPasswordResetEmail(user, resetToken);
        } catch (MessagingException e) {
            throw new ServiceException("Không thể gửi email đặt lại mật khẩu", e);
        }

        return resetToken;
    }

    @Override
    public boolean validatePasswordResetToken(String token) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token không hợp lệ"));

        // Kiểm tra token đã hết hạn chưa
        return user.getResetPasswordExpiry() != null &&
                user.getResetPasswordExpiry().isAfter(LocalDateTime.now());
    }

    @Override
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token không hợp lệ"));

        // Kiểm tra token còn hiệu lực
        if (user.getResetPasswordExpiry() == null ||
                user.getResetPasswordExpiry().isBefore(LocalDateTime.now())) {
            throw new ServiceException("Token đã hết hạn");
        }

        // Mã hóa mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));

        // Xóa token sau khi reset
        user.setResetPasswordToken(null);
        user.setResetPasswordExpiry(null);

        userRepository.save(user);
        return true;
    }
}
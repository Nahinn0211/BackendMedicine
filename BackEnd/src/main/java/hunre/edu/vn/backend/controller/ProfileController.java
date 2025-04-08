package hunre.edu.vn.backend.controller;

import hunre.edu.vn.backend.dto.DoctorProfileDTO;
import hunre.edu.vn.backend.dto.PatientProfileDTO;
import hunre.edu.vn.backend.dto.UserDTO;
import hunre.edu.vn.backend.entity.DoctorProfile;
import hunre.edu.vn.backend.entity.PatientProfile;
import hunre.edu.vn.backend.exception.ResourceNotFoundException;
import hunre.edu.vn.backend.repository.DoctorProfileRepository;
import hunre.edu.vn.backend.repository.PatientProfileRepository;
import hunre.edu.vn.backend.repository.UserRepository;
import hunre.edu.vn.backend.service.DoctorProfileService;
import hunre.edu.vn.backend.service.PatientProfileService;
import hunre.edu.vn.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "User Profile", description = "API để quản lý thông tin profile của người dùng đang đăng nhập")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DoctorProfileRepository doctorProfileRepository;

    @Autowired
    private PatientProfileRepository patientProfileRepository;

    @Autowired
    private DoctorProfileService doctorProfileService;

    @Autowired
    private PatientProfileService patientProfileService;

    @Operation(summary = "Lấy thông tin người dùng hiện tại", description = "Trả về thông tin chi tiết của người dùng đang đăng nhập cùng với role và profile tương ứng")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy thông tin người dùng thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thông tin người dùng")
    })

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Chưa đăng nhập"));
        }

        // Lấy email từ Authentication
        String email = authentication.getName();

        Optional<UserDTO.GetUserDTO> userOptional = userService.findByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Không tìm thấy thông tin người dùng"));
        }

        UserDTO.GetUserDTO user = userOptional.get();

        // Lấy danh sách các vai trò của người dùng
        List<String> roleNames = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Tạo đối tượng response chi tiết
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("phone", user.getPhone());
        response.put("address", user.getAddress());
        response.put("fullName", user.getFullName());
        response.put("roles", roleNames);
        response.put("avatar", user.getAvatar());

        // Kiểm tra role và thêm thông tin tương ứng
        boolean isDoctor = roleNames.contains("DOCTOR");
        boolean isPatient = roleNames.contains("PATIENT");

        if (isDoctor) {
            // Lấy thông tin bác sĩ
            Optional<DoctorProfile> doctorProfileOptional = doctorProfileRepository.findByUser_Id(user.getId());

            if (doctorProfileOptional.isPresent()) {
                DoctorProfile doctorProfile = doctorProfileOptional.get();
                DoctorProfileDTO.GetDoctorProfileDTO doctorProfileDto =
                        doctorProfileService.findById(doctorProfile.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin bác sĩ"));

                response.put("doctorProfile", doctorProfileDto);
                response.put("doctorProfileId", doctorProfile.getId());
                response.put("currentRole", "DOCTOR");
            }
        }

        if (isPatient) {
            Optional<PatientProfile> patientProfileOptional = patientProfileRepository.findByUserId(user.getId());

            if (patientProfileOptional.isPresent()) {
                PatientProfile patientProfile = patientProfileOptional.get();
                PatientProfileDTO.GetPatientProfileDTO patientProfileDto =
                        patientProfileService.findById(patientProfile.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin bệnh nhân"));

                response.put("patientProfile", patientProfileDto);
                response.put("patientProfileId", patientProfile.getId());

                if (!isDoctor) {
                    response.put("currentRole", "PATIENT");
                }
            }
        }

        if (isDoctor && isPatient) {
            response.put("hasMultipleRoles", true);
        }

        return ResponseEntity.ok(response);
    }


}
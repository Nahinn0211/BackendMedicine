package hunre.edu.vn.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hunre.edu.vn.backend.dto.UserDTO;
import hunre.edu.vn.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "API to manage users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get all users", description = "Returns a list of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users")
    })
    @GetMapping
    public ResponseEntity<List<UserDTO.GetUserDTO>> getAllUsers() {
        List<UserDTO.GetUserDTO> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get user by ID", description = "Returns a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO.GetUserDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Save or update user (Alternative endpoint)", description = "Creates a new user or updates an existing one")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User saved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping(value= "/save", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDTO.GetUserDTO> saveOrUpdateUserAlternative(
            @RequestPart("user") String userJson,
            @RequestPart(value = "file", required = false) MultipartFile file){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserDTO.SaveUserDTO userDto = objectMapper.readValue(userJson, UserDTO.SaveUserDTO.class);

            if (userDto.getId() != null && userDto.getId() > 0) {
                Optional<UserDTO.GetUserDTO> existingUserOpt = userService.findById(userDto.getId());
                if (existingUserOpt.isPresent()) {
                    if (file != null && !file.isEmpty()) {
                        userService.deleteAvatar(existingUserOpt.get().getAvatar());
                        String newImageUrl = userService.uploadAvatar(file);
                        userDto.setAvatar(newImageUrl);
                    } else {
                        userDto.setAvatar(existingUserOpt.get().getAvatar());
                    }
                    userDto.setUpdatedAt(LocalDateTime.now());
                } else {
                    return ResponseEntity.notFound().build();
                }
            }
            else {
                if (file != null && !file.isEmpty()) {
                    String newImageUrl = userService.uploadAvatar(file);
                    userDto.setAvatar(newImageUrl);
                } else {
                    return ResponseEntity.badRequest().body(null);
                }
                userDto.setCreatedAt(LocalDateTime.now());
                userDto.setUpdatedAt(LocalDateTime.now());
            }

            UserDTO.GetUserDTO savedUser = userService.saveOrUpdate(userDto);
            return ResponseEntity.ok(savedUser);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Delete user", description = "Deletes a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public String deleteUser(@RequestBody List<Long> ids) {
        return userService.deleteByList(ids);
    }

    @Operation(summary = "Get user by email", description = "Returns a user by email address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/by-email")
    public ResponseEntity<UserDTO.GetUserDTO> getUserByEmail(@RequestParam String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Search users", description = "Search users by fullName, phone, or address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    })
    @GetMapping("/search")
    public ResponseEntity<List<UserDTO.GetUserDTO>> searchUsers(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address) {
        if (fullName != null) {
            return ResponseEntity.ok(userService.findByFullNameContaining(fullName));
        } else if (phone != null) {
            return ResponseEntity.ok(userService.findByPhone(phone));
        } else if (address != null) {
            return ResponseEntity.ok(userService.findByAddressContaining(address));
        }
        return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Get users by enabled status", description = "Returns users filtered by enabled status")
    @GetMapping("/by-enabled")
    public ResponseEntity<List<UserDTO.GetUserDTO>> getUsersByEnabled(@RequestParam Boolean enabled) {
        return ResponseEntity.ok(userService.findByEnabled(enabled));
    }

    @PutMapping("/change-password/{id}")
    public ResponseEntity<Optional<UserDTO.GetUserDTO>> changePassword(
            @PathVariable Long id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

        Optional<UserDTO.GetUserDTO> updatedUser = userService.changePassword(oldPassword, newPassword, id);

        if (!updatedUser.isPresent()) {
            return ResponseEntity.status(400).body(updatedUser);
        }

        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Get users by locked status", description = "Returns users filtered by locked status")
    @GetMapping("/by-locked")
    public ResponseEntity<List<UserDTO.GetUserDTO>> getUsersByLocked(@RequestParam Boolean locked) {
        return ResponseEntity.ok(userService.findByLocked(locked));
    }
}
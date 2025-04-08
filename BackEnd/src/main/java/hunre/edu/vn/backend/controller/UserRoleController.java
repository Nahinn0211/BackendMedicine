package hunre.edu.vn.backend.controller;

import hunre.edu.vn.backend.dto.UserRoleDTO;
import hunre.edu.vn.backend.service.UserRoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-roles")
public class UserRoleController {

    private final UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @GetMapping
    public ResponseEntity<List<UserRoleDTO.GetUserRoleDTO>> getAllUserRoles() {
        List<UserRoleDTO.GetUserRoleDTO> userRoles = userRoleService.findAll();
        return ResponseEntity.ok(userRoles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserRoleDTO.GetUserRoleDTO> getUserRoleById(@PathVariable Long id) {
        return userRoleService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
    public ResponseEntity<UserRoleDTO.GetUserRoleDTO> saveOrUpdateUserRole(@RequestBody UserRoleDTO.SaveUserRoleDTO userRoleDTO) {
        UserRoleDTO.GetUserRoleDTO savedUserRole = userRoleService.saveOrUpdate(userRoleDTO);
        return ResponseEntity.ok(savedUserRole);
    }

    @DeleteMapping("/{id}")
    public String deleteUserRole(@RequestBody List<Long> ids) {
        return userRoleService.deleteByList(ids);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<UserRoleDTO.GetUserRoleDTO>> getUserRolesByUserId(@PathVariable Long userId) {
        List<UserRoleDTO.GetUserRoleDTO> userRoles = userRoleService.findByUserId(userId);
        return ResponseEntity.ok(userRoles);
    }

    @GetMapping("/by-role/{roleId}")
    public ResponseEntity<List<UserRoleDTO.GetUserRoleDTO>> getUserRolesByRoleId(@PathVariable Long roleId) {
        List<UserRoleDTO.GetUserRoleDTO> userRoles = userRoleService.findByRoleId(roleId);
        return ResponseEntity.ok(userRoles);
    }

}
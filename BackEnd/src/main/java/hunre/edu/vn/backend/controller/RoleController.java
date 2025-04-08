package hunre.edu.vn.backend.controller;

import hunre.edu.vn.backend.dto.RoleDTO;
import hunre.edu.vn.backend.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<List<RoleDTO.GetRoleDTO>> getAllRoles() {
        List<RoleDTO.GetRoleDTO> roles = roleService.findAll();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO.GetRoleDTO> getRoleById(@PathVariable Long id) {
        return roleService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
    public ResponseEntity<RoleDTO.GetRoleDTO> saveOrUpdateRole(@RequestBody RoleDTO.SaveRoleDTO roleDTO) {
        RoleDTO.GetRoleDTO savedRole = roleService.saveOrUpdate(roleDTO);
        return ResponseEntity.ok(savedRole);
    }

    @DeleteMapping("/{id}")
    public String deleteRole(@RequestBody List<Long> ids) {
        return roleService.deleteByList(ids);
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<RoleDTO.GetRoleDTO> getRoleByName(@PathVariable String name) {
        return roleService.findByName(name)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
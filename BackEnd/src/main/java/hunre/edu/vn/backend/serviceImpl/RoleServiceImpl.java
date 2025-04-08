package hunre.edu.vn.backend.serviceImpl;

import hunre.edu.vn.backend.dto.RoleDTO;
import hunre.edu.vn.backend.entity.Role;
import hunre.edu.vn.backend.mapper.RoleMapper;
import hunre.edu.vn.backend.repository.RoleRepository;
import hunre.edu.vn.backend.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    public List<RoleDTO.GetRoleDTO> findAll() {
        return roleRepository.findAllActive()
                .stream()
                .map(roleMapper::toGetRoleDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<RoleDTO.GetRoleDTO> findById(Long id) {
        return roleRepository.findActiveById(id)
                .map(roleMapper::toGetRoleDTO);
    }

    @Override
    @Transactional
    public RoleDTO.GetRoleDTO saveOrUpdate(RoleDTO.SaveRoleDTO roleDTO) {
        Role role;

        if (roleDTO.getId() == null || roleDTO.getId() == 0) {
            // INSERT case
            role = new Role();
            role.setCreatedAt(LocalDateTime.now());
            role.setUpdatedAt(LocalDateTime.now());
        } else {
            // UPDATE case
            Optional<Role> existingRole = roleRepository.findActiveById(roleDTO.getId());
            if (existingRole.isEmpty()) {
                throw new RuntimeException("Role not found with ID: " + roleDTO.getId());
            }
            role = existingRole.get();
            role.setUpdatedAt(LocalDateTime.now());
        }

        // Cập nhật trường name
        role.setName(roleDTO.getName());

        Role savedRole = roleRepository.save(role);
        return roleMapper.toGetRoleDTO(savedRole);
    }

    @Override
    public String deleteByList(List<Long> ids) {
        for (Long id : ids) {
            if (roleRepository.existsById(id)) {
                roleRepository.softDelete(id);
            }
        }

        return "Đã xóa " + ids.size() + " quyền";
    }

    @Override
    public Optional<RoleDTO.GetRoleDTO> findByName(String name) {
        return roleRepository.findByNameAndIsDeletedFalse(name)
                .map(roleMapper::toGetRoleDTO);
    }
}
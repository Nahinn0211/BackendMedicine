package hunre.edu.vn.backend.serviceImpl;

import hunre.edu.vn.backend.dto.UserRoleDTO;
import hunre.edu.vn.backend.entity.Role;
import hunre.edu.vn.backend.entity.User;
import hunre.edu.vn.backend.entity.UserRole;
import hunre.edu.vn.backend.mapper.UserRoleMapper;
import hunre.edu.vn.backend.repository.RoleRepository;
import hunre.edu.vn.backend.repository.UserRepository;
import hunre.edu.vn.backend.repository.UserRoleRepository;
import hunre.edu.vn.backend.service.UserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserRoleServiceImpl implements UserRoleService {
    private final UserRoleMapper userRoleMapper;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserRoleServiceImpl(
            UserRoleRepository userRoleRepository,
            UserRoleMapper userRoleMapper,
            UserRepository userRepository,
            RoleRepository roleRepository) {
        this.userRoleRepository = userRoleRepository;
        this.userRoleMapper = userRoleMapper;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public List<UserRoleDTO.GetUserRoleDTO> findAll() {
        return userRoleRepository.findAllActive()
                .stream()
                .map(userRoleMapper::toGetUserRoleDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserRoleDTO.GetUserRoleDTO> findById(Long id) {
        return userRoleRepository.findActiveById(id)
                .map(userRoleMapper::toGetUserRoleDTO);
    }

    @Override
    @Transactional
    public UserRoleDTO.GetUserRoleDTO saveOrUpdate(UserRoleDTO.SaveUserRoleDTO userRoleDTO) {
        UserRole userRole;

        if (userRoleDTO.getId() == null || userRoleDTO.getId() == 0) {
            // INSERT case
            userRole = new UserRole();
            userRole.setCreatedAt(LocalDateTime.now());
            userRole.setUpdatedAt(LocalDateTime.now());
        } else {
            // UPDATE case
            Optional<UserRole> existingUserRole = userRoleRepository.findActiveById(userRoleDTO.getId());
            if (existingUserRole.isEmpty()) {
                throw new RuntimeException("UserRole not found with ID: " + userRoleDTO.getId());
            }
            userRole = existingUserRole.get();
            userRole.setUpdatedAt(LocalDateTime.now());
        }

        // Xử lý user relationship
        User user = userRepository.findActiveById(userRoleDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userRoleDTO.getUserId()));
        userRole.setUser(user);

        // Xử lý role relationship
        Role role = roleRepository.findActiveById(userRoleDTO.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + userRoleDTO.getRoleId()));
        userRole.setRole(role);

        UserRole savedUserRole = userRoleRepository.save(userRole);
        return userRoleMapper.toGetUserRoleDTO(savedUserRole);
    }

    @Override
    public String deleteByList(List<Long> ids) {
        for (Long id : ids) {
            if (userRoleRepository.existsById(id)) {
                userRoleRepository.softDelete(id);
            }
        }

        return "Đã xóa thành công " + ids.size() + " liên kết giữa tài khoản và quyền hạn";
    }

    @Override
    public List<UserRoleDTO.GetUserRoleDTO> findByUserId(Long userId) {
        return userRoleRepository.findByUserId(userId)
                .stream()
                .map(userRoleMapper::toGetUserRoleDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserRoleDTO.GetUserRoleDTO> findByRoleId(Long roleId) {
        return userRoleRepository.findByRoleId(roleId)
                .stream()
                .map(userRoleMapper::toGetUserRoleDTO)
                .collect(Collectors.toList());
    }
}
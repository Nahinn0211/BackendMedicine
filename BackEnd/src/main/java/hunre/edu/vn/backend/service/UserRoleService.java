package hunre.edu.vn.backend.service;

import hunre.edu.vn.backend.dto.UserRoleDTO;

import java.util.List;
import java.util.Optional;

public interface UserRoleService {
    List<UserRoleDTO.GetUserRoleDTO> findAll();
    Optional<UserRoleDTO.GetUserRoleDTO> findById(Long id);
    UserRoleDTO.GetUserRoleDTO saveOrUpdate(UserRoleDTO.SaveUserRoleDTO userRoleDTO);
    String deleteByList(List<Long> ids);
    List<UserRoleDTO.GetUserRoleDTO> findByUserId(Long userId);
    List<UserRoleDTO.GetUserRoleDTO> findByRoleId(Long roleId);
}
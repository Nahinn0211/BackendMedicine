package hunre.edu.vn.backend.service;

import hunre.edu.vn.backend.dto.RoleDTO;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<RoleDTO.GetRoleDTO> findAll();
    Optional<RoleDTO.GetRoleDTO> findById(Long id);
    RoleDTO.GetRoleDTO saveOrUpdate(RoleDTO.SaveRoleDTO roleDTO);
    String deleteByList(List<Long> ids);
    Optional<RoleDTO.GetRoleDTO> findByName(String name);
}
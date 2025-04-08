package hunre.edu.vn.backend.serviceImpl;


import hunre.edu.vn.backend.dto.SalaryDTO;
import hunre.edu.vn.backend.entity.Salary;
import hunre.edu.vn.backend.entity.User;
import hunre.edu.vn.backend.mapper.SalaryMapper;
import hunre.edu.vn.backend.repository.SalaryRepository;
import hunre.edu.vn.backend.repository.UserRepository;
import hunre.edu.vn.backend.service.SalaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SalaryServiceImpl implements SalaryService {
    private final SalaryMapper salaryMapper;
    private final SalaryRepository salaryRepository;
    private final UserRepository userRepository;

    public SalaryServiceImpl(
            SalaryRepository salaryRepository,
            UserRepository userRepository,
            SalaryMapper salaryMapper) {
        this.salaryRepository = salaryRepository;
        this.userRepository = userRepository;
        this.salaryMapper = salaryMapper;
    }

    @Override
    public List<SalaryDTO.GetSalaryDTO> findAll() {
        return salaryRepository.findAllActive()
                .stream()
                .map(salaryMapper::toGetSalaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SalaryDTO.GetSalaryDTO> findById(Long id) {
        return salaryRepository.findActiveById(id)
                .map(salaryMapper::toGetSalaryDTO);
    }

    @Override
    @Transactional
    public SalaryDTO.GetSalaryDTO saveOrUpdate(SalaryDTO.SaveSalaryDTO salaryDTO) {
        Salary salary;

        if (salaryDTO.getId() == null || salaryDTO.getId() == 0) {
            // INSERT case
            salary = new Salary();
            salary.setCreatedAt(LocalDateTime.now());
            salary.setUpdatedAt(LocalDateTime.now());
        } else {
            // UPDATE case
            Optional<Salary> existingSalary = salaryRepository.findActiveById(salaryDTO.getId());
            if (existingSalary.isEmpty()) {
                throw new RuntimeException("Salary not found with ID: " + salaryDTO.getId());
            }
            salary = existingSalary.get();
            salary.setUpdatedAt(LocalDateTime.now());
        }

        // Xử lý user relationship
        if (salaryDTO.getUserId() != null) {
            User user = userRepository.findActiveById(salaryDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + salaryDTO.getUserId()));
            salary.setUser(user);
        }

        // Cập nhật các trường khác
        salary.setBankCode(salaryDTO.getBankCode());
        salary.setBankName(salaryDTO.getBankName());
        salary.setPrice(salaryDTO.getPrice());
        salary.setStatus(salaryDTO.getStatus());

        Salary savedSalary = salaryRepository.save(salary);
        return salaryMapper.toGetSalaryDTO(savedSalary);
    }

    @Override
    public String deleteByList(List<Long> ids) {
        for (Long id : ids) {
            if (salaryRepository.existsById(id))
                salaryRepository.softDelete(id);
        }

        return "Đã xóa " + ids.size() + " yêu cầu rút tiền";
    }

    @Override
    public List<SalaryDTO.GetSalaryDTO> findByUserId(Long userId) {
        return salaryRepository.findByUserId(userId)
                .stream()
                .map(salaryMapper::toGetSalaryDTO)
                .collect(Collectors.toList());
    }
}
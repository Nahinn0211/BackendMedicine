package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.Attribute;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttributeRepository extends BaseRepository<Attribute> {
    /**
     * Tìm tất cả thuộc tính theo medicineId, không bao gồm các thuộc tính đã bị xóa mềm
     * @param medicineId ID của thuốc cần tìm thuộc tính
     * @return Danh sách các thuộc tính
     */
    @Query("SELECT a FROM Attribute a WHERE a.medicine.id = :medicineId AND a.isDeleted = false")
    List<Attribute> findByMedicineId(@Param("medicineId") Long medicineId);

    /**
     * Đếm số lượng thuộc tính của một thuốc
     * @param medicineId ID của thuốc
     * @return Số lượng thuộc tính
     */
    @Query("SELECT COUNT(a) FROM Attribute a WHERE a.medicine.id = :medicineId AND a.isDeleted = false")
    long countByMedicineId(@Param("medicineId") Long medicineId);

    /**
     * Xóa mềm tất cả thuộc tính của một thuốc
     * @param medicineId ID của thuốc
     */
    @Query("UPDATE Attribute a SET a.isDeleted = true WHERE a.medicine.id = :medicineId")
    void softDeleteByMedicineId(@Param("medicineId") Long medicineId);
}
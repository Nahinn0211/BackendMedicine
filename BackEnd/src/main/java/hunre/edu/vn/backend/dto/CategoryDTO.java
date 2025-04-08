package hunre.edu.vn.backend.dto;

import hunre.edu.vn.backend.entity.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {
    private String name;
    private String image;
    private List<CategoryDTO> children;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetCategoryDTO {
        private Long id;
        private Long parentId;
        private String name;
        private String image;
        private Integer childrenCount;
        private Boolean isParent;
        private Boolean isLeaf;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private GetCategoryDTO parent;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveCategoryDTO {
        private Long id;

        @NotBlank(message = "Tên danh mục không được trống")
        private String name;

        private String image;
        private Long parentId;
        private Boolean isParent;
        private Boolean isLeaf;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    public static GetCategoryDTO fromEntity(Category category) {
        if (category == null) return null;

        GetCategoryDTO dto = GetCategoryDTO.builder()
                .id(category.getId())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .name(category.getName())
                .image(category.getImage())
                .isParent(category.isParent())
                .isLeaf(category.isLeaf())
                .build();

        if (category.getParent() != null && !category.getParent().getIsDeleted()) {
            dto.setParent(GetCategoryDTO.builder()
                    .id(category.getParent().getId())
                    .name(category.getParent().getName())
                    .image(category.getParent().getImage())
                    .isParent(true)
                    .isLeaf(false)
                    .build());
        }

        return dto;
    }
}
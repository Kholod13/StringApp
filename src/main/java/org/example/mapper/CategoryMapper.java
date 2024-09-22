package org.example.mapper;

import org.example.dto.category.CategoryCreateDTO;
import org.example.dto.category.CategoryItemDTO;
import org.example.model.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "image", ignore = true)
    CategoryEntity categoryEntityByCategoryCreateDTO(CategoryCreateDTO category);

    CategoryItemDTO toDto(CategoryEntity category);
    List<CategoryItemDTO> toDto(List<CategoryEntity> category);
}
package com.jewelry.product.repository;

import com.jewelry.product.entity.Category;
import com.jewelry.product.entity.enums.CategoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
    List<Category> findByStatus(CategoryStatus status);
}

package com.doormate.doormat_catalog.repository;

import com.doormate.doormat_catalog.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Latest products first
    List<Product> findAllByOrderByCreatedAtDesc();

    // Filter products by material, size, or keyword
    @Query("SELECT p FROM Product p WHERE " +
           "(:material IS NULL OR LOWER(p.material) = LOWER(:material)) AND " +
           "(:size IS NULL OR LOWER(p.size) LIKE LOWER(CONCAT('%', :size, '%'))) AND " +
           "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY p.createdAt DESC")
    List<Product> searchProducts(@Param("material") String material,
                                 @Param("size") String size,
                                 @Param("keyword") String keyword);
}

package com.doormate.doormat_catalog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Doormat title

    @Column(length = 1000)
    private String description; // Product details

    @Column(nullable = false)
    private String material; // Coir, Rubber, Cotton, Microfiber

    @Column(nullable = false)
    private String size; // e.g., 16x24 inches

    @Column(nullable = false)
    private String availableColors; // e.g., Brown, Red, Black

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // Price in INR

    @Column(nullable = false)
    private boolean inStock = true; // Stock availability

    @Column(nullable = false)
    private String imageUrl; // Cloudinary image URL

    @Column(nullable = false)
    private String imagePublicId; // Cloudinary ID for deletion

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

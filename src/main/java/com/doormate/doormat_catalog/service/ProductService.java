package com.doormate.doormat_catalog.service;

import com.doormate.doormat_catalog.dto.ProductRequest;
import com.doormate.doormat_catalog.entity.Product;
import com.doormate.doormat_catalog.exception.ResourceNotFoundException;
import com.doormate.doormat_catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    // Get all products (Public)
    public List<Product> getAllProducts(String material, String size, String keyword) {
        if ((material == null || material.isBlank()) &&
            (size == null || size.isBlank()) &&
            (keyword == null || keyword.isBlank())) {
            return productRepository.findAllByOrderByCreatedAtDesc();
        }
        return productRepository.searchProducts(
                material != null && !material.isBlank() ? material : null,
                size != null && !size.isBlank() ? size : null,
                keyword != null && !keyword.isBlank() ? keyword : null
        );
    }

    // Get single product by ID (Public)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    // Add new doormat (Admin)
    public Product createProduct(ProductRequest request, MultipartFile image) throws IOException {
        Map uploadResult = fileStorageService.uploadImage(image);

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .material(request.getMaterial())
                .size(request.getSize())
                .availableColors(request.getAvailableColors())
                .price(request.getPrice())
                .inStock(request.isInStock())
                .imageUrl(uploadResult.get("secure_url").toString())
                .imagePublicId(uploadResult.get("public_id").toString())
                .build();

        return productRepository.save(product);
    }

    // Update existing doormat (Admin)
    public Product updateProduct(Long id, ProductRequest request, MultipartFile image) throws IOException {
        Product existing = getProductById(id);

        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setMaterial(request.getMaterial());
        existing.setSize(request.getSize());
        existing.setAvailableColors(request.getAvailableColors());
        existing.setPrice(request.getPrice());
        existing.setInStock(request.isInStock());

        // If new photo uploaded, replace old photo on Cloudinary
        if (image != null && !image.isEmpty()) {
            fileStorageService.deleteImage(existing.getImagePublicId());
            Map uploadResult = fileStorageService.uploadImage(image);
            existing.setImageUrl(uploadResult.get("secure_url").toString());
            existing.setImagePublicId(uploadResult.get("public_id").toString());
        }

        return productRepository.save(existing);
    }

    // Delete doormat (Admin)
    public void deleteProduct(Long id) {
        Product existing = getProductById(id);
        fileStorageService.deleteImage(existing.getImagePublicId());
        productRepository.delete(existing);
    }
}

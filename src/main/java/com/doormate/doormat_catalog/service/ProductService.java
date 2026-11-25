package com.doormate.doormat_catalog.service;

import com.doormate.doormat_catalog.dto.ProductRequest;
import com.doormate.doormat_catalog.entity.Product;
import com.doormate.doormat_catalog.exception.ResourceNotFoundException;
import com.doormate.doormat_catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

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

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    public Product createProduct(ProductRequest request, List<MultipartFile> images) throws IOException {
        List<MultipartFile> validFiles = images.stream().filter(f -> f != null && !f.isEmpty()).toList();
        if (validFiles.isEmpty()) {
            throw new IllegalArgumentException("At least one product photo is required");
        }

        List<String> urls = new ArrayList<>();
        List<String> publicIds = new ArrayList<>();

        for (MultipartFile file : validFiles) {
            Map uploadResult = fileStorageService.uploadImage(file);
            urls.add(uploadResult.get("secure_url").toString());
            publicIds.add(uploadResult.get("public_id").toString());
        }

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .material(request.getMaterial())
                .size(request.getSize())
                .availableColors(request.getAvailableColors())
                .price(request.getPrice())
                .inStock(request.isInStock())
                .imageUrl(urls.get(0))
                .imagePublicId(publicIds.get(0))
                .extraImageUrls(urls.size() > 1 ? String.join(",", urls.subList(1, urls.size())) : null)
                .extraImagePublicIds(publicIds.size() > 1 ? String.join(",", publicIds.subList(1, publicIds.size())) : null)
                .build();

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductRequest request, List<MultipartFile> images) throws IOException {
        Product existing = getProductById(id);

        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setMaterial(request.getMaterial());
        existing.setSize(request.getSize());
        existing.setAvailableColors(request.getAvailableColors());
        existing.setPrice(request.getPrice());
        existing.setInStock(request.isInStock());

        List<MultipartFile> validFiles = (images == null) ? List.of() :
                images.stream().filter(f -> f != null && !f.isEmpty()).toList();

        if (!validFiles.isEmpty()) {
            deleteAllCloudinaryImages(existing);

            List<String> urls = new ArrayList<>();
            List<String> publicIds = new ArrayList<>();
            for (MultipartFile file : validFiles) {
                Map uploadResult = fileStorageService.uploadImage(file);
                urls.add(uploadResult.get("secure_url").toString());
                publicIds.add(uploadResult.get("public_id").toString());
            }

            existing.setImageUrl(urls.get(0));
            existing.setImagePublicId(publicIds.get(0));
            existing.setExtraImageUrls(urls.size() > 1 ? String.join(",", urls.subList(1, urls.size())) : null);
            existing.setExtraImagePublicIds(publicIds.size() > 1 ? String.join(",", publicIds.subList(1, publicIds.size())) : null);
        }

        return productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        Product existing = getProductById(id);
        deleteAllCloudinaryImages(existing);
        productRepository.delete(existing);
    }

    private void deleteAllCloudinaryImages(Product product) {
        if (product.getImagePublicId() != null && !product.getImagePublicId().isBlank()) {
            fileStorageService.deleteImage(product.getImagePublicId());
        }
        if (product.getExtraImagePublicIds() != null && !product.getExtraImagePublicIds().isBlank()) {
            for (String pid : product.getExtraImagePublicIds().split(",")) {
                if (!pid.isBlank()) {
                    fileStorageService.deleteImage(pid.trim());
                }
            }
        }
    }
}
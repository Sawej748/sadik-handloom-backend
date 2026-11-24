package com.doormate.doormat_catalog.controller;

import com.doormate.doormat_catalog.entity.Product;
import com.doormate.doormat_catalog.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class PublicProductController {

    private final ProductService productService;

    // Fetch all doormats with optional filters
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(
            @RequestParam(required = false) String material,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(productService.getAllProducts(material, size, keyword));
    }

    // Fetch single doormat details
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
}

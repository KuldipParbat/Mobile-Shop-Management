package com.mobileshop.service.impl;

import com.mobileshop.dto.ProductDTO;
import com.mobileshop.entity.Product;
import com.mobileshop.repository.ProductRepository;
import com.mobileshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductDTO createProduct(ProductDTO dto) {

        Product product = mapToEntity(dto);

        product = productRepository.save(product);

        return mapToDTO(product);
    }

    @Override
    public List<ProductDTO> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return mapToDTO(product);
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO dto) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(dto.getName());
        product.setBrand(dto.getBrand());
        product.setModel(dto.getModel());
        product.setCategory(dto.getCategory());
        product.setPurchasePrice(dto.getPurchasePrice());
        product.setSellingPrice(dto.getSellingPrice());
        product.setStock(dto.getStock());
        product.setTrackingType(dto.getTrackingType());

        product = productRepository.save(product);

        return mapToDTO(product);
    }

    @Override
    public void deleteProduct(Long id) {

        productRepository.deleteById(id);
    }

    private Product mapToEntity(ProductDTO dto) {

        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .brand(dto.getBrand())
                .model(dto.getModel())
                .category(dto.getCategory())
                .purchasePrice(dto.getPurchasePrice())
                .sellingPrice(dto.getSellingPrice())
                .stock(dto.getStock())
                .trackingType(dto.getTrackingType())
                .build();
    }

    private ProductDTO mapToDTO(Product product) {

        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .model(product.getModel())
                .category(product.getCategory())
                .purchasePrice(product.getPurchasePrice())
                .sellingPrice(product.getSellingPrice())
                .stock(product.getStock())
                .trackingType(product.getTrackingType())
                .build();
    }
}
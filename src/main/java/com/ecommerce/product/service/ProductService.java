package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.exception.ProductNotFoundException;
import com.ecommerce.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository=productRepository;
    }
    //Get all products
    public Page<ProductResponse> getAllProducts(Pageable pageable){
        return productRepository.findAll(pageable)
                .map(this::mapToResponse);
    }
    //Get Product by ID
    public ProductResponse getProductById(Long id){
        return mapToResponse(findProductById(id));
    }

    private Product findProductById(Long id){
        return productRepository.findById(id).orElseThrow(() ->
                new ProductNotFoundException("Product Not Found with Id: " + id));
    }

//    public Product createProduct(Product product){
//        return productRepository.save(product);
//    }
    public ProductResponse createProduct(ProductRequest request){
        Product product = mapToEntity(request);
        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    public ProductResponse updateProduct(Long id, ProductRequest updateRequest){
        Product existingProduct = findProductById(id);

        existingProduct.setName(updateRequest.getName());
        existingProduct.setDescription(updateRequest.getDescription());
        existingProduct.setPrice(updateRequest.getPrice());
        existingProduct.setQuantity(updateRequest.getQuantity());

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToResponse(updatedProduct);
    }

    public ProductResponse deleteProduct(Long id){
        Product product = findProductById(id);
        ProductResponse response = mapToResponse(product);
        productRepository.delete(product);
        return response;
    }

    private Product mapToEntity(ProductRequest request){
        Product product = new Product();

        product.setName(request.getName());
        product.setQuantity(request.getQuantity());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        return product;
    }

    private ProductResponse mapToResponse(Product product){
        ProductResponse response = new ProductResponse();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setQuantity(product.getQuantity());

        return response;
    }
    public Page<ProductResponse> searchProduct(String name, BigDecimal minPrice, BigDecimal maxPrice,Integer minQuantity,
    Integer maxQuantity,Pageable pageable){
        Specification<Product> spec = ((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());
        if(name != null && !name.isBlank()){
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),"%"+name.toLowerCase()+"%"));
        }
        if(minPrice != null){
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("price"),minPrice));
        }
        if(maxPrice != null){
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("price"),maxPrice));
        }
        if(minQuantity != null){
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("quantity"),minQuantity));
        }
        if(maxQuantity != null){
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("quantity"),maxQuantity));
        }
        return productRepository.findAll(spec,pageable).map(this::mapToResponse);
    }
}

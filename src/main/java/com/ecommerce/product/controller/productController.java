package com.ecommerce.product.controller;


import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class productController {

    private final ProductService productService;


    public productController(ProductService productService){
        this.productService = productService;
    }
    //get all products
    @GetMapping
    public Page<ProductResponse> getAllProduct(@PageableDefault(page = 0,size = 10,sort = "id",direction = Sort.Direction.ASC)Pageable pageable){
        return productService.getAllProducts(pageable);
    }
    //Get product by Id
    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id){
        return productService.getProductById(id);
    }
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request){
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Update Product
    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable Long id,@Valid @RequestBody ProductRequest request){
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    public ProductResponse deleteProduct(@PathVariable Long id){
        return productService.deleteProduct(id);
    }

    @GetMapping("/search")
    public Page<ProductResponse> searchProduct(@RequestParam(required = false)String name,
                                               @RequestParam(required = false)BigDecimal minPrice,
                                               @RequestParam(required = false)BigDecimal maxPrice,
                                               @RequestParam(required = false)Integer minQuantity,
                                               @RequestParam(required = false)Integer maxQuantity,
                                               @PageableDefault(page = 0,size = 10,sort = "id",direction = Sort.Direction.ASC)Pageable pageable){
        return productService.searchProduct(name,minPrice,maxPrice,minQuantity,maxQuantity,pageable);
    }
}

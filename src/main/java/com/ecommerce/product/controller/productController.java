package com.ecommerce.product.controller;


import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@Tag(name = "Product Management",description = "API's for creating, retriving, updating, deleting and searching products")
@RestController
@RequestMapping("/api/products")
public class productController {

    private final ProductService productService;


    public productController(ProductService productService){
        this.productService = productService;
    }
    //get all products
    @Operation(summary = "Get all products",description = "Retrive products with pagination and sorting")
    @GetMapping
    public Page<ProductResponse> getAllProduct(@PageableDefault(page = 0,size = 10,sort = "id",direction = Sort.Direction.ASC)Pageable pageable){
        return productService.getAllProducts(pageable);
    }
    //Get product by Id
    @Operation(summary = "Get Product by id", description = "retrive a product using unique id")
    @ApiResponse(responseCode = "200",description = "Product Found")
    @ApiResponse(responseCode = "404",description = "Product not found")
    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id){
        return productService.getProductById(id);
    }

    @Operation(summary = "Create Product",description = "Create a new product with supplied details")
    @ApiResponse(responseCode = "201",description = "Product Created successfully")
    @ApiResponse(responseCode = "400",description = "Invaid product data")
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request){
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Update Product
    @Operation(summary = "Update Product",description = "Update a existing product using it's ID")
    @ApiResponse(responseCode = "200",description = "Product updated successfully")
    @ApiResponse(responseCode = "404",description = "Product not found")
    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable Long id,@Valid @RequestBody ProductRequest request){
        return productService.updateProduct(id, request);
    }

    @Operation(summary = "Delete Product",description = "Delete a product using it's ID")
    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @DeleteMapping("/{id}")
    public ProductResponse deleteProduct(@PathVariable Long id){
        return productService.deleteProduct(id);
    }

    @Operation(summary = "Search Product", description = "Search Product using filter such as name, price, quantity with pagination and sorting")
    @ApiResponse(responseCode = "200", description = "Product retrived successfully")
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

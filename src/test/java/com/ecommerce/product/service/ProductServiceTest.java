package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.exception.ProductNotFoundException;
import com.ecommerce.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentMatchers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void getProductById_ShouldReturnProduct_whereProductExist(){
        Product product = new Product();
        product.setId(1L);
        product.setName("Iphone 15");
        product.setDescription("Apple");
        product.setPrice(new BigDecimal("65000"));
        product.setQuantity(10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        ProductResponse response = productService.getProductById(1L);

        assertNotNull(response);
        assertEquals(1L,response.getId());
        assertEquals("Iphone 15",response.getName());
        assertEquals(new BigDecimal("65000"),response.getPrice());
        verify(productRepository).findById(1L);
    }
    @Test
    void getProductById_ShouldThrowException_WhenProductDoesNotExist(){
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class,()->productService.getProductById(999L));
        verify(productRepository).findById(999L);
    }
    @Test
    void createProduct_ShouldReturnCreatedProduct() {

        ProductRequest request = new ProductRequest();
        request.setName("Samsung S25");
        request.setDescription("Samsung");
        request.setPrice(new BigDecimal("45000"));
        request.setQuantity(10);

        Product savedProduct = new Product();
        savedProduct.setId(2L);
        savedProduct.setName(request.getName());
        savedProduct.setDescription(request.getDescription());
        savedProduct.setPrice(request.getPrice());
        savedProduct.setQuantity(request.getQuantity());

        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        ProductResponse response = productService.createProduct(request);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals("Samsung S25", response.getName());
        assertEquals(new BigDecimal("45000"), response.getPrice());

        verify(productRepository).save(any(Product.class));
    }
    @Test
    void updateProduct_ShouldReturnUpdatedProduct_WhenProductExists() {

        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("iPhone 15");
        existingProduct.setDescription("Apple");
        existingProduct.setPrice(new BigDecimal("65000"));
        existingProduct.setQuantity(10);

        ProductRequest request = new ProductRequest();
        request.setName("iPhone 17");
        request.setDescription("Apple Latest");
        request.setPrice(new BigDecimal("75000"));
        request.setQuantity(15);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(existingProduct));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = productService.updateProduct(1L, request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("iPhone 17", response.getName());
        assertEquals(new BigDecimal("75000"), response.getPrice());
        assertEquals(15, response.getQuantity());

        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }
    @Test
    void deleteProduct_ShouldDeleteProduct_WhenProductExists() {

        Product product = new Product();
        product.setId(1L);
        product.setName("iPhone 17");

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository).findById(1L);
        verify(productRepository).delete(product);
    }

    @Test
    void searchProduct_ShouldReturnMatchingProducts() {
        Product product = new Product();
        product.setId(1L);
        product.setName("iPhone 17");
        product.setDescription("Apple smartphone");
        product.setPrice(new BigDecimal("75000"));
        product.setQuantity(12);

        Pageable pageable = PageRequest.of(0, 5);
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 5);
        when(productRepository.findAll(ArgumentMatchers.<Specification<Product>>any(), eq(pageable)))
                .thenReturn(productPage);

        Page<ProductResponse> response = productService.searchProduct(
                "iphone", new BigDecimal("50000"), new BigDecimal("100000"), 5, 20, pageable);

        assertNotNull(response);
        assertEquals("iPhone 17", response.getContent().get(0).getName());
        assertEquals("Apple smartphone", response.getContent().get(0).getDescription());
        assertEquals(new BigDecimal("75000"), response.getContent().get(0).getPrice());
        assertEquals(12, response.getContent().get(0).getQuantity());
        assertEquals(0, response.getNumber());
        assertEquals(5, response.getSize());
        assertEquals(5, response.getTotalElements());
        assertEquals(1, response.getTotalPages());

        verify(productRepository).findAll(
                any(Specification.class),
                eq(pageable)
        );
    }
}

package com.example.ShopApp.Services;

import com.example.ShopApp.DTO.*;
import com.example.ShopApp.Exception.DataNotFoundException;
import com.example.ShopApp.Models.*;
import com.example.ShopApp.Responses.ProductResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface InterfaceProductService {
    Product createProduct(ProductDTO productDTO) throws DataNotFoundException;
    Product getProductById(long productId) throws Exception;
    Page<ProductResponses> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest);
    Product updateProduct(long productId, ProductDTO productDTO) throws Exception;
    void deleteProduct(long id);
    boolean existsByName(String name);
    ProductImage creatProductImage(Long productId, ProductImageDTO productImageDTO) throws Exception;
    List<Product> findProductsByIds(List<Long> productIds);
}

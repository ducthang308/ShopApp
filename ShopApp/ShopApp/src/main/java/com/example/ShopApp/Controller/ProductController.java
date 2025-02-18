package com.example.ShopApp.Controller;

import ch.qos.logback.core.util.StringUtil;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.ShopApp.Components.LocalizationUtils;
import com.example.ShopApp.DTO.CategoryDTO;
import com.example.ShopApp.DTO.ProductDTO;
import com.example.ShopApp.DTO.ProductImageDTO;
import com.example.ShopApp.Models.Product;
import com.example.ShopApp.Models.ProductImage;
import com.example.ShopApp.Responses.ProductListResponses;
import com.example.ShopApp.Responses.ProductResponses;
import com.example.ShopApp.Services.InterfaceProductService;
import com.example.ShopApp.Services.ProductService;
import com.example.ShopApp.Utils.MessageKeys;
import com.github.javafaker.Faker;
import jakarta.validation.Path;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final Cloudinary cloudinary;
    private final ProductService productService;
    private final LocalizationUtils localizationUtils;

    @GetMapping("") //http://localhost:8088/api/v1/products?page=1&limit=10
    public ResponseEntity<ProductListResponses> getAllProduct(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "category_id") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        //Tạo pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending());

        Page<ProductResponses> productPage = productService.getAllProducts(keyword, categoryId, pageRequest);
        //Lấy tổng số trang
        int totalPage = productPage.getTotalPages();
        List<ProductResponses> productResponses = productPage.getContent();
        return ResponseEntity.ok(ProductListResponses.builder()
                .products(productResponses)
                .totalPage(totalPage)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long productId) {
        try {
            Product existingProduct = productService.getProductById(productId);
            return ResponseEntity.ok(ProductResponses.fromProduct(existingProduct));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO,
                                            //@ModelAttribute("files") List<MultipartFile> files,
                                            BindingResult result
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessage);
            }
            Product newProduct = productService.createProduct(productDTO);
            return ResponseEntity.ok(newProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName){
        try{
            java.nio.file.Path imagePath = Paths.get("uploads/"+imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if(resource.exists()){
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            }else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpg").toUri()));
            }
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "uploads/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ROLE_Admin')")
    public ResponseEntity<?> uploadImages(@RequestParam("files") List<MultipartFile> files,
                                          @PathVariable("id") Long productId) {
        log.info("Product ID: {}", productId);
        log.info("Number of files received: {}", files != null ? files.size() : 0);
        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest().body("No files provided for upload.");
        }
        try {
            Product existingProduct = productService.getProductById(productId);
            List<ProductImage> productImages = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }
                Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                String imageUrl = uploadResult.get("url").toString();

                ProductImage productImage = productService.creatProductImage(
                        existingProduct.getId(),
                        ProductImageDTO.builder().imageUrl(imageUrl).build()
                );
                productImages.add(productImage);
            }
            return ResponseEntity.ok(productImages);
        } catch (IOException e) {
            log.error("File processing error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file format.");
        } catch (Exception e) {
            log.error("Unexpected error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred.");
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable long id){
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(String.format("Product with ID = %d deleted successfully", id));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    //@PostMapping("/generateFakeProducts")
    public ResponseEntity<String> generateFakeProducts(){
        Faker faker = new Faker();
        for(int i=0; i < 1000000; i++){
            String productName = faker.commerce().productName();
            if(productService.existsByName(productName)){
                continue;
            }
            ProductDTO productDTO = ProductDTO.builder()
                    .name(productName)
                    .price((float)faker.number().numberBetween(10, 9000000))
                    .description(faker.lorem().sentence())
                    .thumbnail("")
                    .categoryId((long)faker.number().numberBetween(3,5))
                    .build();
            try {
                productService.createProduct(productDTO);
            }catch (Exception e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Fake product create successfully");
    }

    @GetMapping("/by-ids")
    public ResponseEntity<?> findProductsByIds(@RequestParam("ids") String ids){
        //Ex: 1,3,5,7
        try {
            //Tách chuỗi ids thành một mảng các số nguyên
            List<Long> productIds = Arrays.stream(ids.split(",")) //Chuyển dãy trên thành mảng
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<Product> products = productService.findProductsByIds(productIds);
            return ResponseEntity.ok(products);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

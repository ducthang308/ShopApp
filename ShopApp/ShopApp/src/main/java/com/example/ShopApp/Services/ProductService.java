package com.example.ShopApp.Services;

import com.example.ShopApp.DTO.ProductDTO;
import com.example.ShopApp.DTO.ProductImageDTO;
import com.example.ShopApp.Exception.DataNotFoundException;
import com.example.ShopApp.Exception.InvalidParamException;
import com.example.ShopApp.Models.Category;
import com.example.ShopApp.Models.Product;
import com.example.ShopApp.Models.ProductImage;
import com.example.ShopApp.Repositories.CategoryRepository;
import com.example.ShopApp.Repositories.ProductImageRepository;
import com.example.ShopApp.Repositories.ProductRepository;
import com.example.ShopApp.Responses.ProductResponses;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements InterfaceProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(()->
                        new DataNotFoundException("Cannot find category with id: "+productDTO.getCategoryId()));
        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .category(existingCategory)
                .build();
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(long productId) throws Exception {
        Optional<Product> optionalProduct = productRepository.getDetailProduct(productId);
        if(optionalProduct.isPresent()){
            return optionalProduct.get();
        }
        throw new DataNotFoundException("Cannot find product with ID: "+productId);
    }

    @Override
    public Page<ProductResponses> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest) {
        //Lấy danh sách sản phẩm theo page, giới hạn(limit) và categoryId (nếu có)
        Page<Product> productPage;
        productPage = productRepository.searchProducts(categoryId, keyword, pageRequest);
        return productPage.map(ProductResponses::fromProduct);
    }

    @Override
    @Transactional
    public Product updateProduct(long productId, ProductDTO productDTO) throws Exception {
        Product existingProduct = getProductById(productId);
        if(existingProduct != null){
            //Copy các thuộc tính từ DTO -> Product
            //Có thể sử dụng ModelMapper(Xem có những thuộc tính nào thì copy y chang nhau)
            Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(()-> new DataNotFoundException("Cannot find category with id: "+productDTO.getCategoryId()));
            existingProduct.setName(productDTO.getName());
            existingProduct.setCategory(existingCategory);
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setThumbnail(productDTO.getThumbnail());
            return productRepository.save(existingProduct);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteProduct(long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        optionalProduct.ifPresent(productRepository::delete);

    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    public ProductImage creatProductImage(Long productId, ProductImageDTO productImageDTO) throws Exception{
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(()->
                        new DataNotFoundException("Cannot find product with id: "+productImageDTO.getProductId()));
        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();
        //Không cho insert quá 5 ảnh cho 1 sản phẩm
        int size = productImageRepository.findByProductId(productId).size();
        if(size >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT){
            throw new InvalidParamException("Numbers of images must be <= "+ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }
        return productImageRepository.save(newProductImage);
    }

    @Override
    public List<Product> findProductsByIds(List<Long> productIds) {
        return productRepository.findProductsByIds(productIds);
    }
}

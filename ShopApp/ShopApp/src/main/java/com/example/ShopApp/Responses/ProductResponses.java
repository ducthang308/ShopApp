package com.example.ShopApp.Responses;

import com.example.ShopApp.Models.BaseEntity;
import com.example.ShopApp.Models.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponses extends BaseResponses {
    @JsonProperty("name_product")
    private String name;
    @JsonProperty("price")
    private Float price;
    private String thumbnail;
    @JsonProperty("descriptions")
    private String description;
    @JsonProperty("category_id")
    private Long categoryId;

    public static ProductResponses fromProduct(Product product){
        ProductResponses productResponse = ProductResponses.builder()
                .name(product.getName())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .description(product.getDescription())
                .categoryId(product.getCategory().getId())
                .build();
        productResponse.setCreatedAt(product.getCreatedAt());
        productResponse.setUpdatedAt(product.getUpdatedAt());
        return productResponse;
    }
}

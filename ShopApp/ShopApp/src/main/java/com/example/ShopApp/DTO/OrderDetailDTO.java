package com.example.ShopApp.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

@Data //toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailDTO {
    @JsonProperty("order_id")
    @Min(value = 1, message = "Order ID must be > 0")
    private Long orderId;

    @JsonProperty("product_id")
    @Min(value = 1, message = "Product ID must be > 0")
    private Long productId;

    @JsonProperty("price")
    @Min(value = 0, message = "Price must be >= 0")
    private float price;

    @JsonProperty("number_of_products")
    @Min(value = 1, message = "Number Of Products must be >= 1")
    private int numberOfProducts;

    @JsonProperty("total_money")
    @Min(value = 0, message = "Total Money must be >= 0")
    private float totalMoney;

    @JsonProperty("color")
    private String color;

}

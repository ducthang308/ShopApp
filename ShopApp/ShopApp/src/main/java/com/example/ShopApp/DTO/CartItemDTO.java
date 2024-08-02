package com.example.ShopApp.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data //toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDTO {
    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("product_id")
    private Long productId;
}

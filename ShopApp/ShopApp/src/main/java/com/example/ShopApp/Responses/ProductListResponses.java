package com.example.ShopApp.Responses;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductListResponses {
    private List<ProductResponses> products;
    private int totalPage;
}

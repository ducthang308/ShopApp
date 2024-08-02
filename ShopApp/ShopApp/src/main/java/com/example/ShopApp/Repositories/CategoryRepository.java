package com.example.ShopApp.Repositories;

import com.example.ShopApp.Models.Category;
import com.example.ShopApp.Models.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
//    List<OrderDetail> findByOrderId(Long orderId);
}

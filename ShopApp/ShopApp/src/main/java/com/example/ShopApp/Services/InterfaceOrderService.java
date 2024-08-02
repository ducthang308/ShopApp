package com.example.ShopApp.Services;

import com.example.ShopApp.DTO.*;
import com.example.ShopApp.Exception.DataNotFoundException;
import com.example.ShopApp.Models.Category;
import com.example.ShopApp.Models.Order;
import com.example.ShopApp.Responses.*;

import java.util.List;

public interface InterfaceOrderService {
    Order createOrder(OrderDTO orderDTO) throws Exception;
    Order getOrder(Long id);
    List<Order> findByUserId(Long userId);
    Order updateOrder(long id, OrderDTO orderDTO) throws DataNotFoundException;
    void deleteOrder(long id);
}

package com.example.ShopApp.Services;

import com.example.ShopApp.DTO.OrderDTO;
import com.example.ShopApp.DTO.OrderDetailDTO;
import com.example.ShopApp.Exception.DataNotFoundException;
import com.example.ShopApp.Models.Order;
import com.example.ShopApp.Models.OrderDetail;

import java.util.List;

public interface InterfaceOrderDetailService {
    OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws Exception;
    OrderDetail getOrderDetail(Long id) throws DataNotFoundException;
    OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) throws DataNotFoundException;
    void deleteById(Long id);
    List<OrderDetail> findByOrderId(Long orderId);
}

package com.example.ShopApp.Controller;

import com.example.ShopApp.Components.LocalizationUtils;
import com.example.ShopApp.DTO.OrderDTO;
import com.example.ShopApp.Exception.DataNotFoundException;
import com.example.ShopApp.Models.Order;
import com.example.ShopApp.Models.OrderDetail;
import com.example.ShopApp.Responses.OrderDetailResponses;
import com.example.ShopApp.Services.InterfaceOrderDetailService;
import com.example.ShopApp.Services.OrderDetailService;
import com.example.ShopApp.Utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import com.example.ShopApp.DTO.OrderDetailDTO;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order_details")
@RequiredArgsConstructor
public class OrderDetailController {
    private final OrderDetailService orderDetailService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    public ResponseEntity<?> createOderDetail(@Valid @RequestBody OrderDetailDTO orderdetailDTO) {
        try {
            OrderDetail newOrderDetail = orderDetailService.createOrderDetail(orderdetailDTO);
            return ResponseEntity.ok(OrderDetailResponses.fromOrderDetail(newOrderDetail));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(@Valid @PathVariable("id") Long id) throws DataNotFoundException {
        try {
            OrderDetail orderDetail = orderDetailService.getOrderDetail(id);
            return ResponseEntity.ok(OrderDetailResponses.fromOrderDetail(orderDetail));
            //return ResponseEntity.ok(orderDetail);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Lấy ra danh sách các order_details của 1 order nào đó
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails(@Valid @PathVariable("orderId") Long orderId){
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(orderId);
        List<OrderDetailResponses> orderDetailResponses = orderDetails
                .stream()
                .map(OrderDetailResponses::fromOrderDetail)
                .toList();
        return ResponseEntity.ok(orderDetailResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail(@Valid @PathVariable("id") Long id,
                                         @Valid @RequestBody OrderDetailDTO orderDetailDTO
    )throws DataNotFoundException{
        try {
            OrderDetail orderDetail = orderDetailService.updateOrderDetail(id, orderDetailDTO);
            return ResponseEntity.ok(orderDetail);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable("id") Long id){
        orderDetailService.deleteById(id);
        return ResponseEntity.ok().body(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_ORDER_DETAIL_SUCCESSFULLY));
    }
}

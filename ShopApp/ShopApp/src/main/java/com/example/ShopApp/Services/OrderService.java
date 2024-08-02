package com.example.ShopApp.Services;

import com.example.ShopApp.DTO.CartItemDTO;
import com.example.ShopApp.DTO.OrderDTO;
import com.example.ShopApp.Exception.DataNotFoundException;
import com.example.ShopApp.Models.Order;
import com.example.ShopApp.Models.OrderDetail;
import com.example.ShopApp.Models.OrderStatus;
import com.example.ShopApp.Models.User;
import com.example.ShopApp.Repositories.OrderRepository;
import com.example.ShopApp.Repositories.UserRepository;
import com.example.ShopApp.Responses.OrderResponses;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class OrderService implements InterfaceOrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public Order createOrder(OrderDTO orderDTO) throws Exception {
        //Lấy user id xem có tồn tại không
        User user = userRepository
                .findById(orderDTO.getUsersId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find user with id: "+orderDTO.getUsersId()));
        //Covert orderDTO => Order
        //Dùng thư viện Model Mapper
        //Tạo một luồng bảng ánh xạ riêng để kiểm tra việc ánh xạ
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        //Cập nhật các trường của đơn hàng từ OrderDTO
        Order order = new Order();
        modelMapper.map(orderDTO, order);
        order.setUser(user);
        order.setOrderDate(new Date()); //Lấy thời điểm hiện tại
        order.setStatus(OrderStatus.PENDING);
        LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now():orderDTO.getShippingDate();
        if(shippingDate.isBefore(LocalDate.now())){
            throw new DataNotFoundException("Data must be at least today");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        orderRepository.save(order);
        // Tạo danh sách các đối tượng OrderDetail từ CartItem
        List<OrderDetail> orderDetails = new ArrayList<>();
        for(CartItemDTO cartItemDTO : orderDTO.getCartItems()){
            //Tạo một đối tượng OrderDetail từ đối tượng CartItemDTO
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
        }
        return order;
    }

    @Override
    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public List<Order> findByUserId(Long userId) {

        return orderRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Order updateOrder(long id, OrderDTO orderDTO) throws DataNotFoundException {
        Order order = orderRepository.findById(id).orElseThrow(() ->
                new DataNotFoundException("Can find order with id: " + id));
        User existingUser= userRepository.findById(orderDTO.getUsersId()).orElseThrow(()->
                new DataNotFoundException("Can find user with id: "+id));
        //Tạo một luồng bảng ánh xạ riêng để kiểm soát việc ánh xạ
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        //Cập nhật các trường của đơn hàng orderDTO
        modelMapper.map(orderDTO,order);
        order.setUser(existingUser);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void deleteOrder(long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if(order != null){
            order.setActive(false);
            orderRepository.save(order);
        }
    }
}

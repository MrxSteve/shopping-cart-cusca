package com.prueba.order_service.service;

import com.prueba.order_service.client.ProductClient;
import com.prueba.order_service.dto.CreateOrderRequest;
import com.prueba.order_service.dto.OrderResponse;
import com.prueba.order_service.dto.UpdateOrderStatusRequest;
import com.prueba.order_service.dto.external.ProductResponse;
import com.prueba.order_service.entity.Customer;
import com.prueba.order_service.entity.Order;
import com.prueba.order_service.entity.OrderItem;
import com.prueba.order_service.entity.OrderStatus;
import com.prueba.order_service.exception.OrderNotFoundException;
import com.prueba.order_service.exception.ProductValidationException;
import com.prueba.order_service.mapper.OrderMapper;
import com.prueba.order_service.repository.CustomerRepository;
import com.prueba.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductClient productClient;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseGet(() -> {
                    Customer c = new Customer();
                    c.setFullName(request.getFullName());
                    c.setEmail(request.getEmail());
                    return customerRepository.save(c);
                });

        Order order = new Order();
        order.setCustomer(customer);
        order.setItems(new ArrayList<>());

        BigDecimal total = BigDecimal.ZERO;

        for (var itemRequest : request.getItems()) {
            ProductResponse product = productClient.getProductById(itemRequest.getProductId());
            if (product == null) {
                throw new ProductValidationException(
                        "Product not found with id: " + itemRequest.getProductId());
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(product.getId());
            item.setProductTitle(product.getTitle());
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(product.getPrice());
            order.getItems().add(item);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }

        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);
        return orderMapper.toOrderResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return orderMapper.toOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderMapper.toOrderResponseList(orderRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomer(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerIdWithDetails(customerId);
        return orderMapper.toOrderResponseList(orders);
    }

    @Transactional
    public OrderResponse updateStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        order.setStatus(request.getStatus());
        return orderMapper.toOrderResponse(orderRepository.save(order));
    }
}

package com.prueba.order_service.mapper;

import com.prueba.order_service.dto.CustomerResponse;
import com.prueba.order_service.dto.OrderItemResponse;
import com.prueba.order_service.dto.OrderResponse;
import com.prueba.order_service.entity.Customer;
import com.prueba.order_service.entity.Order;
import com.prueba.order_service.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", imports = {BigDecimal.class})
public interface OrderMapper {

    CustomerResponse toCustomerResponse(Customer customer);

    @Mapping(target = "subtotal", expression = "java(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))")
    OrderItemResponse toOrderItemResponse(OrderItem item);

    List<OrderItemResponse> toOrderItemResponseList(List<OrderItem> items);

    @Mapping(target = "customer", source = "customer")
    @Mapping(target = "items", source = "items")
    OrderResponse toOrderResponse(Order order);

    List<OrderResponse> toOrderResponseList(List<Order> orders);
}

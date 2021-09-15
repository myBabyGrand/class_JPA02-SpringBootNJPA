package jpabook.jpashop.api;


import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import jpabook.jpashop.service.query.OrderDto2;
import jpabook.jpashop.service.query.OrderQueryService;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> orderV1(){
        List<Order> all= orderRepository.findAll(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o->o.getItem().getName());
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> orderV2(){
        List<Order> orders= orderRepository.findAll(new OrderSearch());
        return orders.stream().map(o->new OrderDto(o)).collect(toList());
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        List<OrderDto> result = orderRepository.findAllWithItem().stream()
                .map(OrderDto::new)
                .collect(toList());
        return result;
    }
    @Data
    static class OrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream().map(orderItem -> new OrderItemDto(orderItem)).collect(toList());
        }
    }

    @Getter
    static class OrderItemDto{
        private String name;
        private int orderPrice;
        private int orderQuantity;

        public OrderItemDto(OrderItem orderItem){
            name = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            orderQuantity = orderItem.getOrderQuantity();
        }
    }
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> orderV3_page(
            @RequestParam (value="offset", defaultValue="0") int offset,
            @RequestParam (value="limit", defaultValue="100") int limit) {
        List<Order> orders= orderRepository.findAllWithMemberDelivery(offset, limit);
        return orders.stream().map(o->new OrderDto(o)).collect(toList());
    }

    private final OrderQueryService orderQueryService;
    @GetMapping("/api/v3.2/orders")
    public List<OrderDto2> ordersV3_usingDedicatedService(){
        return orderQueryService.ordersV3_usingDedicatedService();
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> orderV4(){
        return orderQueryRepository.findOrderQueryDtos();
    }
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> orderV5(){
        return orderQueryRepository.findAllByDto_optimization();
    }
    @GetMapping("/api/v6/orders")
    public List<OrderFlatDto> orderV6(){
        return orderQueryRepository.findAllByDto_flat();
    }
    @GetMapping("/api/v6.5/orders")
    public List<OrderQueryDto> orderV6_5(){
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();
        return flats.stream()
               .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList())))
                .entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }

}

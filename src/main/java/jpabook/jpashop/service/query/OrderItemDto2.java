package jpabook.jpashop.service.query;

import jpabook.jpashop.domain.OrderItem;
import lombok.Getter;

@Getter
public class OrderItemDto2 {
    private String name;
    private int orderPrice;
    private int orderQuantity;

    public OrderItemDto2(OrderItem orderItem){
        name = orderItem.getItem().getName();
        orderPrice = orderItem.getOrderPrice();
        orderQuantity = orderItem.getOrderQuantity();
    }
}

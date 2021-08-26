package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@Table(name = "order_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {
    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")//FK
    private Item item;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")//FK
    private Order order;

    private int orderPrice;
    private int orderQuantity;


    //생성 메소드
    public static OrderItem createOrderItem(Item item, int orderPrice, int orderQuantity){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setOrderQuantity(orderQuantity);

        item.removeStock(orderQuantity);
        return orderItem;
    }

    //비즈니스 로직
    public void cancel(){
        getItem().addStock(orderQuantity);
    }

    public int getOrderAmount() {
        return getOrderPrice()*orderQuantity;
    }
}

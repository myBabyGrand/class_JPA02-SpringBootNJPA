package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문생성
     * */
    @Transactional
    public Long order(Long memberId, Long itemId, int orderQuantity){
        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setStatus(DeliveryStatus.READY);

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(),orderQuantity );
        //OrderItem orderItem = new OrderItem(); //불가 -> @NoArgsConstructor(access = AccessLevel.PROTECTED) = protected OrderItem(){}
        //orderItem.setItem ...
        //생성을 생성 메소드를 통해서만 할 수 있도록 제한한다.

        //주문
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문저장
        orderRepository.save(order);//cascade = CascadeType.ALL 인 orderItem, delivery도 함께 퍼시스트 한다.

        return order.getId();
    }


    /**
     * 주문취소
     * */
    @Transactional
    public void cancel(Long orderId){
       // 주문 엔티티
        Order order = orderRepository.findOne(orderId);

        order.cancel();
    }



    /**
     * 조회
     * */
    @Transactional
    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAll(orderSearch);

    }
}

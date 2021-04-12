package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.Exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    static Long testOrderId;

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }
    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setStockQuantity(stockQuantity);
        book.setPrice(price);
        em.persist(book);
        return book;
    }


    @Test
    @DisplayName("상품 주문")
    void order() {
        Member member = createMember();
        Book book = createBook("시골 JPA", 10000, 10);
        int orderQuantity = 2;
        //When
        Long orderId = orderService.order(member.getId(), book.getId(), orderQuantity);


        //then
        Order getOrder = orderRepository.findOne(orderId);
        assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문시 상태는 ORDER");
        assertEquals(1, getOrder.getOrderItems().size(), "주문한 상품 종류 수가 정확해야 한다.");
        assertEquals(20000, getOrder.getTotalPrice(), "주문금액은 수량 * 단가");
        assertEquals(8, book.getStockQuantity(), "주문수량 만큼 재고는 줄어야한다");

    }

    @Test
    @DisplayName("주문취소")
    void cancel() {
        //given
        Member member = createMember();
        Book book = createBook("시골 JPA", 10000, 10);

        int orderQuantity = 3;
        Long orderId = orderService.order(member.getId(), book.getId(), orderQuantity);
        //when
        orderService.cancel(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);
        assertEquals(OrderStatus.CANCEL, getOrder.getStatus(), "주문취소의 상태는 CANCEL");
        assertEquals(10, book.getStockQuantity(), "재고는 복원되어야 한다");

    }
    @Test
    @DisplayName("재고수량 초과 주문")
    void order_NotEnoughStock(){
        Member member = createMember();
        Book book = createBook("시골 JPA", 10000, 10);
        int orderQuantity = 11;

        //When & then
        assertThrows(NotEnoughStockException.class, () -> orderService.order(member.getId(), book.getId(), orderQuantity));
    }
}
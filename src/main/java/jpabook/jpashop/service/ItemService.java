package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }
    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

    @Transactional
    public void updateBook(@NotNull Book itemParam) { //itemParam: 파리미터로 넘어온 준영속 상태의 엔티티
        Book findBook = (Book)findOne(itemParam.getId()); //같은 엔티티를 조회한다.
        findBook.setName(itemParam.getName());
        findBook.setPrice(itemParam.getPrice());
        findBook.setStockQuantity(itemParam.getStockQuantity());
        findBook.setAuthor(itemParam.getAuthor());
        findBook.setIsbn(itemParam.getIsbn());
    }

}

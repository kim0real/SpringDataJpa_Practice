package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    public void save() {
        Item item = new Item("itemA");
        /**
         * save는 새로운 엔티티는 persist하고 이미 있는 엔티티는 merge한다.
         * 아래는 itemA라는 값이 있다고 판단하여 merge를 한다.
         * 즉 select, insert가 나가므로 비효율적이다.
         * 실무에서는 persist 또는 변경감지를 통해 데이터를 변경해야 한다.
         */
        itemRepository.save(item);
    }
}
package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findByOwnerId(long userId);

    @Query("""
            SELECT i
            FROM Item AS i
            WHERE i.available = TRUE
            AND (LOWER(i.name) like CONCAT('%', LOWER(:text), '%')
            OR LOWER(i.description) like CONCAT('%', LOWER(:text) , '%'))
            """)
    Collection<Item> searchItems(String text);

    @Query("""
            SELECT CASE WHEN COUNT(i)> 0 THEN TRUE ELSE FALSE END
            FROM Item AS i
            WHERE i.id=:itemId AND i.owner.id=:userId
            """)
    boolean existsByIdAndOwner(long itemId, long userId);

    List<Item> findByRequestId(long requestId);

    List<Item> findByRequestIdIn(Collection<Long> requestIds);

}

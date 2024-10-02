package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    Optional<ItemRequest> findById(long requestId);

    List<ItemRequest> findByRequestorIdOrderByCreatedAsc(long userId);

    List<ItemRequest> findByRequestorIdNotOrderByCreatedAsc(long userId);
}

package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.item.ItemDTOForRequest;
import ru.practicum.shareit.item.mapper.ItemDTOMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDTO;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.dto.ItemRequestDTOWithAnswers;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.mapper.UserDTOMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
@RequiredArgsConstructor
public class RequestDTOMapper {
    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;

    public static ItemRequest fromCreateDTO(User user, ItemRequestCreateDTO createDTO) {
        return ItemRequest.builder()
                .description(createDTO.getDescription())
                .requestor(user)
                .build();
    }

    public static ItemRequestDTO toDTO(ItemRequest itemRequest) {
        UserDTO userDTO = UserDTOMapper.toDTO(itemRequest.getRequestor());
        return ItemRequestDTO.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }


    private static ItemRequestDTOWithAnswers toSemiFinishedDTOWithAnswers(ItemRequest itemRequest) {
        return ItemRequestDTOWithAnswers.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequestDTOWithAnswers toDTOWithAnswers(ItemRequest itemRequest, Collection<Item> items) {
        ItemRequestDTOWithAnswers request = toSemiFinishedDTOWithAnswers(itemRequest);
        if (items != null) {
            List<ItemDTOForRequest> requestedItemsDTO = items
                    .stream()
                    .map(ItemDTOMapper::toDTOForRequest)
                    .toList();
            request.setItems(requestedItemsDTO);
        }
        return request;
    }

    public static List<ItemRequestDTOWithAnswers> toDTOWithAnswers(List<ItemRequest> itemRequests,
                                                                   Map<Long, List<Item>> itemMap) {
        if (itemMap != null) {
            Map<Long, List<ItemDTOForRequest>> itemMapDto = itemMapConvert(itemMap);
            return itemRequests.stream()
                    .map(RequestDTOMapper::toSemiFinishedDTOWithAnswers)
                    .peek(requestDto -> requestDto.setItems(itemMapDto.get(requestDto.getId())))
                    .toList();
        } else {
            return itemRequests.stream()
                    .map(RequestDTOMapper::toSemiFinishedDTOWithAnswers)
                    .toList();
        }
    }

    private Map<Long, List<Item>> getItemMapByRequestId(List<ItemRequest> itemRequests) {
        List<Long> requestIds = itemRequests.stream().map(ItemRequest::getId).toList();
        List<Item> items = itemRepository.findByRequestId(requestIds);
        Map<Long, List<Item>> map = new HashMap<>();
        for (Item item : items) {
            long requestId = item.getRequest().getId();
            if (!map.containsKey(requestId)) {
                map.put(requestId, new ArrayList<>());
            }
            map.get(requestId).add(item);
        }
        return map;
    }

    private static Map<Long, List<ItemDTOForRequest>> itemMapConvert(Map<Long, List<Item>> itemMap) {
        Map<Long, List<ItemDTOForRequest>> itemMapDto = new HashMap<>();
        for (Map.Entry<Long, List<Item>> entry : itemMap.entrySet()) {
            List<ItemDTOForRequest> itemDTOForRequests = entry.getValue()
                    .stream()
                    .map(ItemDTOMapper::toDTOForRequest)
                    .toList();
            itemMapDto.put(entry.getKey(), itemDTOForRequests);
        }
        return itemMapDto;
    }
}

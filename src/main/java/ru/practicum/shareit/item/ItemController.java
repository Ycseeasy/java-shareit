package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive long userId,
                              @RequestBody @Valid ItemDto itemDto) {
        log.info("""
                Создание инструмента
                Название {}
                Описание {}
                Доступность для бронирования {}
                ID владельца {}
                """, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), userId);
        Item item = ItemDtoMapper.fromDTO(userId, itemDto);
        Item createdItem = itemService.createItem(item);
        return ItemDtoMapper.toDTO(createdItem);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive long userId,
                              @PathVariable @Positive long itemId,
                              @RequestBody @Valid ItemDto itemDto) {
        log.info("""
                Обновление данных о инструменте
                ID инструмента {}
                Название {}
                Описание {}
                Доступность для бронирования {}
                ID владельца {}
                """, itemId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), userId);
        Item item = ItemDtoMapper.fromDTO(userId, itemDto);
        Item updatedItem = itemService.updateItem(itemId, item);
        return ItemDtoMapper.toDTO(updatedItem);
    }


    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItem(@PathVariable @NonNull Long itemId) {
        log.info("""
                Поиск инструмента с ID {}
                """, itemId);
        Item searchResult = itemService.getItem(itemId);
        return ItemDtoMapper.toDTO(searchResult);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDto> getAllOwnerItems(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive long userId) {
        log.info("""
                Отображение списка инструментов пользователя с ID {}
                """, userId);
        Collection<Item> ownerItems = itemService.getAllOwnerItems(userId);
        return ownerItems.stream()
                .map(ItemDtoMapper::toDTO)
                .toList();
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDto> searchItems(@RequestParam("text") String text) {
        log.info("""
                Поиск инструментов по запросу "{}"
                """, text);
        Collection<Item> itemsByQuery = itemService.searchByQuery(text);
        return itemsByQuery.stream()
                .map(ItemDtoMapper::toDTO)
                .toList();
    }
}
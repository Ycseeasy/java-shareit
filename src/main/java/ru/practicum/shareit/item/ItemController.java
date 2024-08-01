package ru.practicum.shareit.item;

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
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long userId,
                              @RequestBody ItemDto itemDto) {
        log.info("""
                Создание инструмента
                Название {}
                Описание {}
                Доступность для бронирования {}
                ID владельца {}
                """, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), userId);
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long userId,
                              @PathVariable @Positive long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("""
                Обновление данных о инструменте
                ID инструмента {}
                Название {}
                Описание {}
                Доступность для бронирования {}
                ID владельца {}
                """, itemId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), userId);
        return itemService.updateItem(itemId, itemDto, userId);
    }


    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItem(@PathVariable @NonNull Long itemId) {
        log.info("""
                Поиск инструмента с ID {}
                """, itemId);
        return itemService.getItem(itemId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDto> getAllOwnerItems(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long userId) {
        log.info("""
                Отображение списка инструментов пользователя с ID {}
                """, userId);
        return itemService.getAllOwnerItems(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDto> searchItems(@RequestParam("text") String text) {
        log.info("""
                Поиск инструментов по запросу "{}"
                """, text);
        return itemService.searchByQuery(text);
    }
}
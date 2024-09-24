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
import ru.practicum.shareit.item.dto.CommentDtoInput;
import ru.practicum.shareit.item.dto.CommentDtoOutput;
import ru.practicum.shareit.item.dto.ItemDtoInput;
import ru.practicum.shareit.item.dto.ItemDtoOutput;
import ru.practicum.shareit.item.dto.ItemDtoOutputBooking;
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
    public ItemDtoOutput createItem(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long userId,
                                    @RequestBody ItemDtoInput itemDtoInput) {
        log.info("""
                Создание инструмента
                Название {}
                Описание {}
                Доступность для бронирования {}
                ID владельца {}
                """, itemDtoInput.getName(), itemDtoInput.getDescription(), itemDtoInput.getAvailable(), userId);
        return itemService.createItem(itemDtoInput, userId);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDtoOutput updateItem(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long userId,
                                    @PathVariable @Positive long itemId,
                                    @RequestBody ItemDtoInput itemDtoInput) {
        log.info("""
                Обновление данных о инструменте
                ID инструмента {}
                Название {}
                Описание {}
                Доступность для бронирования {}
                ID владельца {}
                """, itemId, itemDtoInput.getName(), itemDtoInput.getDescription(), itemDtoInput.getAvailable(), userId);
        return itemService.updateItem(itemId, itemDtoInput, userId);
    }


    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDtoOutputBooking getItem(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                        @PathVariable @NonNull Long itemId) {
        log.info("""
                Поиск инструмента с ID {}
                """, itemId);
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDtoOutputBooking> getAllOwnerItems(@RequestHeader("X-Sharer-User-Id") @NotNull @Positive Long userId) {
        log.info("""
                Отображение списка инструментов пользователя с ID {}
                """, userId);
        return itemService.getAllOwnerItems(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDtoOutput> searchItems(@RequestParam("text") String text) {
        log.info("""
                Поиск инструментов по запросу "{}"
                """, text);
        return itemService.searchByQuery(text);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDtoOutput addComment(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                       @PathVariable @Positive long itemId,
                                       @RequestBody @Valid CommentDtoInput comment) {
        log.info("Received request from userId={} to add comment {} to itemId={}", userId, comment, itemId);
        return itemService.addComment(userId, itemId, comment);
    }
}
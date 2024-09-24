package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemMapper {

    @Mapping(target = "comments", source = "comments")
    ItemDtoOutput toDTO(Item item, Collection<CommentDtoOutput> comments);

    ItemDtoOutput toDTO(Item item);

    @Mapping(target = "comments", source = "comments")
    @Mapping(target = "lastBooking", source = "lastBooking.end")
    @Mapping(target = "nextBooking", source = "nextBooking.start")
    @Mapping(target = "id", source = "item.id")
    ItemDtoOutputBooking toDTOBookings(Item item, Collection<CommentDtoOutput> comments,
                                       Booking lastBooking, Booking nextBooking);

    @Mapping(target = "owner", source = "user")
    @Mapping(target = "name", source = "itemDTO.name")
    Item fromDTO(ItemDtoInput itemDTO, User user);
}

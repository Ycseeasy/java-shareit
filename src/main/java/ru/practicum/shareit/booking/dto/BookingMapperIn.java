package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookingMapperIn {

    @Mapping(target = "item", source = "item")
    @Mapping(target = "booker", source = "user")
    @Mapping(target = "id", ignore = true)
    Booking formDTO(BookingDtoInput bookingDtoInput, Item item, User user);
}

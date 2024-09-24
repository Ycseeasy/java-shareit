package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoOutput;
import ru.practicum.shareit.user.dto.UserDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookingMapperOut {

    @Mapping(target = "item", source = "itemDto")
    @Mapping(target = "booker", source = "userDto")
    @Mapping(target = "id", source = "booking.id")
    BookingDtoOutput toDTO(Booking booking, ItemDtoOutput itemDto, UserDto userDto);
}
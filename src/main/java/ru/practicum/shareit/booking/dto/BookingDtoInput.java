package ru.practicum.shareit.booking.dto;

import lombok.*;


import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoInput {
    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}

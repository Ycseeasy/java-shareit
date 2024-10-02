package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoTest {

    private final JacksonTester<BookingDto> json;

    @Test
    void testJson() throws Exception {
        BookingDto bookingDto = new BookingDto(
                1L,
                LocalDateTime.of(2024, 12, 21, 13, 32, 22),
                LocalDateTime.of(2025, 12, 21, 13, 32, 22),
                null,
                null,
                BookingStatus.WAITING
        );

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-12-21T13:32:22");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-12-21T13:32:22");
    }

    @Test
    void getterAndSetterTest() {
        BookingDto bookingDto = new BookingDto(
                1L,
                LocalDateTime.of(2024, 12, 21, 13, 32, 22),
                LocalDateTime.of(2025, 12, 21, 13, 32, 22),
                null,
                null,
                BookingStatus.WAITING
        );

        LocalDateTime end = bookingDto.getEnd();
        LocalDateTime newDateStart = LocalDateTime.of(2019, 12, 21, 13, 32, 22);
        bookingDto.setStart(newDateStart);

        assertEquals(newDateStart, bookingDto.getStart());
        assertEquals(end, bookingDto.getEnd());
    }
}

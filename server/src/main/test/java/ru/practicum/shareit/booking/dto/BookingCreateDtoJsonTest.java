package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingCreateDtoJsonTest {

    private final JacksonTester<BookingCreateDto> json;

    @Test
    void testJson() throws Exception {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                1L,
                LocalDateTime.of(2024,12,21,13,32,22),
                LocalDateTime.of(2025,12,21,13,32,22)
                );

        JsonContent<BookingCreateDto> result = json.write(bookingCreateDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-12-21T13:32:22");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-12-21T13:32:22");
    }

}
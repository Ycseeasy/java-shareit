package ru.practicum.shareit.item.dto.comment;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentCreateDTOJsonTest {

    private final JacksonTester<CommentCreateDTO> json;

    @Test
    void testJson() throws Exception {
        CommentCreateDTO comment = new CommentCreateDTO("text");

        JsonContent<CommentCreateDTO> result = json.write(comment);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
    }

}
package ru.practicum.shareit.item.dto.comment;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

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
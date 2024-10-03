package ru.practicum.shareit.item.dto.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemCreateDtoJsonTest {

    private final JacksonTester<ItemCreateDTO> json;

    @Test
    void testJson() throws Exception {
        ItemCreateDTO item = new ItemCreateDTO("name", "description", true, 1L);

        JsonContent<ItemCreateDTO> result = json.write(item);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }
}
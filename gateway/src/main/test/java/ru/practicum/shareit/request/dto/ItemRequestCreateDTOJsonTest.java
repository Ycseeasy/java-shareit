package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestCreateDTOJsonTest {

    private final JacksonTester<ItemRequestCreateDTO> json;

    @Test
    void testJson() throws Exception {
        ItemRequestCreateDTO request = new ItemRequestCreateDTO("text");

        JsonContent<ItemRequestCreateDTO> result = json.write(request);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("text");
       }

}
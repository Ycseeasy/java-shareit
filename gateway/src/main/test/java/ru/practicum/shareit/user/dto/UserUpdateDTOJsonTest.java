package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;



@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserUpdateDTOJsonTest {

    private final JacksonTester<UserUpdateDTO> json;

    @Test
    void testJson() throws Exception {
        UserUpdateDTO user = new UserUpdateDTO("name", "email@email");

        JsonContent<UserUpdateDTO> result = json.write(user);

        AssertionsForInterfaceTypes.assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("name");
        AssertionsForInterfaceTypes.assertThat(result).extractingJsonPathStringValue("$.email")
                .isEqualTo("email@email");
    }

}
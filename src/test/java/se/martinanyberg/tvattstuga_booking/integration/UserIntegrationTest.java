package se.martinanyberg.tvattstuga_booking.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import se.martinanyberg.tvattstuga_booking.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }


    @Test
    void login_createsNewUser() throws Exception {

        mockMvc.perform(
                        post("/api/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "email": "martina@test.se"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.email")
                                .value("martina@test.se")
                );

        assertEquals(
                1,
                userRepository.count()
        );

        assertTrue(
                userRepository.existsById(
                        "martina@test.se"
                )
        );
    }


    @Test
    void login_existingUserDoesNotCreateDuplicate()
            throws Exception {

        String json = """
                {
                    "email": "martina@test.se"
                }
                """;

        mockMvc.perform(
                        post("/api/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk());

        assertEquals(
                1,
                userRepository.count()
        );
    }


    @Test
    void invalidEmail_isRejected()
            throws Exception {

        mockMvc.perform(
                        post("/api/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "email": "martinatest.se"
                                        }
                                        """)
                )
                .andExpect(
                        status().isBadRequest()
                );

        assertEquals(
                0,
                userRepository.count()
        );
    }


    @Test
    void emptyEmail_isRejected()
            throws Exception {

        mockMvc.perform(
                        post("/api/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "email": ""
                                        }
                                        """)
                )
                .andExpect(
                        status().isBadRequest()
                );

        assertEquals(
                0,
                userRepository.count()
        );
    }
}
package se.martinanyberg.tvattstuga_booking.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import se.martinanyberg.tvattstuga_booking.model.Booking;
import se.martinanyberg.tvattstuga_booking.service.BookingService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;


    @Test
    void getAllBookings_returnsOk() throws Exception {

        when(bookingService.getAllBookings())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/bookings")
                )
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }


    @Test
    void getBookingsByUser_returnsUsersBookings() throws Exception {

        String email = "martina@test.se";

        Booking booking = new Booking(
                1L,
                "2026-09-28",
                "08-11",
                email
        );

        when(bookingService.getBookingsByUser(email))
                .thenReturn(List.of(booking));

        mockMvc.perform(
                        get("/api/bookings/user")
                                .param("email", email)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].date").value("2026-09-28"))
                .andExpect(jsonPath("$[0].timeSlot").value("08-11"))
                .andExpect(jsonPath("$[0].userEmail").value(email));
    }
}
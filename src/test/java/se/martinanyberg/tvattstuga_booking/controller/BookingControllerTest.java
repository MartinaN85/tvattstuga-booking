package se.martinanyberg.tvattstuga_booking.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import se.martinanyberg.tvattstuga_booking.model.Booking;
import se.martinanyberg.tvattstuga_booking.service.BookingService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

        verify(bookingService)
                .getAllBookings();
    }


    @Test
    void getBookingsByUser_returnsUsersBookings() throws Exception {

        String email = "martina@test.se";

        Booking booking = new Booking(
                1L,
                "2026-10-05",
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
                .andExpect(jsonPath("$[0].date").value("2026-10-05"))
                .andExpect(jsonPath("$[0].timeSlot").value("08-11"))
                .andExpect(jsonPath("$[0].userEmail").value(email));

        verify(bookingService)
                .getBookingsByUser(email);
    }


    @Test
    void getBookingsByUser_withoutEmail_returnsBadRequest() throws Exception {

        mockMvc.perform(
                        get("/api/bookings/user")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }


    @Test
    void createBooking_returnsCreatedBooking() throws Exception {

        Booking savedBooking = new Booking(
                1L,
                "2026-10-05",
                "08-11",
                "martina@test.se"
        );

        when(bookingService.createBooking(any(Booking.class)))
                .thenReturn(savedBooking);

        String json = """
                {
                    "date": "2026-10-05",
                    "timeSlot": "08-11",
                    "userEmail": "martina@test.se"
                }
                """;

        mockMvc.perform(
                        post("/api/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.date").value("2026-10-05"))
                .andExpect(jsonPath("$.timeSlot").value("08-11"))
                .andExpect(jsonPath("$.userEmail").value("martina@test.se"));

        verify(bookingService)
                .createBooking(any(Booking.class));
    }


    @Test
    void createBooking_withInvalidJson_returnsBadRequest() throws Exception {

        mockMvc.perform(
                        post("/api/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{invalid json}")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingService);
    }


    @Test
    void deleteBooking_deletesBooking() throws Exception {

        Long bookingId = 1L;

        mockMvc.perform(
                        delete("/api/bookings/{id}", bookingId)
                )
                .andExpect(status().isOk());

        verify(bookingService)
                .deleteBooking(bookingId);
    }
}
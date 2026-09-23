package se.martinanyberg.tvattstuga_booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.martinanyberg.tvattstuga_booking.model.Booking;
import se.martinanyberg.tvattstuga_booking.repository.BookingRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository repository;

    @InjectMocks
    private BookingService service;

    @Test
    void getAllBookings_returnsBookings() {

        Booking booking =
                new Booking(1L,
                        "Martina",
                        "2026-09-25",
                        "18:00-21:00");

        when(repository.findAll())
                .thenReturn(List.of(booking));

        List<Booking> bookings =
                service.getAllBookings();

        assertEquals(1, bookings.size());
        assertEquals("Martina", bookings.get(0).getName());
    }
}
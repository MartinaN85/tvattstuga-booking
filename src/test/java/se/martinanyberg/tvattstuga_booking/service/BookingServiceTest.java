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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository repository;

    @InjectMocks
    private BookingService service;


    @Test
    void getAllBookings_returnsBookings() {

        Booking booking = new Booking(
                1L,
                "2026-09-25",
                "18-21",
                "martina@test.se"
        );

        when(repository.findAll())
                .thenReturn(List.of(booking));

        List<Booking> bookings =
                service.getAllBookings();

        assertEquals(1, bookings.size());
        assertEquals(
                "2026-09-25",
                bookings.get(0).getDate()
        );
        assertEquals(
                "18-21",
                bookings.get(0).getTimeSlot()
        );
        assertEquals(
                "martina@test.se",
                bookings.get(0).getUserEmail()
        );
    }


    @Test
    void createBooking_savesAvailableBooking() {

        Booking booking = new Booking(
                null,
                "2026-09-28",
                "08-11",
                "martina@test.se"
        );

        when(repository.existsByDateAndTimeSlot(
                booking.getDate(),
                booking.getTimeSlot()
        )).thenReturn(false);

        when(repository.countByUserEmail(
                booking.getUserEmail()
        )).thenReturn(0L);

        when(repository.save(booking))
                .thenReturn(booking);


        Booking result =
                service.createBooking(booking);


        assertEquals(booking, result);

        verify(repository).save(booking);
    }


    @Test
    void createBooking_rejectsAlreadyBookedSlot() {

        Booking booking = new Booking(
                null,
                "2026-09-28",
                "08-11",
                "martina@test.se"
        );

        when(repository.existsByDateAndTimeSlot(
                booking.getDate(),
                booking.getTimeSlot()
        )).thenReturn(true);


        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.createBooking(booking)
                );


        assertEquals(
                "Tiden är redan bokad",
                exception.getMessage()
        );

        verify(repository, never())
                .save(any(Booking.class));
    }


    @Test
    void createBooking_rejectsThirdBooking() {

        Booking booking = new Booking(
                null,
                "2026-09-29",
                "14-17",
                "martina@test.se"
        );

        when(repository.existsByDateAndTimeSlot(
                booking.getDate(),
                booking.getTimeSlot()
        )).thenReturn(false);

        when(repository.countByUserEmail(
                booking.getUserEmail()
        )).thenReturn(2L);


        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.createBooking(booking)
                );


        assertEquals(
                "Du kan endast ha två bokningar samtidigt",
                exception.getMessage()
        );

        verify(repository, never())
                .save(any(Booking.class));
    }


    @Test
    void getBookingsByUser_returnsOnlyUsersBookings() {

        String email =
                "martina@test.se";

        Booking booking1 = new Booking(
                1L,
                "2026-09-28",
                "08-11",
                email
        );

        Booking booking2 = new Booking(
                2L,
                "2026-09-30",
                "14-17",
                email
        );

        when(repository.findByUserEmail(email))
                .thenReturn(
                        List.of(
                                booking1,
                                booking2
                        )
                );


        List<Booking> bookings =
                service.getBookingsByUser(email);


        assertEquals(2, bookings.size());

        assertEquals(
                email,
                bookings.get(0).getUserEmail()
        );

        assertEquals(
                email,
                bookings.get(1).getUserEmail()
        );
    }


    @Test
    void deleteBooking_deletesBooking() {

        Long bookingId = 1L;


        service.deleteBooking(bookingId);


        verify(repository)
                .deleteById(bookingId);
    }
}
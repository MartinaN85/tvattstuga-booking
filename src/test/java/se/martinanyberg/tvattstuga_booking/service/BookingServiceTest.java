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
                "2026-10-05",
                "18-21",
                "martina@test.se"
        );

        when(repository.findAll())
                .thenReturn(List.of(booking));

        List<Booking> bookings =
                service.getAllBookings();

        assertEquals(1, bookings.size());
        assertEquals(
                "2026-10-05",
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

        verify(repository).findAll();
    }


    @Test
    void getAllBookings_returnsEmptyListWhenNoBookingsExist() {

        when(repository.findAll())
                .thenReturn(List.of());

        List<Booking> bookings =
                service.getAllBookings();

        assertEquals(0, bookings.size());

        verify(repository).findAll();
    }


    @Test
    void createBooking_savesAvailableBooking() {

        Booking booking = new Booking(
                null,
                "2026-10-05",
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
    void createBooking_allowsSecondBooking() {

        Booking booking = new Booking(
                null,
                "2026-10-06",
                "14-17",
                "martina@test.se"
        );

        when(repository.existsByDateAndTimeSlot(
                booking.getDate(),
                booking.getTimeSlot()
        )).thenReturn(false);

        when(repository.countByUserEmail(
                booking.getUserEmail()
        )).thenReturn(1L);

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
                "2026-10-05",
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
                .countByUserEmail(anyString());

        verify(repository, never())
                .save(any(Booking.class));
    }


    @Test
    void createBooking_rejectsThirdBooking() {

        Booking booking = new Booking(
                null,
                "2026-10-06",
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
    void createBooking_rejectsNullBooking() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.createBooking(null)
                );

        assertEquals(
                "Bokningen får inte vara null",
                exception.getMessage()
        );

        verifyNoInteractions(repository);
    }


    @Test
    void createBooking_rejectsMissingDate() {

        Booking booking = new Booking(
                null,
                "",
                "08-11",
                "martina@test.se"
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.createBooking(booking)
                );

        assertEquals(
                "Datum måste anges",
                exception.getMessage()
        );

        verifyNoInteractions(repository);
    }


    @Test
    void createBooking_rejectsNullDate() {

        Booking booking = new Booking(
                null,
                null,
                "08-11",
                "martina@test.se"
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.createBooking(booking)
                );

        assertEquals(
                "Datum måste anges",
                exception.getMessage()
        );

        verifyNoInteractions(repository);
    }


    @Test
    void createBooking_rejectsMissingTimeSlot() {

        Booking booking = new Booking(
                null,
                "2026-10-05",
                "",
                "martina@test.se"
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.createBooking(booking)
                );

        assertEquals(
                "Tid måste anges",
                exception.getMessage()
        );

        verifyNoInteractions(repository);
    }


    @Test
    void createBooking_rejectsNullTimeSlot() {

        Booking booking = new Booking(
                null,
                "2026-10-05",
                null,
                "martina@test.se"
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.createBooking(booking)
                );

        assertEquals(
                "Tid måste anges",
                exception.getMessage()
        );

        verifyNoInteractions(repository);
    }


    @Test
    void createBooking_rejectsMissingUserEmail() {

        Booking booking = new Booking(
                null,
                "2026-10-05",
                "08-11",
                ""
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.createBooking(booking)
                );

        assertEquals(
                "E-post måste anges",
                exception.getMessage()
        );

        verifyNoInteractions(repository);
    }


    @Test
    void createBooking_rejectsNullUserEmail() {

        Booking booking = new Booking(
                null,
                "2026-10-05",
                "08-11",
                null
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.createBooking(booking)
                );

        assertEquals(
                "E-post måste anges",
                exception.getMessage()
        );

        verifyNoInteractions(repository);
    }


    @Test
    void getBookingsByUser_returnsOnlyUsersBookings() {

        String email = "martina@test.se";

        Booking booking1 = new Booking(
                1L,
                "2026-10-05",
                "08-11",
                email
        );

        Booking booking2 = new Booking(
                2L,
                "2026-10-06",
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

        verify(repository)
                .findByUserEmail(email);
    }


    @Test
    void getBookingsByUser_returnsEmptyListWhenUserHasNoBookings() {

        String email = "martina@test.se";

        when(repository.findByUserEmail(email))
                .thenReturn(List.of());

        List<Booking> bookings =
                service.getBookingsByUser(email);

        assertEquals(0, bookings.size());

        verify(repository)
                .findByUserEmail(email);
    }


    @Test
    void deleteBooking_deletesBooking() {

        Long bookingId = 1L;

        service.deleteBooking(bookingId);

        verify(repository)
                .deleteById(bookingId);
    }
}
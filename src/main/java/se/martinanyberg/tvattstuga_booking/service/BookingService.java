package se.martinanyberg.tvattstuga_booking.service;

import org.springframework.stereotype.Service;
import se.martinanyberg.tvattstuga_booking.model.Booking;
import se.martinanyberg.tvattstuga_booking.repository.BookingRepository;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository repository;

    public BookingService(BookingRepository repository) {
        this.repository = repository;
    }

    public List<Booking> getAllBookings() {
        return repository.findAll();
    }

    public List<Booking> getBookingsByUser(String userEmail) {
        return repository.findByUserEmail(userEmail);
    }

    public Booking createBooking(Booking booking) {

        boolean alreadyBooked =
                repository.existsByDateAndTimeSlot(
                        booking.getDate(),
                        booking.getTimeSlot()
                );

        if (alreadyBooked) {
            throw new IllegalArgumentException(
                    "Tiden är redan bokad"
            );
        }

        long numberOfBookings =
                repository.countByUserEmail(
                        booking.getUserEmail()
                );

        if (numberOfBookings >= 2) {
            throw new IllegalArgumentException(
                    "Du kan endast ha två bokningar samtidigt"
            );
        }

        return repository.save(booking);
    }

    public void deleteBooking(Long id) {
        repository.deleteById(id);
    }
}
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

    public Booking createBooking(Booking booking) {
        return repository.save(booking);
    }

    public void deleteBooking(Long id) {
        repository.deleteById(id);
    }
}
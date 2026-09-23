package se.martinanyberg.tvattstuga_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.martinanyberg.tvattstuga_booking.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    void deleteById(Long id);

    Booking save(Booking booking);
}
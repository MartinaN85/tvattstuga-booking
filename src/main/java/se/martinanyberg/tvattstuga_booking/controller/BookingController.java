package se.martinanyberg.tvattstuga_booking.controller;

import org.springframework.web.bind.annotation.*;
import se.martinanyberg.tvattstuga_booking.model.Booking;
import se.martinanyberg.tvattstuga_booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @GetMapping
    public List<Booking> getAllBookings() {
        return service.getAllBookings();
    }

    @GetMapping("/user")
    public List<Booking> getBookingsByUser(
            @RequestParam String email
    ) {
        return service.getBookingsByUser(email);
    }

    @PostMapping
    public Booking createBooking(
            @RequestBody Booking booking
    ) {
        return service.createBooking(booking);
    }

    @DeleteMapping("/{id}")
    public void deleteBooking(
            @PathVariable Long id
    ) {
        service.deleteBooking(id);
    }
}
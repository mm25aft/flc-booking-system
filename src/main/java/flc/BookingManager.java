package flc;

import java.util.HashMap;
import java.util.Map;

public class BookingManager {
    private final Map<String, Booking> allBookings = new HashMap<>();
    private int nextBookingId = 1;

    public Booking createBooking(Member member, Lesson lesson) {
        String id = generateBookingId();
        Booking booking = new Booking(id, member, lesson);
        allBookings.put(id, booking);
        return booking;
    }

    public boolean changeBooking(String bookingId, Lesson newLesson) {
        Booking booking = allBookings.get(bookingId);
        if (booking == null || newLesson == null) {
            return false;
        }
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.ATTENDED) {
            return false;
        }
        Lesson oldLesson = booking.getLesson();
        if (!newLesson.hasCapacity()) {
            return false;
        }
        oldLesson.removeBooking(booking);
        newLesson.addBooking(booking);
        booking.markChanged(newLesson);
        return true;
    }

    public boolean cancelBooking(String bookingId) {
        Booking booking = allBookings.get(bookingId);
        if (booking == null) {
            return false;
        }
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.ATTENDED) {
            return false;
        }
        booking.getLesson().removeBooking(booking);
        booking.markCancelled();
        return true;
    }

    public Booking getBooking(String bookingId) {
        return allBookings.get(bookingId);
    }

    public String generateBookingId() {
        return String.format("B%04d", nextBookingId++);
    }
}

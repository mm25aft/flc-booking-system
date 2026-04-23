package flc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Member {
    private final String memberId;
    private final String memberName;
    private final List<Booking> bookings = new ArrayList<>();
    //Handles booking a lesson for a member
    public Member(String memberId, String memberName) {
        this.memberId = memberId;
        this.memberName = memberName;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public List<Booking> getBookings() {
        return Collections.unmodifiableList(bookings);
    }

    public Booking bookLesson(Lesson lesson, BookingManager manager) {
        if (lesson == null || manager == null) {
            return null;
        }
        if (!lesson.hasCapacity()) {
            return null;
        }
        if (!canBookLesson(lesson, null)) {
            return null;
        }
        Booking booking = manager.createBooking(this, lesson);
        if (booking != null) {
            bookings.add(booking);
            lesson.addBooking(booking);
        }
        return booking;
    }

    public boolean changeBooking(String bookingId, Lesson newLesson, BookingManager manager) {
        if (bookingId == null || newLesson == null || manager == null) {
            return false;
        }
        Booking existing = manager.getBooking(bookingId);
        if (existing == null || existing.getMember() != this) {
            return false;
        }
        if (!newLesson.hasCapacity()) {
            return false;
        }
        if (!canBookLesson(newLesson, bookingId)) {
            return false;
        }
        return manager.changeBooking(bookingId, newLesson);
    }

    public boolean cancelBooking(String bookingId, BookingManager manager) {
        if (bookingId == null || manager == null) {
            return false;
        }
        Booking existing = manager.getBooking(bookingId);
        if (existing == null || existing.getMember() != this) {
            return false;
        }
        return manager.cancelBooking(bookingId);
    }

    public Review attendLesson(String bookingId, int rating, String text, BookingManager manager) {
        if (bookingId == null || manager == null) {
            return null;
        }
        Booking booking = manager.getBooking(bookingId);
        if (booking == null || booking.getMember() != this) {
            return null;
        }
        Review review = new Review(text, rating);
        if (!review.isValidRating()) {
            return null;
        }
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.ATTENDED) {
            return null;
        }
        booking.markAttended();
        booking.getLesson().addReview(review);
        return review;
    }

    private boolean canBookLesson(Lesson lesson, String excludeBookingId) {
        for (Booking booking : bookings) {
            if (excludeBookingId != null && booking.getBookingId().equals(excludeBookingId)) {
                continue;
            }
            if (booking.getStatus() == BookingStatus.CANCELLED) {
                continue;
            }
            Lesson bookedLesson = booking.getLesson();
            if (bookedLesson.getLessonId().equals(lesson.getLessonId())) {
                return false;
            }
            boolean sameWeekend = bookedLesson.getWeekendNumber() == lesson.getWeekendNumber();
            boolean sameDay = bookedLesson.getDay() == lesson.getDay();
            boolean sameSlot = bookedLesson.getTimeSlot() == lesson.getTimeSlot();
            if (sameWeekend && sameDay && sameSlot) {
                return false;
            }
        }
        return true;
    }
}

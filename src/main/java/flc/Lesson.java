package flc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lesson {
    private final String lessonId;
    private final ExerciseType exerciseType;
    private final Day day;
    private final TimeSlot timeSlot;
    private final double price;
    private final int weekendNumber;
    private final int capacity = 4;
    private final List<Booking> bookings = new ArrayList<>();
    private final List<Review> reviews = new ArrayList<>();

    public Lesson(String lessonId, ExerciseType exerciseType, Day day, TimeSlot timeSlot, double price,
            int weekendNumber) {
        this.lessonId = lessonId;
        this.exerciseType = exerciseType;
        this.day = day;
        this.timeSlot = timeSlot;
        this.price = price;
        this.weekendNumber = weekendNumber;
    }

    public String getLessonId() {
        return lessonId;
    }

    public ExerciseType getExerciseType() {
        return exerciseType;
    }

    public Day getDay() {
        return day;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public double getPrice() {
        return price;
    }

    public int getWeekendNumber() {
        return weekendNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<Booking> getBookings() {
        return Collections.unmodifiableList(bookings);
    }

    public List<Review> getReviews() {
        return Collections.unmodifiableList(reviews);
    }

    public boolean hasCapacity() {
        return bookings.size() < capacity;
    }

    public boolean addBooking(Booking booking) {
        if (booking == null || !hasCapacity()) {
            return false;
        }
        return bookings.add(booking);
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking);
    }

    public void addReview(Review review) {
        if (review != null && review.isValidRating()) {
            reviews.add(review);
        }
    }

    public int attendedCount() {
        int count = 0;
        for (Booking booking : bookings) {
            if (booking.getStatus() == BookingStatus.ATTENDED) {
                count++;
            }
        }
        return count;
    }

    public double averageRating() {
        if (reviews.isEmpty()) {
            return 0.0;
        }
        int sum = 0;
        for (Review review : reviews) {
            sum += review.getRating();
        }
        return (double) sum / reviews.size();
    }
}

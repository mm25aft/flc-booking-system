package flc;

public class Booking {
    private final String bookingId;
    private final Member member;
    private Lesson lesson;
    private BookingStatus status;

    public Booking(String bookingId, Member member, Lesson lesson) {
        this.bookingId = bookingId;
        this.member = member;
        this.lesson = lesson;
        this.status = BookingStatus.BOOKED;
    }

    public String getBookingId() {
        return bookingId;
    }

    public Member getMember() {
        return member;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void markChanged(Lesson newLesson) {
        this.lesson = newLesson;
        this.status = BookingStatus.CHANGED;
    }

    public void markCancelled() {
        this.status = BookingStatus.CANCELLED;
    }

    public void markAttended() {
        this.status = BookingStatus.ATTENDED;
    }
}

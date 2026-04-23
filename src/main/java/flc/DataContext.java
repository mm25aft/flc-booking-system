package flc;

import java.util.List;

public class DataContext {
    private final List<Member> members;
    private final List<Timetable> timetables;
    private final BookingManager bookingManager;

    public DataContext(List<Member> members, List<Timetable> timetables, BookingManager bookingManager) {
        this.members = members;
        this.timetables = timetables;
        this.bookingManager = bookingManager;
    }

    public List<Member> getMembers() {
        return members;
    }

    public List<Timetable> getTimetables() {
        return timetables;
    }

    public BookingManager getBookingManager() {
        return bookingManager;
    }
}

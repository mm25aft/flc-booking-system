package flc;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SampleData {
    public static DataContext createSampleData() {
        BookingManager bookingManager = new BookingManager();
        List<Member> members = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            members.add(new Member(String.format("M%03d", i), "Member " + i));
        }

        Map<ExerciseType, Double> prices = new EnumMap<>(ExerciseType.class);
        prices.put(ExerciseType.YOGA, 8.0);
        prices.put(ExerciseType.ZUMBA, 9.0);
        prices.put(ExerciseType.AQUACISE, 10.0);
        prices.put(ExerciseType.BOX_FIT, 11.0);
        prices.put(ExerciseType.BODY_BLITZ, 12.0);

        List<Timetable> timetables = new ArrayList<>();
        List<Lesson> allLessons = new ArrayList<>();
        int lessonCounter = 1;
        ExerciseType[] types = ExerciseType.values();
        int typeIndex = 0;

        for (int weekend = 1; weekend <= 8; weekend++) {
            Timetable timetable = new Timetable(weekend);
            for (Day day : Day.values()) {
                for (TimeSlot slot : TimeSlot.values()) {
                    ExerciseType type = types[typeIndex % types.length];
                    typeIndex++;
                    double price = prices.get(type);
                    Lesson lesson = new Lesson(
                            String.format("L%02d", lessonCounter++),
                            type,
                            day,
                            slot,
                            price,
                            weekend);
                    timetable.addLesson(lesson);
                    allLessons.add(lesson);
                }
            }
            timetables.add(timetable);
        }

        List<Booking> attendedBookings = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Member member = members.get(i % members.size());
            Lesson lesson = allLessons.get(i);
            Booking booking = member.bookLesson(lesson, bookingManager);
            if (booking != null) {
                attendedBookings.add(booking);
            }
        }

        int rating = 3;
        for (Booking booking : attendedBookings) {
            Member member = booking.getMember();
            String reviewText = "Good session " + booking.getLesson().getExerciseType();
            member.attendLesson(booking.getBookingId(), rating, reviewText, bookingManager);
            rating++;
            if (rating > 5) {
                rating = 3;
            }
        }

        return new DataContext(members, timetables, bookingManager);
    }
}

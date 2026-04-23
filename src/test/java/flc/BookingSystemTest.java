package flc;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class BookingSystemTest {
    @Test
    void testLessonCapacityLimit() {
        BookingManager manager = new BookingManager();
        Lesson lesson = new Lesson("L01", ExerciseType.YOGA, Day.SATURDAY, TimeSlot.MORNING, 8.0, 1);
        for (int i = 1; i <= 4; i++) {
            Member member = new Member(String.format("M%03d", i), "Member " + i);
            Booking booking = member.bookLesson(lesson, manager);
            assertNotNull(booking);
        }
        Member extra = new Member("M999", "Extra");
        Booking shouldFail = extra.bookLesson(lesson, manager);
        assertNull(shouldFail);
    }

    @Test
    void testDuplicateBookingNotAllowed() {
        BookingManager manager = new BookingManager();
        Member member = new Member("M001", "Member 1");
        Lesson lesson = new Lesson("L01", ExerciseType.ZUMBA, Day.SUNDAY, TimeSlot.MORNING, 9.0, 1);
        Booking first = member.bookLesson(lesson, manager);
        Booking second = member.bookLesson(lesson, manager);
        assertNotNull(first);
        assertNull(second);
    }

    @Test
    void testTimeSlotConflictNotAllowed() {
        BookingManager manager = new BookingManager();
        Member member = new Member("M001", "Member 1");
        Lesson l1 = new Lesson("L01", ExerciseType.AQUACISE, Day.SATURDAY, TimeSlot.MORNING, 10.0, 1);
        Lesson l2 = new Lesson("L02", ExerciseType.BOX_FIT, Day.SATURDAY, TimeSlot.MORNING, 11.0, 1);
        assertNotNull(member.bookLesson(l1, manager));
        assertNull(member.bookLesson(l2, manager));
    }

    @Test
    void testChangeBookingKeepsId() {
        BookingManager manager = new BookingManager();
        Member member = new Member("M001", "Member 1");
        Lesson l1 = new Lesson("L01", ExerciseType.BODY_BLITZ, Day.SUNDAY, TimeSlot.AFTERNOON, 12.0, 1);
        Lesson l2 = new Lesson("L02", ExerciseType.YOGA, Day.SATURDAY, TimeSlot.EVENING, 8.0, 1);
        Booking booking = member.bookLesson(l1, manager);
        assertNotNull(booking);
        boolean changed = member.changeBooking(booking.getBookingId(), l2, manager);
        assertTrue(changed);
        Booking updated = manager.getBooking(booking.getBookingId());
        assertEquals(booking.getBookingId(), updated.getBookingId());
        assertEquals(BookingStatus.CHANGED, updated.getStatus());
        assertEquals("L02", updated.getLesson().getLessonId());
    }

    @Test
    void testChampionReportIncomeTotals() {
        DataContext context = SampleData.createSampleData();
        ReportService reportService = new ReportService();
        ChampionReport report = reportService.generateMonthlyChampionReport(1, context.getTimetables());
        double computedTotal = 0.0;
        for (Map.Entry<ExerciseType, Double> entry : report.getIncomeByType().entrySet()) {
            computedTotal += entry.getValue();
        }

        double expectedTotal = 0.0;
        List<Timetable> tables = context.getTimetables();
        for (int i = 0; i < 4; i++) {
            for (Lesson lesson : tables.get(i).getLessons()) {
                expectedTotal += lesson.getPrice() * lesson.attendedCount();
            }
        }
        assertEquals(expectedTotal, computedTotal, 0.0001);
        assertNotNull(report.getChampion());
    }
}

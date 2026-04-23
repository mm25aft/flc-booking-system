package flc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        DataContext context = SampleData.createSampleData();
        BookingManager bookingManager = context.getBookingManager();
        ReportService reportService = new ReportService();
        List<Member> members = new ArrayList<>(context.getMembers());
        List<Timetable> timetables = context.getTimetables();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> handleBookLesson(scanner, members, timetables, bookingManager);
                case "2" -> handleChangeOrCancel(scanner, members, timetables, bookingManager);
                case "3" -> handleAttendLesson(scanner, members, bookingManager);
                case "4" -> handleMonthlyLessonReport(scanner, reportService, timetables);
                case "5" -> handleMonthlyChampionReport(scanner, reportService, timetables);
                case "6" -> running = false;
                case "7" -> handleRegisterMember(scanner, members);
                default -> System.out.println("Invalid option. Try again.");
            }
        }
        System.out.println("Goodbye.");
    }

    private static void printMenu() {
        System.out.println("\nGroup Exercise Booking Management System");
        System.out.println("1. Book Lesson");
        System.out.println("2. Change or Cancel Booking");
        System.out.println("3. Attend Lesson (add review + rating)");
        System.out.println("4. Monthly Lesson Report");
        System.out.println("5. Monthly Champion Exercise Report");
        System.out.println("6. Exit Program");
        System.out.println("7. Register new member");
        System.out.print("Select option: ");
    }

    private static void handleBookLesson(Scanner scanner, List<Member> members,
            List<Timetable> timetables, BookingManager bookingManager) {
        Member member = promptMember(scanner, members);
        if (member == null) {
            return;
        }
        List<Lesson> matches = searchLessons(scanner, timetables);
        if (matches.isEmpty()) {
            System.out.println("No lessons found.");
            return;
        }
        printLessons(matches);
        System.out.print("Enter lessonId to book: ");
        String lessonId = scanner.nextLine().trim();
        Lesson lesson = findLessonByIdInList(matches, lessonId);
        if (lesson == null) {
            System.out.println("Lesson not found in the listed results.");
            return;
        }
        String bookingValidation = validateNewBooking(member, lesson);
        if (bookingValidation != null) {
            System.out.println(bookingValidation);
            return;
        }
        Booking booking = member.bookLesson(lesson, bookingManager);
        if (booking == null) {
            System.out.println("Booking failed. Check capacity, duplicates, or time-slot conflicts.");
            return;
        }
        System.out.println("Booking successful. BookingId: " + booking.getBookingId());
    }

    private static void handleChangeOrCancel(Scanner scanner, List<Member> members,
            List<Timetable> timetables, BookingManager bookingManager) {
        Member member = promptMember(scanner, members);
        if (member == null) {
            return;
        }
        System.out.print("Enter bookingId: ");
        String bookingId = scanner.nextLine().trim();
        Booking booking = findMemberBooking(bookingManager, member, bookingId);
        if (booking == null) {
            System.out.println("Booking not found for this member.");
            return;
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            System.out.println("Booking is already cancelled.");
            return;
        }
        if (booking.getStatus() == BookingStatus.ATTENDED) {
            System.out.println("Booking is already attended and cannot be changed or cancelled.");
            return;
        }
        String action = promptChangeOrCancel(scanner);
        if ("X".equals(action)) {
            boolean cancelled = member.cancelBooking(bookingId, bookingManager);
            System.out.println(cancelled ? "Booking cancelled." : "Cancel failed.");
            return;
        }
        List<Lesson> matches = searchLessons(scanner, timetables);
        if (matches.isEmpty()) {
            System.out.println("No lessons found.");
            return;
        }
        printLessons(matches);
        System.out.print("Enter new lessonId: ");
        String lessonId = scanner.nextLine().trim();
        Lesson newLesson = findLessonByIdInList(matches, lessonId);
        if (newLesson == null) {
            System.out.println("Lesson not found in the listed results.");
            return;
        }
        String changeValidation = validateChangeBooking(member, booking, newLesson);
        if (changeValidation != null) {
            System.out.println(changeValidation);
            return;
        }
        boolean changed = member.changeBooking(bookingId, newLesson, bookingManager);
        System.out.println(changed ? "Booking changed." : "Change failed.");
    }

    private static void handleAttendLesson(Scanner scanner, List<Member> members, BookingManager bookingManager) {
        Member member = promptMember(scanner, members);
        if (member == null) {
            return;
        }
        System.out.print("Enter bookingId: ");
        String bookingId = scanner.nextLine().trim();
        Booking booking = findMemberBooking(bookingManager, member, bookingId);
        if (booking == null) {
            System.out.println("Booking not found for this member.");
            return;
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            System.out.println("Cannot attend a cancelled booking.");
            return;
        }
        if (booking.getStatus() == BookingStatus.ATTENDED) {
            System.out.println("Booking already attended.");
            return;
        }
        int rating = promptRating(scanner);
        System.out.print("Review text: ");
        String text = scanner.nextLine();
        Review review = member.attendLesson(bookingId, rating, text, bookingManager);
        System.out.println(review == null ? "Attend failed." : "Attendance recorded.");
    }

    private static void handleMonthlyLessonReport(Scanner scanner, ReportService reportService,
            List<Timetable> timetables) {
        int month = promptMonth(scanner);
        LessonReport report = reportService.generateMonthlyLessonReport(month, timetables);
        System.out.println("\nMonthly Lesson Report for month " + String.format("%02d", report.getMonth()));
        for (LessonStats stat : report.getStats()) {
            System.out.printf("%s %s %s %s | Attended: %d | Avg Rating: %.2f%n",
                    stat.getLessonId(),
                    stat.getExerciseType(),
                    stat.getDay(),
                    stat.getTimeSlot(),
                    stat.getAttendedCount(),
                    stat.getAverageRating());
        }
    }

    private static void handleMonthlyChampionReport(Scanner scanner, ReportService reportService,
            List<Timetable> timetables) {
        int month = promptMonth(scanner);
        ChampionReport report = reportService.generateMonthlyChampionReport(month, timetables);
        System.out.println("\nMonthly Champion Exercise Report for month " + String.format("%02d", report.getMonth()));
        for (Map.Entry<ExerciseType, Double> entry : report.getIncomeByType().entrySet()) {
            System.out.printf("%s Income: %.2f%n", entry.getKey(), entry.getValue());
        }
        System.out.println("Champion Exercise Type: " + report.getChampion());
    }

    private static void handleRegisterMember(Scanner scanner, List<Member> members) {
        System.out.print("Enter new member name: ");
        String name = scanner.nextLine().trim();
        String memberId = String.format("M%03d", members.size() + 1);
        Member member = new Member(memberId, name.isEmpty() ? "Member " + memberId : name);
        members.add(member);
        System.out.println("Member registered with ID: " + memberId);
    }

    private static Member promptMember(Scanner scanner, List<Member> members) {
        System.out.print("Enter memberId: ");
        String memberId = scanner.nextLine().trim();
        for (Member member : members) {
            if (member.getMemberId().equalsIgnoreCase(memberId)) {
                return member;
            }
        }
        System.out.println("Member not found.");
        return null;
    }

    private static int promptMonth(Scanner scanner) {
        while (true) {
            System.out.print("Enter month (two digits, e.g. 05): ");
            String input = scanner.nextLine().trim();
            if (!input.matches("\\d{2}")) {
                System.out.println("Invalid month format. Use two digits (e.g., 05).");
                continue;
            }
            int month = Integer.parseInt(input);
            if (month < 1 || month > 12) {
                System.out.println("Invalid month. Enter 01 to 12.");
                continue;
            }
            return month;
        }
    }

    private static List<Lesson> searchLessons(Scanner scanner, List<Timetable> timetables) {
        while (true) {
            System.out.print("Search by Day (D) or Exercise Type (E)?: ");
            String mode = scanner.nextLine().trim().toUpperCase();
            List<Lesson> matches = new ArrayList<>();
            if ("D".equals(mode)) {
                Day day = promptDay(scanner);
                if (day == null) {
                    return matches;
                }
                for (Timetable timetable : timetables) {
                    matches.addAll(timetable.findByDay(day));
                }
                return matches;
            }
            if ("E".equals(mode)) {
                ExerciseType type = promptExerciseType(scanner);
                if (type == null) {
                    return matches;
                }
                for (Timetable timetable : timetables) {
                    matches.addAll(timetable.findByExerciseType(type));
                }
                return matches;
            }
            System.out.println("Invalid option. Enter D or E.");
        }
    }

    private static Lesson findLessonByIdInList(List<Lesson> lessons, String lessonId) {
        for (Lesson lesson : lessons) {
            if (lesson.getLessonId().equalsIgnoreCase(lessonId)) {
                return lesson;
            }
        }
        return null;
    }

    private static void printLessons(List<Lesson> lessons) {
        for (Lesson lesson : lessons) {
            int available = lesson.getCapacity() - lesson.getBookings().size();
            System.out.printf("Weekend %d | %s | %s | %s | %s | Price: %.2f | Available: %d%n",
                    lesson.getWeekendNumber(),
                    lesson.getLessonId(),
                    lesson.getExerciseType(),
                    lesson.getDay(),
                    lesson.getTimeSlot(),
                    lesson.getPrice(),
                    available);
        }
    }

    private static Booking findMemberBooking(BookingManager bookingManager, Member member, String bookingId) {
        if (bookingId == null || bookingId.isBlank()) {
            return null;
        }
        Booking booking = bookingManager.getBooking(bookingId);
        if (booking == null || booking.getMember() != member) {
            return null;
        }
        return booking;
    }

    private static String promptChangeOrCancel(Scanner scanner) {
        while (true) {
            System.out.print("Change (C) or Cancel (X)?: ");
            String action = scanner.nextLine().trim().toUpperCase();
            if ("C".equals(action) || "X".equals(action)) {
                return action;
            }
            System.out.println("Invalid action. Enter C to change or X to cancel.");
        }
    }

    private static String validateNewBooking(Member member, Lesson lesson) {
        if (!lesson.hasCapacity()) {
            return "Booking failed: lesson is full.";
        }
        for (Booking booking : member.getBookings()) {
            if (booking.getStatus() == BookingStatus.CANCELLED) {
                continue;
            }
            Lesson bookedLesson = booking.getLesson();
            if (bookedLesson.getLessonId().equals(lesson.getLessonId())) {
                return "Booking failed: duplicate booking for the same lesson.";
            }
            boolean sameWeekend = bookedLesson.getWeekendNumber() == lesson.getWeekendNumber();
            boolean sameDay = bookedLesson.getDay() == lesson.getDay();
            boolean sameSlot = bookedLesson.getTimeSlot() == lesson.getTimeSlot();
            if (sameWeekend && sameDay && sameSlot) {
                return "Booking failed: time-slot conflict with an existing booking.";
            }
        }
        return null;
    }

    private static String validateChangeBooking(Member member, Booking booking, Lesson newLesson) {
        if (booking.getLesson().getLessonId().equals(newLesson.getLessonId())) {
            return "Change failed: new lesson must be different.";
        }
        if (!newLesson.hasCapacity()) {
            return "Change failed: new lesson is full.";
        }
        for (Booking existing : member.getBookings()) {
            if (existing.getBookingId().equals(booking.getBookingId())) {
                continue;
            }
            if (existing.getStatus() == BookingStatus.CANCELLED) {
                continue;
            }
            Lesson bookedLesson = existing.getLesson();
            if (bookedLesson.getLessonId().equals(newLesson.getLessonId())) {
                return "Change failed: duplicate booking for the same lesson.";
            }
            boolean sameWeekend = bookedLesson.getWeekendNumber() == newLesson.getWeekendNumber();
            boolean sameDay = bookedLesson.getDay() == newLesson.getDay();
            boolean sameSlot = bookedLesson.getTimeSlot() == newLesson.getTimeSlot();
            if (sameWeekend && sameDay && sameSlot) {
                return "Change failed: time-slot conflict with an existing booking.";
            }
        }
        return null;
    }

    private static Day promptDay(Scanner scanner) {
        while (true) {
            System.out.print("Enter day (SATURDAY/SUNDAY): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if ("SATURDAY".equals(input) || "SUNDAY".equals(input)) {
                return Day.valueOf(input);
            }
            System.out.println("Invalid day. Please enter SATURDAY or SUNDAY.");
        }
    }

    private static ExerciseType promptExerciseType(Scanner scanner) {
        while (true) {
            System.out.print("Enter exercise type: ");
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                return ExerciseType.valueOf(input);
            } catch (IllegalArgumentException ex) {
                System.out.println("Invalid exercise type. Try again.");
            }
        }
    }

    private static int promptRating(Scanner scanner) {
        while (true) {
            System.out.print("Rating (1-5): ");
            String input = scanner.nextLine().trim();
            try {
                int rating = Integer.parseInt(input);
                if (rating >= 1 && rating <= 5) {
                    return rating;
                }
                System.out.println("Invalid rating. Enter a number from 1 to 5.");
            } catch (NumberFormatException ex) {
                System.out.println("Invalid rating. Enter a number from 1 to 5.");
            }
        }
    }
}

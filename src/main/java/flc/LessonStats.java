package flc;

public class LessonStats {
    private final String lessonId;
    private final ExerciseType exerciseType;
    private final Day day;
    private final TimeSlot timeSlot;
    private final int attendedCount;
    private final double averageRating;

    public LessonStats(Lesson lesson) {
        this.lessonId = lesson.getLessonId();
        this.exerciseType = lesson.getExerciseType();
        this.day = lesson.getDay();
        this.timeSlot = lesson.getTimeSlot();
        this.attendedCount = lesson.attendedCount();
        this.averageRating = lesson.averageRating();
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

    public int getAttendedCount() {
        return attendedCount;
    }

    public double getAverageRating() {
        return averageRating;
    }
}

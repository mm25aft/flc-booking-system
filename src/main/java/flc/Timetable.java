package flc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Timetable {
    private final int weekendNumber;
    private final List<Lesson> lessons = new ArrayList<>();

    public Timetable(int weekendNumber) {
        this.weekendNumber = weekendNumber;
    }

    public int getWeekendNumber() {
        return weekendNumber;
    }

    public List<Lesson> getLessons() {
        return Collections.unmodifiableList(lessons);
    }

    public void addLesson(Lesson lesson) {
        if (lesson != null) {
            lessons.add(lesson);
        }
    }

    public Lesson findById(String lessonId) {
        for (Lesson lesson : lessons) {
            if (lesson.getLessonId().equals(lessonId)) {
                return lesson;
            }
        }
        return null;
    }

    public List<Lesson> findByDay(Day day) {
        List<Lesson> result = new ArrayList<>();
        for (Lesson lesson : lessons) {
            if (lesson.getDay() == day) {
                result.add(lesson);
            }
        }
        return result;
    }

    public List<Lesson> findByExerciseType(ExerciseType type) {
        List<Lesson> result = new ArrayList<>();
        for (Lesson lesson : lessons) {
            if (lesson.getExerciseType() == type) {
                result.add(lesson);
            }
        }
        return result;
    }
}

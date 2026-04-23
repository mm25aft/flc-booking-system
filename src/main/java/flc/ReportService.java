package flc;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ReportService {
    public LessonReport generateMonthlyLessonReport(int month, List<Timetable> timetables) {
        List<Timetable> monthTables = selectMonthTimetables(month, timetables);
        List<LessonStats> stats = new ArrayList<>();
        for (Timetable timetable : monthTables) {
            for (Lesson lesson : timetable.getLessons()) {
                stats.add(new LessonStats(lesson));
            }
        }
        return new LessonReport(month, stats);
    }

    public ChampionReport generateMonthlyChampionReport(int month, List<Timetable> timetables) {
        List<Timetable> monthTables = selectMonthTimetables(month, timetables);
        Map<ExerciseType, Double> income = new EnumMap<>(ExerciseType.class);
        for (ExerciseType type : ExerciseType.values()) {
            income.put(type, 0.0);
        }
        for (Timetable timetable : monthTables) {
            for (Lesson lesson : timetable.getLessons()) {
                int attended = lesson.attendedCount();
                double value = income.get(lesson.getExerciseType());
                income.put(lesson.getExerciseType(), value + (lesson.getPrice() * attended));
            }
        }
        ExerciseType champion = null;
        double max = -1;
        for (Map.Entry<ExerciseType, Double> entry : income.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                champion = entry.getKey();
            }
        }
        return new ChampionReport(month, income, champion);
    }

    private List<Timetable> selectMonthTimetables(int month, List<Timetable> timetables) {
        List<Timetable> result = new ArrayList<>();
        if (timetables == null || timetables.isEmpty()) {
            return result;
        }
        int startIndex = (month % 2 == 1) ? 0 : 4;
        int endIndex = Math.min(startIndex + 4, timetables.size());
        for (int i = startIndex; i < endIndex; i++) {
            result.add(timetables.get(i));
        }
        return result;
    }
}

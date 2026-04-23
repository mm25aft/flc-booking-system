package flc;

import java.util.Collections;
import java.util.List;

public class LessonReport {
    private final int month;
    private final List<LessonStats> stats;

    public LessonReport(int month, List<LessonStats> stats) {
        this.month = month;
        this.stats = stats == null ? List.of() : List.copyOf(stats);
    }

    public int getMonth() {
        return month;
    }

    public List<LessonStats> getStats() {
        return Collections.unmodifiableList(stats);
    }
}

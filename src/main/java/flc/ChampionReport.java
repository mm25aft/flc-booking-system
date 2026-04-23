package flc;

import java.util.Collections;
import java.util.Map;

public class ChampionReport {
    private final int month;
    private final Map<ExerciseType, Double> incomeByType;
    private final ExerciseType champion;

    public ChampionReport(int month, Map<ExerciseType, Double> incomeByType, ExerciseType champion) {
        this.month = month;
        this.incomeByType = incomeByType == null ? Map.of() : Map.copyOf(incomeByType);
        this.champion = champion;
    }

    public int getMonth() {
        return month;
    }

    public Map<ExerciseType, Double> getIncomeByType() {
        return Collections.unmodifiableMap(incomeByType);
    }

    public ExerciseType getChampion() {
        return champion;
    }
}

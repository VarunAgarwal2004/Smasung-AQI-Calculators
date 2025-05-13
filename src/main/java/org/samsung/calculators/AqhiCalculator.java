package org.samsung.calculators;

import org.samsung.AQICalculator;

import java.util.List;
import java.util.Map;

/** Canadian “Air-Quality Health Index” (AQHI). */
public final class AqhiCalculator implements AQICalculator {

    @Override
    public Result calculate(Map<String, Double> v) {
        /* concentrations (null → 0) */
        double o3  = v.getOrDefault("O3",   0.0);     // ppb
        double pm  = v.getOrDefault("PM2.5",0.0);     // µg/m³
        double no2 = v.getOrDefault("NO2",  0.0);     // ppb

        /* risk contribution */
        double rO3  = 0.000537 * o3;
        double rPM  = 0.000487 * pm;
        double rNO2 = 0.000871 * no2;

        double aqhi = Math.round(10 * (rO3 + rPM + rNO2));

        String culprit = switch (max(rO3, rPM, rNO2)) {
            case 1 -> "O3";
            case 2 -> "PM2.5";
            default -> "NO2";
        };

        Category cat = CATEGORIES.stream()
                .filter(c -> aqhi >= c.min && aqhi <= c.max)
                .findFirst()
                .orElse(UNKNOWN);

        return new Result((int) aqhi, cat.name, cat.color, culprit);
    }

    /* -------- helpers -------- */

    /** which of the three doubles is largest – returns 0/1/2 marker */
    private static int max(double a, double b, double c) {
        return (a >= b && a >= c) ? 1 : (b >= a && b >= c ? 2 : 3);
    }

    private record Category(String name, int min, int max, String color) {}
    private static final List<Category> CATEGORIES = List.of(
            new Category("Low",        1, 3,  "Blue"),
            new Category("Moderate",   4, 6,  "Yellow"),
            new Category("High",       7, 10, "Orange"),
            new Category("Very High", 11, Integer.MAX_VALUE, "Red")
    );
    private static final Category UNKNOWN = new Category("Unknown",0,0,"Unknown");
}

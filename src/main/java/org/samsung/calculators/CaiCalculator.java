package org.samsung.calculators;

import org.samsung.AQICalculator;

import java.util.List;
import java.util.Map;

/** South-Korea CAI implementation. */
public final class CaiCalculator implements AQICalculator {

    @Override
    public Result calculate(Map<String, Double> values) {
        double highest = 0;
        String culprit = "";

        for (Pollutant p : Pollutant.values()) {
            Double c = values.get(p.displayName());
            if (c == null) continue;

            double sub = p.indexFor(c);
            if (sub > highest) {
                highest  = sub;
                culprit  = p.displayName();
            }
        }

        final double h = highest;
        Category cat = CATEGORIES.stream()
                .filter(c -> h >= c.min() && h <= c.max())
                .findFirst()
                .orElse(UNKNOWN);

        return new Result((int) Math.round(highest),
                cat.name(), cat.color(), culprit);
    }

    /* ------ static helpers ------------------------------------------------ */

    private static final List<Category> CATEGORIES = List.of(
            new Category("Good",           0,   50, "Blue"),
            new Category("Moderate",      51,  100, "Green"),
            new Category("Unhealthy",    101,  250, "Yellow"),
            new Category("Very Unhealthy",251, 500, "Red")
    );
    private static final Category UNKNOWN = new Category("Unknown", 0, 0, "Unknown");

    private record Category(String name, int min, int max, String color) {}

    private enum Pollutant {
        SO2("SO2",
                new double[]{0.000, 0.020, 0.051, 0.151},
                new double[]{0.020, 0.050, 0.150, 1.000},
                new int[]   {0, 51, 101, 251},
                new int[]   {50,100,250,500}),

        CO("CO",
                new double[]{0, 2, 9, 15},
                new double[]{2, 9, 15, 50},
                new int[]   {0, 51, 101, 251},
                new int[]   {50,100,250,500}),

        O3("O3",
                new double[]{0.000, 0.030, 0.091, 0.151},
                new double[]{0.030, 0.090, 0.150, 0.600},
                new int[]   {0, 51, 101, 251},
                new int[]   {50,100,250,500}),

        NO2("NO2",
                new double[]{0.000, 0.030, 0.061, 0.201},
                new double[]{0.030, 0.060, 0.200, 2.000},
                new int[]   {0, 51, 101, 251},
                new int[]   {50,100,250,500}),

        PM10("PM10",
                new double[]{0, 31, 81, 151},
                new double[]{30,80,150,600},
                new int[]   {0, 51, 101, 251},
                new int[]   {50,100,250,500}),

        PM25("PM2.5",
                new double[]{0, 16, 36, 76},
                new double[]{15,35,75,500},
                new int[]   {0, 51, 101, 251},
                new int[]   {50,100,250,500});

        /* ---- fields + ctor + helpers ---- */

        private final String displayName;
        private final double[] bpLo, bpHi;
        private final int[] iLo, iHi;

        Pollutant(String n, double[] lo, double[] hi, int[] il, int[] ih) {
            displayName = n; bpLo = lo; bpHi = hi; iLo = il; iHi = ih;
        }
        String displayName() { return displayName; }

        double indexFor(double value) {
            for (int i = 0; i < bpLo.length; i++) {
                if (value >= bpLo[i] && value <= bpHi[i]) {
                    return (iHi[i]-iLo[i])/(bpHi[i]-bpLo[i])
                            * (value-bpLo[i]) + iLo[i];
                }
            }
            return iHi[iHi.length-1];      // above highest breakpoint
        }
    }
}

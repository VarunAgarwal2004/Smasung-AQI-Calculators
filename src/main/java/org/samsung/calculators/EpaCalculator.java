// src/main/java/org/samsung/calculators/EpaCalculator.java
package org.samsung.calculators;

import org.samsung.AQICalculator;

import java.util.List;
import java.util.Map;

/** United-States EPA AQI implementation. */
public final class EpaCalculator implements AQICalculator {

    // ---------- public API ---------------------------------------------------

    @Override
    public Result calculate(Map<String, Double> values) {
        double highest = 0;
        String culprit = "";

        /* 1.  Loop through every pollutant and keep the highest sub-index. */
        for (Pollutant p : Pollutant.values()) {
            Double concentration = values.get(p.displayName());
            if (concentration == null) continue;

            double subIndex = p.indexFor(concentration);
            if (subIndex > highest) {
                highest  = subIndex;
                culprit  = p.displayName();
            }
        }

        /* 2.  Translate the numeric AQI into a textual category + colour. */
        final double highestFinal = highest;                    // make it effectively-final for the lambda
        Category category = CATEGORIES.stream()
                .filter(c -> highestFinal >= c.min() && highestFinal <= c.max())
                .findFirst()
                .orElse(UNKNOWN_CATEGORY);

        return new Result((int) Math.round(highest), category.name(), category.color(), culprit);
    }

    // ---------- static helpers ----------------------------------------------

    /** EPA break-point table → textual category & colour. */
    private static final List<Category> CATEGORIES = List.of(
            new Category("Good",            0,   50,  "Green"),
            new Category("Moderate",       51,  100,  "Yellow"),
            new Category("USG",           101,  150,  "Orange"),
            new Category("Unhealthy",     151,  200,  "Red"),
            new Category("Very Unhealthy",201,  300,  "Purple"),
            new Category("Hazardous",     301,  500,  "Maroon")
    );
    private static final Category UNKNOWN_CATEGORY = new Category("Unknown", 0, 0, "Unknown");

    /** Immutable record used only inside this class. */
    private record Category(String name, int min, int max, String color) {}

    /**
     * Enum containing break-points and linear-interpolation logic for each pollutant
     * recognised by the U.S. EPA standard.
     */
    private enum Pollutant {
        PM25("PM2.5",
                new double[]{   0,  12,  35.4,  55.4, 150.4, 250.4, 350.4},
                new double[]{  12, 35.4, 55.4, 150.4, 250.4, 350.4, 500.4},
                new int[]   {   0,   51,   101,   151,   201,   301,   401},
                new int[]   {  50,  100,   150,   200,   300,   400,   500}),

        PM10("PM10",
                new double[]{   0,   54,  154,  254,  354,  424,  504},
                new double[]{  54,  154,  254,  354,  424,  504,  604},
                new int[]   {   0,   51,  101,  151,  201,  301,  401},
                new int[]   {  50,  100,  150,  200,  300,  400,  500}),

        O3("O3",
                new double[]{0.000, 0.054, 0.070, 0.085, 0.105, 0.200},
                new double[]{0.054, 0.070, 0.085, 0.105, 0.200, 0.300},
                new int[]   {   0,    51,   101,   151,   201,   301},
                new int[]   {  50,   100,   150,   200,   300,   500}),

        CO("CO",
                new double[]{ 0.0, 4.4, 9.4, 12.4, 15.4, 30.4},
                new double[]{ 4.4, 9.4,12.4, 15.4, 30.4, 40.4},
                new int[]   {   0,  51, 101, 151, 201, 301},
                new int[]   {  50, 100, 150, 200, 300, 500}),

        SO2("SO2",
                new double[]{   0,   35,   75,  185,  304,  604},
                new double[]{  35,   75,  185,  304,  604,  804},
                new int[]   {   0,   51,  101,  151,  201,  301},
                new int[]   {  50,  100,  150,  200,  300,  500}),

        NO2("NO2",
                new double[]{   0,   53,  100,  360,  649, 1249},
                new double[]{  53,  100,  360,  649, 1249, 2049},
                new int[]   {   0,   51,  101,  151,  201,  301},
                new int[]   {  50,  100,  150,  200,  300,  500});

        // ----- constructor & fields -----
        private final String  displayName;
        private final double[] bpLo, bpHi;
        private final int[]    iLo, iHi;

        Pollutant(String displayName,
                  double[] bpLo, double[] bpHi,
                  int[] iLo,     int[] iHi) {
            this.displayName = displayName;
            this.bpLo  = bpLo;
            this.bpHi  = bpHi;
            this.iLo   = iLo;
            this.iHi   = iHi;
        }

        String displayName() { return displayName; }

        /**
         * Convert a concentration to its sub-index using linear interpolation,
         * per EPA rules.
         */
        double indexFor(double value) {
            for (int i = 0; i < bpLo.length; i++) {
                if (value >= bpLo[i] && value <= bpHi[i]) {
                    return (iHi[i] - iLo[i]) / (bpHi[i] - bpLo[i])
                            * (value - bpLo[i]) + iLo[i];
                }
            }
            // value is above the highest break-point → clamp to maximum AQI.
            return iHi[iHi.length - 1];
        }
    }
}

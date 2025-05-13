package org.samsung.calculators;

import org.samsung.AQICalculator;

import java.util.List;
import java.util.Map;

/** India NAQI implementation. */
public final class NaqiCalculator implements AQICalculator {

    @Override
    public Result calculate(Map<String, Double> values) {
        double highest = 0;
        String culprit = "";

        for (Pollutant p : Pollutant.values()) {
            Double c = values.get(p.displayName());
            if (c == null) continue;

            double sub = p.indexFor(c);
            if (sub > highest) {
                highest = sub;
                culprit = p.displayName();
            }
        }

        final double h = highest;
        Category cat = CATEGORIES.stream()
                .filter(c -> h >= c.min() && h <= c.max())
                .findFirst()
                .orElse(UNKNOWN);

        return new Result((int)Math.round(highest),
                cat.name(), cat.color(), culprit);
    }

    /* ---- helpers ---------------------------------------------------------- */

    private static final List<Category> CATEGORIES = List.of(
            new Category("Good",          0,   50, "Green"),
            new Category("Satisfactory", 51,  100, "Light Green"),
            new Category("Moderate",    101,  200, "Yellow"),
            new Category("Poor",        201,  300, "Orange"),
            new Category("Very Poor",   301,  400, "Red"),
            new Category("Severe",      401,  500, "Maroon")
    );
    private static final Category UNKNOWN = new Category("Unknown", 0,0,"Unknown");

    private record Category(String name, int min, int max, String color) {}

    private enum Pollutant {
        PM25("PM2.5",
                new double[]{0, 31, 61, 91, 121, 251},
                new double[]{30,60,90,120,250,500},
                new int[]{0,51,101,201,301,401},
                new int[]{50,100,200,300,400,500}),

        PM10("PM10",
                new double[]{0,51,101,251,351,431},
                new double[]{50,100,250,350,430,500},
                new int[]{0,51,101,201,301,401},
                new int[]{50,100,200,300,400,500}),

        SO2("SO2",
                new double[]{0,41,81,381,801,1601},
                new double[]{40,80,380,800,1600,2000},
                new int[]{0,51,101,201,301,401},
                new int[]{50,100,200,300,400,500}),

        NO2("NO2",
                new double[]{0,41,81,181,281,401},
                new double[]{40,80,180,280,400,500},
                new int[]{0,51,101,201,301,401},
                new int[]{50,100,200,300,400,500}),

        CO("CO",
                new double[]{0,1.1,2.1,10.1,17.1,34.1},
                new double[]{1,2,10,17,34,50},
                new int[]{0,51,101,201,301,401},
                new int[]{50,100,200,300,400,500}),

        O3("O3",
                new double[]{0,51,101,169,209,749},
                new double[]{50,100,168,208,748,1000},
                new int[]{0,51,101,201,301,401},
                new int[]{50,100,200,300,400,500}),

        NH3("NH3",
                new double[]{0,201,401,801,1201,1801},
                new double[]{200,400,800,1200,1800,2400},
                new int[]{0,51,101,201,301,401},
                new int[]{50,100,200,300,400,500});

        /* ctor + data + helper */
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
                            * (value - bpLo[i]) + iLo[i];
                }
            }
            return iHi[iHi.length-1];
        }
    }
}

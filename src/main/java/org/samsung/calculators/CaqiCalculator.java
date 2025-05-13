package org.samsung.calculators;

import org.samsung.AQICalculator;

import java.util.List;
import java.util.Map;

public final class CaqiCalculator implements AQICalculator {

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
                .filter(c -> h >= c.min && h <= c.max)
                .findFirst()
                .orElse(UNKNOWN);

        return new Result((int) Math.round(highest),
                cat.name, cat.color, culprit);
    }

    /* ------------ static data ---------------- */

    private record Category(String name, int min, int max, String color) {}
    private static final List<Category> CATEGORIES = List.of(
            new Category("Very Low", 0, 25, "Green"),
            new Category("Low",      26, 50, "Yellow"),
            new Category("Medium",   51, 75, "Orange"),
            new Category("High",     76,100, "Red"),
            new Category("Very High",101,Integer.MAX_VALUE,"Purple")
    );
    private static final Category UNKNOWN = new Category("Unknown",0,0,"Unknown");

    private enum Pollutant {
        PM10("PM10",
                new double[]{0, 21, 41, 51, 101},
                new double[]{20,40,50,100,500},
                new int[]{0,26,51,76,101},
                new int[]{25,50,75,100,500}),

        PM25("PM2.5",
                new double[]{0,11,21,26,51},
                new double[]{10,20,25,50,250},
                new int[]{0,26,51,76,101},
                new int[]{25,50,75,100,500}),

        O3("O3",
                new double[]{0,41,81,101,141},
                new double[]{40,80,100,140,240},
                new int[]{0,26,51,76,101},
                new int[]{25,50,75,100,500}),

        NO2("NO2",
                new double[]{0,26,51,101,201},
                new double[]{25,50,100,200,400},
                new int[]{0,26,51,76,101},
                new int[]{25,50,75,100,500});

        private final String n; private final double[] lo, hi; private final int[] ilo, ihi;
        Pollutant(String n,double[] lo,double[] hi,int[] ilo,int[] ihi){
            this.n=n; this.lo=lo; this.hi=hi; this.ilo=ilo; this.ihi=ihi;}
        String displayName(){ return n; }
        double indexFor(double v){
            for(int i=0;i<lo.length;i++)
                if(v>=lo[i]&&v<=hi[i])
                    return (ihi[i]-ilo[i])/(hi[i]-lo[i])*(v-lo[i])+ilo[i];
            return ihi[ihi.length-1];
        }
    }
}

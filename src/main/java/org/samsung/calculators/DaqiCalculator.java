package org.samsung.calculators;

import org.samsung.AQICalculator;

import java.util.List;
import java.util.Map;

/** DEFRA Daily Air-Quality Index (UK). */
public final class DaqiCalculator implements AQICalculator {

    @Override
    public Result calculate(Map<String, Double> v) {
        double highest = 0;
        String culprit = "";

        for (Pollutant p : Pollutant.values()) {
            Double c = v.get(p.displayName());
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

        return new Result((int)Math.round(highest),
                cat.name, cat.color, culprit);
    }

    /* --- helpers --- */

    private record Category(String name,int min,int max,String color){}
    private static final List<Category> CATEGORIES = List.of(
            new Category("Low",       1,3,"Green"),
            new Category("Moderate",  4,6,"Yellow"),
            new Category("High",      7,9,"Red"),
            new Category("Very High",10,10,"Purple")
    );
    private static final Category UNKNOWN=new Category("Unknown",0,0,"Unknown");

    private enum Pollutant {
        SO2("SO2",
                new double[]{0,267,533,1065},
                new double[]{266,532,1064,1200},
                new int[]{1,4,7,10},
                new int[]{3,6,9,10}),

        O3("O3",
                new double[]{0,101,161,241},
                new double[]{100,160,240,350},
                new int[]{1,4,7,10},
                new int[]{3,6,9,10}),

        NO2("NO2",
                new double[]{0,201,401,601},
                new double[]{200,400,600,800},
                new int[]{1,4,7,10},
                new int[]{3,6,9,10}),

        PM10("PM10",
                new double[]{0,51,76,101},
                new double[]{50,75,100,150},
                new int[]{1,4,7,10},
                new int[]{3,6,9,10}),

        PM25("PM2.5",
                new double[]{0,36,54,71},
                new double[]{35,53,70,100},
                new int[]{1,4,7,10},
                new int[]{3,6,9,10});

        private final String n;private final double[] lo,hi;private final int[] ilo,ihi;
        Pollutant(String n,double[] lo,double[] hi,int[] ilo,int[] ihi){
            this.n=n;this.lo=lo;this.hi=hi;this.ilo=ilo;this.ihi=ihi;}
        String displayName(){return n;}
        double indexFor(double v){
            for(int i=0;i<lo.length;i++)
                if(v>=lo[i]&&v<=hi[i])
                    return (ihi[i]-ilo[i])/(hi[i]-lo[i])*(v-lo[i])+ilo[i];
            return ihi[ihi.length-1];
        }
    }
}

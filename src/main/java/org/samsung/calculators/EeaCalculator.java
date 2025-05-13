package org.samsung.calculators;

import org.samsung.AQICalculator;

import java.util.List;
import java.util.Map;

public final class EeaCalculator implements AQICalculator {

    @Override
    public Result calculate(Map<String, Double> v) {
        double highest = 0;
        String culprit = "";

        for (Pollutant p : Pollutant.values()) {
            Double c = v.get(p.displayName());
            if (c == null) continue;

            double sub = p.indexFor(c);
            if (sub > highest) { highest = sub; culprit = p.displayName(); }
        }

        final double h = highest;
        Category cat = CATEGORIES.stream()
                .filter(c -> h >= c.min && h <= c.max)
                .findFirst()
                .orElse(UNKNOWN);

        return new Result((int)Math.round(highest),
                cat.name, cat.color, culprit);
    }

    /* ---- static tables ---- */

    private record Category(String name,int min,int max,String color){}
    private static final List<Category> CATEGORIES = List.of(
            new Category("Good",            0, 20,"Light Blue"),
            new Category("Fair",           21, 40,"Green"),
            new Category("Moderate",       41, 60,"Yellow"),
            new Category("Poor",           61, 80,"Orange"),
            new Category("Very Poor",      81,100,"Red"),
            new Category("Extremely Poor",101,Integer.MAX_VALUE,"Purple")
    );
    private static final Category UNKNOWN=new Category("Unknown",0,0,"Unknown");

    private enum Pollutant {
        PM25("PM2.5",
                new double[]{0,10,20,25,50,75},
                new double[]{10,20,25,50,75,800},
                new int[]{0,20,40,60,80,100},
                new int[]{20,40,60,80,100,100}),

        PM10("PM10",
                new double[]{0,20,40,50,100,150},
                new double[]{20,40,50,100,150,1200},
                new int[]{0,20,40,60,80,100},
                new int[]{20,40,60,80,100,100}),

        NO2("NO2",
                new double[]{0,40,90,120,230,340},
                new double[]{40,90,120,230,340,1000},
                new int[]{0,20,40,60,80,100},
                new int[]{20,40,60,80,100,100}),

        O3("O3",
                new double[]{0,50,100,130,240,380},
                new double[]{50,100,130,240,380,800},
                new int[]{0,20,40,60,80,100},
                new int[]{20,40,60,80,100,100}),

        SO2("SO2",
                new double[]{0,100,200,350,500,750},
                new double[]{100,200,350,500,750,1250},
                new int[]{0,20,40,60,80,100},
                new int[]{20,40,60,80,100,100});

        private final String n; private final double[] lo,hi; private final int[] ilo,ihi;
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

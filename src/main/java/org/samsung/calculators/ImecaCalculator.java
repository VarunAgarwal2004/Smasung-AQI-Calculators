package org.samsung.calculators;

import org.samsung.AQICalculator;

import java.util.List;
import java.util.Map;

/** Mexican IMECA index. */
public final class ImecaCalculator implements AQICalculator {

    @Override
    public Result calculate(Map<String, Double> v) {
        double hi = 0; String culprit = "";

        for (Pollutant p : Pollutant.values()) {
            Double c = v.get(p.displayName());
            if (c == null) continue;
            double sub = p.indexFor(c);
            if (sub > hi) { hi = sub; culprit = p.displayName(); }
        }

        final double h = hi;
        Category cat = CATEGORIES.stream()
                .filter(c -> h >= c.min && h <= c.max)
                .findFirst()
                .orElse(UNKNOWN);

        return new Result((int)Math.round(hi),
                cat.name, cat.color, culprit);
    }

    private record Category(String name,int min,int max,String color){}
    private static final List<Category> CATEGORIES = List.of(
            new Category("Good",0,50,"Green"),
            new Category("Regular",51,100,"Yellow"),
            new Category("Bad",101,150,"Orange"),
            new Category("Very Bad",151,200,"Red"),
            new Category("Extremely Bad",201,300,"Purple")
    );
    private static final Category UNKNOWN = new Category("Unknown",0,0,"Unknown");

    private enum Pollutant {
        PM10("PM10",
                new double[]{0,61,121,221,321},
                new double[]{60,120,220,320,420},
                new int[]{0,51,101,151,201},
                new int[]{50,100,150,200,300}),
        PM25("PM2.5",
                new double[]{0,15.5,40.5,65.5,90.5},
                new double[]{15.4,40.4,65.4,90.4,115.4},
                new int[]{0,51,101,151,201},
                new int[]{50,100,150,200,300}),
        O3("O3",
                new double[]{0,0.056,0.096,0.136,0.176},
                new double[]{0.055,0.095,0.135,0.175,0.220},
                new int[]{0,51,101,151,201},
                new int[]{50,100,150,200,300}),
        NO2("NO2",
                new double[]{0,0.111,0.211,0.311,0.411},
                new double[]{0.110,0.210,0.310,0.410,0.510},
                new int[]{0,51,101,151,201},
                new int[]{50,100,150,200,300}),
        SO2("SO2",
                new double[]{0,0.136,0.186,0.236,0.286},
                new double[]{0.135,0.185,0.235,0.285,0.335},
                new int[]{0,51,101,151,201},
                new int[]{50,100,150,200,300}),
        CO("CO",
                new double[]{0,5.6,11.6,17.6,23.6},
                new double[]{5.5,11.5,17.5,23.5,29.5},
                new int[]{0,51,101,151,201},
                new int[]{50,100,150,200,300});

        private final String n; private final double[] lo,hi; private final int[] ilo,ihi;
        Pollutant(String n,double[] lo,double[] hi,int[] ilo,int[] ihi){
            this.n=n; this.lo=lo; this.hi=hi; this.ilo=ilo; this.ihi=ihi;}
        String displayName(){ return n; }
        double indexFor(double v){
            for(int i=0;i<lo.length;i++)
                if(v>=lo[i]&&v<=hi[i])
                    return (ihi[i]-ilo[i])/(hi[i]-lo[i])*(v-lo[i])+ilo[i];
            // above highest range
            return ihi[ihi.length-1];
        }
    }
}

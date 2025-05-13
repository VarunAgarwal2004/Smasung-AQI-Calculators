package org.samsung.calculators;

import org.samsung.AQICalculator;

import java.util.List;
import java.util.Map;

/** Chinese MEE standard HJ 633-2012. */
public final class Hj6332012Calculator implements AQICalculator {

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

    /* ---- static tables ---- */

    private record Category(String name,int min,int max,String color){}
    private static final List<Category> CATEGORIES = List.of(
            new Category("Good", 0, 50,"Green"),
            new Category("Moderate",51,100,"Yellow"),
            new Category("Unhealthy for Sensitive Groups",101,150,"Orange"),
            new Category("Unhealthy",151,200,"Red"),
            new Category("Very Unhealthy",201,300,"Purple"),
            new Category("Hazardous",301,500,"Maroon")
    );
    private static final Category UNKNOWN = new Category("Unknown",0,0,"Unknown");

    private enum Pollutant {
        PM25("PM2.5",
                new double[]{0,35,75,115,150,250,350},
                new double[]{34,74,114,149,249,349,500},
                new int[]{0,51,101,151,201,301,401},
                new int[]{50,100,150,200,300,400,500}),
        PM10("PM10",
                new double[]{0,50,150,250,350,420,500},
                new double[]{49,149,249,349,419,499,600},
                new int[]{0,51,101,151,201,301,401},
                new int[]{50,100,150,200,300,400,500}),
        SO2("SO2",
                new double[]{0,150,500,650,800},
                new double[]{149,499,649,799,1000},
                new int[]{0,51,101,201,301},
                new int[]{50,100,200,300,400}),
        NO2("NO2",
                new double[]{0,40,80,180,280,400},
                new double[]{39,79,179,279,399,500},
                new int[]{0,51,101,201,301,401},
                new int[]{50,100,200,300,400,500}),
        CO("CO",
                new double[]{0,2,4,14,24},
                new double[]{1.9,3.9,13.9,23.9,35},
                new int[]{0,51,101,201,301},
                new int[]{50,100,200,300,400}),
        O3("O3",
                new double[]{0,100,160,215,265,800},
                new double[]{99,159,214,264,799,1000},
                new int[]{0,51,101,151,201,301},
                new int[]{50,100,150,200,300,500});

        private final String n; private final double[] lo,hi; private final int[] ilo,ihi;
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

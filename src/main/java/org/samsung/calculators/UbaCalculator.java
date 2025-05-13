package org.samsung.calculators;

import org.samsung.AQICalculator;

import java.util.List;
import java.util.Map;

/** German Umweltbundesamt 1-to-5 index. */
public final class UbaCalculator implements AQICalculator {

    @Override
    public Result calculate(Map<String, Double> v) {
        double hi = 0; String culprit = "";

        for (Pollutant p : Pollutant.values()) {
            Double c = v.get(p.displayName());
            if (c == null) continue;
            double sub = p.indexFor(c);
            if (sub > hi) { hi = sub; culprit = p.displayName(); }
        }

        Category cat = CATEGORIES.get((int)Math.round(hi) - 1);
        return new Result((int)Math.round(hi),
                cat.name, cat.color, culprit);
    }

    private record Category(String name,String color){}
    private static final List<Category> CATEGORIES = List.of(
            new Category("Very Good","Cyan"),
            new Category("Good","Teal"),
            new Category("Moderate","Yellow"),
            new Category("Poor","Red"),
            new Category("Very Poor","Dark Red")
    );

    private enum Pollutant {
        NO2("NO2",
                new double[]{0,21,41,101,201},
                new double[]{20,40,100,200,Double.POSITIVE_INFINITY},
                new int[]{1,2,3,4,5}),
        PM10("PM10",
                new double[]{0,21,36,51,101},
                new double[]{20,35,50,100,Double.POSITIVE_INFINITY},
                new int[]{1,2,3,4,5}),
        PM25("PM2.5",
                new double[]{0,11,21,26,51},
                new double[]{10,20,25,50,Double.POSITIVE_INFINITY},
                new int[]{1,2,3,4,5}),
        O3("O3",
                new double[]{0,61,121,181,241},
                new double[]{60,120,180,240,Double.POSITIVE_INFINITY},
                new int[]{1,2,3,4,5});

        private final String n; private final double[] lo,hi; private final int[] idx;
        Pollutant(String n,double[] lo,double[] hi,int[] idx){
            this.n=n; this.lo=lo; this.hi=hi; this.idx=idx;}
        String displayName(){ return n; }
        double indexFor(double v){
            for(int i=0;i<lo.length;i++)
                if(v>=lo[i]&&v<=hi[i]) return idx[i];
            return idx[idx.length-1];
        }
    }
}

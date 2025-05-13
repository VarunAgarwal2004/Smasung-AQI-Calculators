

import org.samsung.AQICalculator;
import org.samsung.calculators.CalculatorFactory;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String,Double> sample = Map.of(
                "PM2.5", 35.0,
                "O3",    80.0,   // µg/m³ for CAQI, ppb for AQHI
                "NO2",   90.0
        );

        System.out.println(CalculatorFactory.of("naqi").calculate(sample));
        System.out.println(CalculatorFactory.of("cai").calculate(sample));
        System.out.println(CalculatorFactory.of("aqhi").calculate(sample));
        System.out.println(CalculatorFactory.of("caqi").calculate(sample));
        System.out.println(CalculatorFactory.of("daqi").calculate(sample));
        System.out.println(CalculatorFactory.of("eea").calculate(sample));
        System.out.println(CalculatorFactory.of("hj6332012").calculate(sample));
        System.out.println(CalculatorFactory.of("imeca").calculate(sample));
        System.out.println(CalculatorFactory.of("uba").calculate(sample));
    }
}

package org.samsung.calculators;

import org.samsung.AQICalculator;

public final class CalculatorFactory {

    private CalculatorFactory() {}

    public static AQICalculator of(String standard) {
        return switch (standard.toLowerCase()) {
            case "epa"        -> new EpaCalculator();
            case "cai"        -> new CaiCalculator();
            case "naqi"       -> new NaqiCalculator();
            case "aqhi"       -> new AqhiCalculator();
            case "caqi"       -> new CaqiCalculator();
            case "daqi"       -> new DaqiCalculator();
            case "eea"        -> new EeaCalculator();
            case "imeca"      -> new ImecaCalculator();
            case "uba"        -> new UbaCalculator();
            case "hj6332012"  -> new Hj6332012Calculator();
            default -> throw new IllegalArgumentException(
                    "Unsupported AQI standard: " + standard);
        };
    }
}

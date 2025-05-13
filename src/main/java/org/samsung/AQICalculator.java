// src/main/java/org/samsung/AQICalculator.java
package org.samsung;

import java.util.Map;

public interface AQICalculator {

    /**
     * @param pollutantValues key = pollutant name (e.g. "PM2.5"), value = concentration
     * @return Result object with AQI value, category, colour, and the pollutant that drove the AQI.
     */
    Result calculate(Map<String, Double> pollutantValues);

    /** Simple data carrier returned by every calculator implementation. */
    record Result(int aqi, String category, String color, String pollutant) {}
}

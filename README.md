# AQI Calculators Library

A Java library for calculating the *Air Quality Index (AQI)* based on pollutant concentration data. This library is designed for integration into applications that need to assess air quality, such as environmental monitoring tools, weather forecasting apps, or health and safety systems.

---

## 📦 Project Information

- *Group ID*: io.github.samsung.aqi
- *Artifact ID*: aqi-calculators
- *Version*: 1.0.0
- *Package*: io.github.samsung.aqi

---

## 🧩 Features

- Supports AQI calculation using standard breakpoints (e.g., PM2.5, PM10, CO, NO₂, SO₂, O₃).
- Follows national and international AQI computation guidelines (e.g., US EPA, India CPCB, etc.).
- Easy to integrate into both Java and Android applications.
- Lightweight and dependency-free core logic.

---

## 🛠 Installation

### 🔹 Option 1: Use with *Maven*

Add the following dependency to your pom.xml:

xml
<dependency>
  <groupId>io.github.samsung.aqi</groupId>
  <artifactId>aqi-calculators</artifactId>
  <version>1.0.0</version>
</dependency>

### 🔹 Option 2: Use with *Gradle*

Add this to your build.gradle:

groovy
dependencies {
    implementation 'io.github.samsung.aqi:aqi-calculators:1.0.0'
}


> ⚠ If you haven't published the library to a remote repository yet, you can use it locally by installing it in your .m2 repo using:
>
> bash
> mvn install
> 

---

## 🚀 Usage

Here’s a sample example on how to use the library in your code:

java
import io.github.samsung.aqi.AQICalculator;

public class Main {
    public static void main(String[] args) {
        double pm25Concentration = 87.5;

        int aqi = AQICalculator.calculatePM25AQI(pm25Concentration);
        System.out.println("The AQI for PM2.5 concentration " + pm25Concentration + " is: " + aqi);
    }
}


> The AQICalculator class provides static methods for calculating AQI based on different pollutants. You can extend this logic or use multiple calculators depending on your needs.

---

## 📂 Project Structure


aqi-calculators/
├── pom.xml
└── src/
    ├── main/
    │   └── java/
    │       └── io/github/samsung/aqi/
    │           └── AQICalculator.java
    └── test/
        └── java/
            └── io/github/samsung/aqi/
                └── AQICalculatorTest.java


---

## 🧪 Testing

Run tests using Maven:

bash
mvn test


Or with Gradle:

bash
gradle test

```

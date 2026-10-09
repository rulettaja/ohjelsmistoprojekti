# Temperature Converter

## 1. Assignment Description

This individual assignment implements a temperature-conversion program. It converts values between Fahrenheit and Celsius and identifies whether a temperature, expressed in Celsius, is outside the configured normal range.

Deliverables in this repository:
- A JavaFX desktop interface for selecting a conversion direction, entering a value, and viewing the converted result and temperature-range message.
- A command-line interface for performing either conversion.
- Automated unit tests for conversion calculations, range boundaries, and command-line behavior.
- A Maven build with JaCoCo test-coverage reporting.

## 2. Technologies & Tools Used

- Java 17
- JavaFX Controls 21.0.1 for the desktop interface
- Apache Maven for dependency management, building, and test execution
- JUnit Jupiter 5.10.2 for automated tests
- JaCoCo 0.8.11 for test-coverage reports
- Docker for container image builds
- Jenkins for the build, test, and image-publishing pipeline

## 3. Design Approach & Implementation Method

The conversion calculations are kept in the `TemperatureConverter` class, separate from the user interfaces. Fahrenheit-to-Celsius uses `(F - 32) * 5 / 9`; Celsius-to-Fahrenheit uses `(C * 9 / 5) + 32`. The extreme-temperature check accepts a Celsius value and reports extreme temperatures strictly below -40°C or above 50°C; the boundary values are considered within the normal range.

`TemperatureConverterApp` builds a small JavaFX form with a conversion selector, temperature input, Convert button, result, and range message. Invalid numeric input is caught and shown in the interface. `Main` provides a command-line entry point with explicit conversion names and argument validation. The project has no database component.

## 4. Testing & Quality Assurance Steps

Automated tests use JUnit 5 and can be run with:

```sh
mvn clean verify
```

The test suite covers:

| Scenario | Expected result checked by the tests |
| --- | --- |
| Fahrenheit-to-Celsius conversions (32°F, 212°F, 0°F) | 0°C, 100°C, and approximately -17.7778°C |
| Celsius-to-Fahrenheit conversions (0°C, 100°C, -40°C) | 32°F, 212°F, and -40°F |
| Extreme-temperature threshold | -41°C and 51°C are extreme; -40°C, 50°C, and 25°C are not |
| CLI conversions in both directions | Correct numeric results |
| Missing, unsupported, or non-numeric CLI arguments | Appropriate argument or number-format exception |
| CLI output | Converted number is printed to standard output |

The Jenkins pipeline runs `mvn -B clean verify`, publishes Surefire test results, and archives the JaCoCo report. To reproduce the automated checks locally, run `mvn clean verify` from the repository root.

## 5. How to Run

### Prerequisites

- JDK 17 or newer
- Apache Maven 3.9 or newer
- A desktop environment to display the JavaFX interface

### Build and test

From the repository root, run:

```sh
mvn clean verify
```

### Run the desktop application

Import the repository as a Maven project in an IDE, then run `TemperatureConverterApp` as the Java application entry point. Ensure the IDE uses JDK 17 or newer and resolves the Maven dependencies.

### Run the command-line converter

First build the project, then run the `Main` class from the generated JAR:

```sh
mvn package
java -cp target/standalone-temperature-project-1.0-SNAPSHOT.jar Main fahrenheit-to-celsius 32
```

Use `fahrenheit-to-celsius` or `celsius-to-fahrenheit` as the first argument, followed by a numeric temperature. For example:

```sh
java -cp target/standalone-temperature-project-1.0-SNAPSHOT.jar Main celsius-to-fahrenheit 0
```

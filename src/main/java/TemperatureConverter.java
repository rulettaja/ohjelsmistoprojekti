public class TemperatureConverter {

    public double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - 32) * 5 / 9.0;
    }

    public double celsiusToFahrenheit(double celsius) {
        return (celsius * 9 / 5.0) + 32;
    }

    public double convert(double value, String fromUnit, String toUnit) {
        double celsius = switch (fromUnit) {
            case "celsius" -> value;
            case "fahrenheit" -> fahrenheitToCelsius(value);
            case "kelvin" -> value - 273.15;
            default -> throw new IllegalArgumentException("Unsupported temperature unit: " + fromUnit);
        };
        return switch (toUnit) {
            case "celsius" -> celsius;
            case "fahrenheit" -> celsiusToFahrenheit(celsius);
            case "kelvin" -> celsius + 273.15;
            default -> throw new IllegalArgumentException("Unsupported temperature unit: " + toUnit);
        };
    }

    public boolean isExtremeTemperature(double celsius) {
        return celsius < -40 || celsius > 50;
    }
}

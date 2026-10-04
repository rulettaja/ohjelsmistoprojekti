public class Main {

    public static void main(String[] args) {
        System.out.println(convert(args));
    }

    static double convert(String[] args) {
        if (args == null || args.length != 2) {
            throw new IllegalArgumentException("Usage: java -jar app.jar <fahrenheit-to-celsius|celsius-to-fahrenheit> <value>");
        }

        double value = Double.parseDouble(args[1]);
        TemperatureConverter converter = new TemperatureConverter();
        return switch (args[0]) {
            case "fahrenheit-to-celsius" -> converter.fahrenheitToCelsius(value);
            case "celsius-to-fahrenheit" -> converter.celsiusToFahrenheit(value);
            default -> throw new IllegalArgumentException("Unsupported conversion: " + args[0]);
        };
    }
}
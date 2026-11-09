import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemperatureConverterTest {

    private final TemperatureConverter converter = new TemperatureConverter();

    @Test
    void fahrenheitToCelsius_multipleInputs() {
        assertEquals(0.0, converter.fahrenheitToCelsius(32), 0.0001);
        assertEquals(100.0, converter.fahrenheitToCelsius(212), 0.0001);
        assertEquals(-17.7778, converter.fahrenheitToCelsius(0), 0.0001);
    }

    @Test
    void celsiusToFahrenheit_multipleInputs() {
        assertEquals(32.0, converter.celsiusToFahrenheit(0), 0.0001);
        assertEquals(212.0, converter.celsiusToFahrenheit(100), 0.0001);
        assertEquals(-40.0, converter.celsiusToFahrenheit(-40), 0.0001);
    }

    @Test
    void convertSupportsAllTemperatureUnits() {
        assertEquals(0.0, converter.convert(32, "fahrenheit", "celsius"), 0.0001);
        assertEquals(273.15, converter.convert(0, "celsius", "kelvin"), 0.0001);
        assertEquals(32.0, converter.convert(273.15, "kelvin", "fahrenheit"), 0.0001);
    }

    @Test
    void convertRejectsUnsupportedUnits() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> converter.convert(1, "mile", "celsius"));
    }

    @Test
    void isExtremeTemperature_edgeCases() {
        assertTrue(converter.isExtremeTemperature(-41));
        assertTrue(converter.isExtremeTemperature(51));
        assertFalse(converter.isExtremeTemperature(-40));
        assertFalse(converter.isExtremeTemperature(50));
        assertFalse(converter.isExtremeTemperature(25));
    }
}

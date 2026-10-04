import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MainTest {

    @Test
    void convertSupportsBothDirections() {
        assertEquals(0.0, Main.convert(new String[]{"fahrenheit-to-celsius", "32"}), 0.0001);
        assertEquals(32.0, Main.convert(new String[]{"celsius-to-fahrenheit", "0"}), 0.0001);
    }

    @Test
    void convertRejectsInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> Main.convert(null));
        assertThrows(IllegalArgumentException.class, () -> Main.convert(new String[]{}));
        assertThrows(IllegalArgumentException.class, () -> Main.convert(new String[]{"kelvin", "0"}));
        assertThrows(NumberFormatException.class, () -> Main.convert(new String[]{"fahrenheit-to-celsius", "cold"}));
    }

    @Test
    void mainPrintsTheConvertedTemperature() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try (PrintStream capturedOutput = new PrintStream(output)) {
            System.setOut(capturedOutput);
            Main.main(new String[]{"fahrenheit-to-celsius", "32"});
        } finally {
            System.setOut(originalOutput);
        }

        assertEquals("0.0" + System.lineSeparator(), output.toString());
    }
}
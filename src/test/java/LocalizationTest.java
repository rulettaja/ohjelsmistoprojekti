import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalizationTest {

    @Test
    void localizedBundlesContainApplicationText() {
        List<Locale> locales = List.of(
                Locale.ENGLISH,
                Locale.forLanguageTag("fi"),
                Locale.forLanguageTag("sv"),
                Locale.JAPANESE,
                Locale.forLanguageTag("ar"));
        String englishTitle = ResourceBundle.getBundle("messages", Locale.ENGLISH).getString("app.title");

        for (Locale locale : locales) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
            assertTrue(bundle.containsKey("convert.button"));
            assertTrue(bundle.containsKey("input.invalid"));
            if (!Locale.ENGLISH.equals(locale)) {
                assertNotEquals(englishTitle, bundle.getString("app.title"));
            }
        }
    }
}
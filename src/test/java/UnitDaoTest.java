import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UnitDaoTest {
    private UnitDao dao;

    @BeforeEach
    void setUp() throws SQLException {
        dao = new UnitDao("jdbc:h2:mem:unit-dao-" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
    }

    @Test
    void returnsLocalizedUnits() throws SQLException {
        List<LocalizedUnit> units = dao.getUnits("temperature", "ja");

        assertEquals(3, units.size());
        assertEquals("摂氏", units.get(0).name());
        assertEquals("摂氏温度", units.get(0).description());
    }

    @Test
    void containsEachUnitCategoryInEveryLanguage() throws SQLException {
        for (String category : List.of("temperature", "speed", "distance", "time")) {
            for (String language : List.of("en", "fi", "sv", "ja", "ar")) {
                assertEquals(3, dao.getUnits(category, language).size(), category + " / " + language);
            }
        }
    }

    @Test
    void fallsBackToEnglishWhenTranslationIsMissing() throws SQLException {
        List<LocalizedUnit> units = dao.getUnits("temperature", "zz");

        assertEquals("Celsius", units.get(0).name());
        assertEquals("Temperature in degrees Celsius", units.get(0).description());
    }

    @Test
    void savesAndReturnsRecentConversions() throws SQLException {
        for (int value = 1; value <= 6; value++) {
            dao.saveConversion(value, "fahrenheit", "celsius", value - 32,
                    "en", value);
        }

        List<ConversionRecord> conversions = dao.getRecentConversions("ja", 5);

        assertEquals(5, conversions.size());
        assertEquals(6, conversions.get(0).value());
        assertEquals("華氏", conversions.get(0).fromUnit());
        assertEquals("摂氏", conversions.get(0).toUnit());
        assertThrows(IllegalArgumentException.class, () -> dao.getRecentConversions("en", 0));
    }
}
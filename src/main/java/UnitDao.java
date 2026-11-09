import org.h2.tools.RunScript;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class UnitDao {
    private static final String DEFAULT_URL = "jdbc:h2:file:./data/temperature-converter";
    private final String jdbcUrl;

    public UnitDao() throws SQLException {
        this(System.getenv().getOrDefault("TEMPERATURE_DB_URL", DEFAULT_URL));
    }

    public UnitDao(String jdbcUrl) throws SQLException {
        this.jdbcUrl = jdbcUrl;
        if (jdbcUrl.startsWith("jdbc:h2:file:./data/")) {
            try {
                Files.createDirectories(Path.of("data"));
            } catch (IOException exception) {
                throw new SQLException("Unable to create the database directory", exception);
            }
        }
        initialize();
    }

    public List<LocalizedUnit> getUnits(String category, String language) throws SQLException {
        String sql = "SELECT u.unit_key, u.category, " +
                "COALESCE(selected.name, english.name) AS name, " +
                "COALESCE(selected.description, english.description) AS description " +
                "FROM unit u " +
                "JOIN unit_translation english ON english.unit_id = u.id AND english.lang = 'en' " +
                "LEFT JOIN unit_translation selected ON selected.unit_id = u.id AND selected.lang = ? " +
                "WHERE u.category = ? ORDER BY u.id";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, language);
            statement.setString(2, category);
            try (ResultSet results = statement.executeQuery()) {
                List<LocalizedUnit> units = new ArrayList<>();
                while (results.next()) {
                    units.add(new LocalizedUnit(results.getString("unit_key"),
                            results.getString("category"), results.getString("name"),
                            results.getString("description")));
                }
                return units;
            }
        }
    }

    public void saveConversion(double value, String fromUnit, String toUnit, double result,
                               String language, long elapsedMillis) throws SQLException {
        String sql = "INSERT INTO calculation " +
                "(input_value, from_unit_id, to_unit_id, result, lang) " +
                "VALUES (?, (SELECT id FROM unit WHERE unit_key = ?), " +
                "(SELECT id FROM unit WHERE unit_key = ?), ?, ?)";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setDouble(1, value);
            statement.setString(2, fromUnit);
            statement.setString(3, toUnit);
            statement.setDouble(4, result);
            statement.setString(5, language);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("The database did not return a calculation id");
                }
                try (PreparedStatement timeStatement = connection.prepareStatement(
                        "INSERT INTO time_measurement (calculation_id, elapsed_millis) VALUES (?, ?)")) {
                    timeStatement.setLong(1, keys.getLong(1));
                    timeStatement.setLong(2, elapsedMillis);
                    timeStatement.executeUpdate();
                }
            }
        }
    }

    public List<ConversionRecord> getRecentConversions(String language, int limit) throws SQLException {
        if (limit < 1) {
            throw new IllegalArgumentException("limit must be positive");
        }
        String sql = "SELECT c.input_value, COALESCE(from_selected.name, from_english.name) AS from_name, " +
                "COALESCE(to_selected.name, to_english.name) AS to_name, c.result, c.lang, c.created_at " +
                "FROM calculation c " +
                "JOIN unit from_unit ON from_unit.id = c.from_unit_id " +
                "JOIN unit to_unit ON to_unit.id = c.to_unit_id " +
                "JOIN unit_translation from_english ON from_english.unit_id = from_unit.id AND from_english.lang = 'en' " +
                "JOIN unit_translation to_english ON to_english.unit_id = to_unit.id AND to_english.lang = 'en' " +
                "LEFT JOIN unit_translation from_selected ON from_selected.unit_id = from_unit.id AND from_selected.lang = ? " +
                "LEFT JOIN unit_translation to_selected ON to_selected.unit_id = to_unit.id AND to_selected.lang = ? " +
                "ORDER BY c.created_at DESC, c.id DESC LIMIT ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, language);
            statement.setString(2, language);
            statement.setInt(3, limit);
            try (ResultSet results = statement.executeQuery()) {
                List<ConversionRecord> conversions = new ArrayList<>();
                while (results.next()) {
                    conversions.add(new ConversionRecord(results.getDouble("input_value"),
                            results.getString("from_name"), results.getString("to_name"),
                            results.getDouble("result"), results.getString("lang"),
                            results.getObject("created_at", OffsetDateTime.class)));
                }
                return conversions;
            }
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, "sa", "");
    }

    private void initialize() throws SQLException {
        InputStream script = UnitDao.class.getResourceAsStream("/init.sql");
        if (script == null) {
            throw new SQLException("Database initialization script /init.sql was not found");
        }
        try (Connection connection = connect();
             InputStreamReader reader = new InputStreamReader(script, StandardCharsets.UTF_8)) {
            RunScript.execute(connection, reader);
        }
    }
}
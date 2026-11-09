import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

public class TemperatureConverterController implements Initializable {

    @FXML private VBox root;
    @FXML private Label titleLabel;
    @FXML private Label languageLabel;
    @FXML private ComboBox<String> languageChoice;
    @FXML private Label temperatureLabel;
    @FXML private TextField inputField;
        @FXML private Button convertButton;
        @FXML private Label fromUnitLabel;
        @FXML private ComboBox<LocalizedUnit> fromUnitChoice;
        @FXML private Label toUnitLabel;
        @FXML private ComboBox<LocalizedUnit> toUnitChoice;
        @FXML private Label unitDescriptionLabel;
    @FXML private Label resultLabel;
    @FXML private Label rangeLabel;
        @FXML private Label historyLabel;
        @FXML private ListView<String> historyList;

    private final TemperatureConverter converter = new TemperatureConverter();
        private UnitDao unitDao;
    private ResourceBundle messages;
        private boolean initialized;
    private static final Locale[] LOCALES = {
            Locale.ENGLISH, Locale.forLanguageTag("fi"), Locale.forLanguageTag("sv"),
            Locale.JAPANESE, Locale.forLanguageTag("ar")
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        messages = resources;
                try {
                        unitDao = new UnitDao();
                } catch (SQLException exception) {
                        resultLabel.setText(messages.getString("database.error"));
                }
        languageChoice.getItems().setAll(
                messages.getString("language.english"),
                messages.getString("language.finnish"),
                messages.getString("language.swedish"),
                messages.getString("language.japanese"),
                messages.getString("language.arabic"));
        languageChoice.getSelectionModel().select(0);
        inputField.setPromptText(messages.getString("temperature.prompt"));
        rangeLabel.setText("");
        initialized = true;
        updateLanguage(0);
    }

    @FXML
    private void changeLanguage() {
                if (!initialized) {
                        return;
                }
        int languageIndex = languageChoice.getSelectionModel().getSelectedIndex();
        if (languageIndex < 0) {
            return;
        }
                updateLanguage(languageIndex);
        }

        private void updateLanguage(int languageIndex) {
                String fromKey = selectedKey(fromUnitChoice);
                String toKey = selectedKey(toUnitChoice);
        messages = ResourceBundle.getBundle("messages", LOCALES[languageIndex]);
        resultLabel.setText(messages.getString("result.empty"));
        rangeLabel.setText("");
        titleLabel.setText(messages.getString("app.title"));
        languageLabel.setText(messages.getString("language.label"));
        temperatureLabel.setText(messages.getString("temperature.label"));
                fromUnitLabel.setText(messages.getString("unit.from"));
                toUnitLabel.setText(messages.getString("unit.to"));
                historyLabel.setText(messages.getString("history.label"));
                convertButton.setText(messages.getString("convert.button"));
        inputField.setPromptText(messages.getString("temperature.prompt"));
        root.setNodeOrientation(languageIndex == 4
                ? NodeOrientation.RIGHT_TO_LEFT
                : NodeOrientation.LEFT_TO_RIGHT);
                if (root.getScene() != null && root.getScene().getWindow() instanceof Stage stage) {
                        stage.setTitle(messages.getString("app.title"));
                }
                if (unitDao != null) {
                        try {
                                var units = unitDao.getUnits("temperature", LOCALES[languageIndex].getLanguage());
                                fromUnitChoice.getItems().setAll(units);
                                toUnitChoice.getItems().setAll(units);
                                selectUnit(fromUnitChoice, fromKey);
                                selectUnit(toUnitChoice, toKey);
                                updateTargetDescription();
                                refreshHistory(LOCALES[languageIndex].getLanguage());
                        } catch (SQLException exception) {
                                resultLabel.setText(messages.getString("database.error"));
                        }
                }
    }

        @FXML
        private void updateTargetDescription() {
                LocalizedUnit unit = toUnitChoice.getSelectionModel().getSelectedItem();
                unitDescriptionLabel.setText(unit == null ? "" : unit.description());
        }

    @FXML
    private void convertTemperature() {
        try {
            double value = Double.parseDouble(inputField.getText().trim());
                        LocalizedUnit fromUnit = fromUnitChoice.getSelectionModel().getSelectedItem();
                        LocalizedUnit toUnit = toUnitChoice.getSelectionModel().getSelectedItem();
                        if (fromUnit == null || toUnit == null) {
                                resultLabel.setText(messages.getString("database.error"));
                                return;
                        }
                        long startedAt = System.nanoTime();
                        double converted = converter.convert(value, fromUnit.key(), toUnit.key());
                        double celsius = converter.convert(value, fromUnit.key(), "celsius");

            resultLabel.setText(String.format(Locale.ROOT,
                                        messages.getString("result.value"), converted, toUnit.name()));
            rangeLabel.setText(messages.getString(converter.isExtremeTemperature(celsius)
                    ? "range.extreme"
                    : "range.normal"));
                        unitDao.saveConversion(value, fromUnit.key(), toUnit.key(), converted,
                                        LOCALES[languageChoice.getSelectionModel().getSelectedIndex()].getLanguage(),
                                        (System.nanoTime() - startedAt) / 1_000_000);
                        refreshHistory(LOCALES[languageChoice.getSelectionModel().getSelectedIndex()].getLanguage());
        } catch (NumberFormatException exception) {
            resultLabel.setText(messages.getString("input.invalid"));
            rangeLabel.setText("");
                } catch (SQLException exception) {
                        resultLabel.setText(messages.getString("database.error"));
        }
    }

        private void refreshHistory(String language) throws SQLException {
                historyList.getItems().setAll(unitDao.getRecentConversions(language, 5).stream()
                                .map(conversion -> String.format(Locale.ROOT, "%.2f %s -> %.2f %s",
                                                conversion.value(), conversion.fromUnit(), conversion.result(), conversion.toUnit()))
                                .toList());
        }

        private String selectedKey(ComboBox<LocalizedUnit> choice) {
                LocalizedUnit selected = choice.getSelectionModel().getSelectedItem();
                return selected == null ? null : selected.key();
        }

        private void selectUnit(ComboBox<LocalizedUnit> choice, String key) {
                if (key != null) {
                        choice.getItems().stream().filter(unit -> unit.key().equals(key)).findFirst()
                                        .ifPresent(unit -> choice.getSelectionModel().select(unit));
                }
                if (choice.getSelectionModel().getSelectedItem() == null && !choice.getItems().isEmpty()) {
                        choice.getSelectionModel().selectFirst();
                }
        }
}
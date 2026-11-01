import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class TemperatureConverterController implements Initializable {

    @FXML private VBox root;
    @FXML private Label titleLabel;
    @FXML private Label languageLabel;
    @FXML private ComboBox<String> languageChoice;
    @FXML private Label conversionLabel;
    @FXML private ComboBox<String> conversionChoice;
    @FXML private Label temperatureLabel;
    @FXML private TextField inputField;
    @FXML private Label resultLabel;
    @FXML private Label rangeLabel;

    private final TemperatureConverter converter = new TemperatureConverter();
    private ResourceBundle messages;
        private boolean initialized;
    private static final Locale[] LOCALES = {
            Locale.ENGLISH, Locale.forLanguageTag("fi"), Locale.forLanguageTag("sv"),
            Locale.JAPANESE, Locale.forLanguageTag("ar")
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        messages = resources;
        languageChoice.getItems().setAll(
                messages.getString("language.english"),
                messages.getString("language.finnish"),
                messages.getString("language.swedish"),
                messages.getString("language.japanese"),
                messages.getString("language.arabic"));
        languageChoice.getSelectionModel().select(0);
        conversionChoice.getItems().setAll(
                messages.getString("conversion.fahrenheitToCelsius"),
                messages.getString("conversion.celsiusToFahrenheit"));
        conversionChoice.getSelectionModel().select(0);
        inputField.setPromptText(messages.getString("temperature.prompt"));
        rangeLabel.setText("");
        initialized = true;
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

        int conversionIndex = conversionChoice.getSelectionModel().getSelectedIndex();
        messages = ResourceBundle.getBundle("messages", LOCALES[languageIndex]);
        titleLabel.setText(messages.getString("app.title"));
        languageLabel.setText(messages.getString("language.label"));
        conversionLabel.setText(messages.getString("conversion.label"));
        temperatureLabel.setText(messages.getString("temperature.label"));
        conversionChoice.getItems().setAll(
                messages.getString("conversion.fahrenheitToCelsius"),
                messages.getString("conversion.celsiusToFahrenheit"));
        conversionChoice.getSelectionModel().select(conversionIndex);
        inputField.setPromptText(messages.getString("temperature.prompt"));
        root.setNodeOrientation(languageIndex == 4
                ? NodeOrientation.RIGHT_TO_LEFT
                : NodeOrientation.LEFT_TO_RIGHT);
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setTitle(messages.getString("app.title"));
        resultLabel.setText(messages.getString("result.empty"));
        rangeLabel.setText("");
    }

    @FXML
    private void convertTemperature() {
        try {
            double value = Double.parseDouble(inputField.getText().trim());
            boolean fahrenheitToCelsius = conversionChoice.getSelectionModel().getSelectedIndex() == 0;
            double converted = fahrenheitToCelsius
                    ? converter.fahrenheitToCelsius(value)
                    : converter.celsiusToFahrenheit(value);
            double celsius = fahrenheitToCelsius ? converted : value;
            String unit = fahrenheitToCelsius ? "°C" : "°F";

            resultLabel.setText(String.format(Locale.ROOT,
                    messages.getString("result.value"), converted, unit));
            rangeLabel.setText(messages.getString(converter.isExtremeTemperature(celsius)
                    ? "range.extreme"
                    : "range.normal"));
        } catch (NumberFormatException exception) {
            resultLabel.setText(messages.getString("input.invalid"));
            rangeLabel.setText("");
        }
    }
}
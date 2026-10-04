import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Locale;

public class TemperatureConverterApp extends Application {

    private final TemperatureConverter converter = new TemperatureConverter();

    @Override
    public void start(Stage stage) {
        Label title = new Label("Temperature Converter");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        ComboBox<String> conversion = new ComboBox<>();
        conversion.getItems().addAll("Fahrenheit to Celsius", "Celsius to Fahrenheit");
        conversion.setValue("Fahrenheit to Celsius");
        conversion.setMaxWidth(Double.MAX_VALUE);

        TextField input = new TextField("32");
        input.setPromptText("Enter a temperature");

        Label result = new Label("Result: -");
        result.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label range = new Label(" ");
        Button convertButton = new Button("Convert");
        convertButton.setDefaultButton(true);
        convertButton.setOnAction(event -> convertTemperature(input, conversion, result, range));

        VBox root = new VBox(14, title, new Label("Conversion"), conversion,
                new Label("Temperature"), input, convertButton, result, range);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #f3f5f4; -fx-font-size: 14px;");

        stage.setTitle("Temperature Converter");
        stage.setScene(new Scene(root, 380, 350));
        stage.setMinWidth(340);
        stage.setMinHeight(330);
        stage.show();
    }

    private void convertTemperature(TextField input, ComboBox<String> conversion, Label result, Label range) {
        try {
            double value = Double.parseDouble(input.getText().trim());
            boolean fahrenheitToCelsius = "Fahrenheit to Celsius".equals(conversion.getValue());
            double converted = fahrenheitToCelsius
                    ? converter.fahrenheitToCelsius(value)
                    : converter.celsiusToFahrenheit(value);
            double celsius = fahrenheitToCelsius ? converted : value;
            String unit = fahrenheitToCelsius ? "°C" : "°F";

            result.setText(String.format(Locale.ROOT, "Result: %.2f %s", converted, unit));
            range.setText(converter.isExtremeTemperature(celsius)
                    ? "Extreme temperature"
                    : "Within the normal range");
        } catch (NumberFormatException exception) {
            result.setText("Enter a valid number.");
            range.setText(" ");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
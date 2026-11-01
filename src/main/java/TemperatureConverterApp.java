import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class TemperatureConverterApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/TemperatureConverter.fxml"),
            ResourceBundle.getBundle("messages", Locale.ENGLISH));
        Parent root = loader.load();
        stage.setTitle(ResourceBundle.getBundle("messages", Locale.ENGLISH).getString("app.title"));
        stage.setScene(new Scene(root, 390, 390));
        stage.setMinWidth(350);
        stage.setMinHeight(360);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
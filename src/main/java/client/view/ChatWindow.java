package client.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class ChatWindow extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("Chat.fxml")));
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle(this.getClass().getSimpleName());
        stage.show();

        stage.setOnCloseRequest(we -> System.exit(0));
    }

    public static void main(String[] args) {
        launch(args);
    }
}

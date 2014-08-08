package app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import player.VLCUtil;

public class VLCPlayerLauncher extends Application {
    private FXMLLoader fxmlLoader;
    private Stage stage;
    private AppController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        fxmlLoader = new FXMLLoader();

        Parent root = (Parent) fxmlLoader.load(getClass().getResource("/fxml/scene.fxml").openStream());
        controller = fxmlLoader.getController();
        final Scene scene = new Scene(root);

        stage = new Stage();
        stage.setTitle("Java FX player using VLC");
        stage.setScene(scene);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
            }
        });

        stage.show();

        VLCUtil.discover();
    }

    @Override
    public void stop() {
        controller.stopMedia();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

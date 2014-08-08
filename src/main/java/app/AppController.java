package app;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import player.VideoPlayer;


public class AppController {
    @FXML
    public BorderPane pane;

    @FXML
    public GridPane gridPane;

    private VideoPlayer player;

    @FXML
    public void playMedia() {
        player = new VideoPlayer("test");
        gridPane.getChildren().add(player);
        player.play("YOUR_MEDIA_LINK");
        player.setVolume(1);
    }

    @FXML
    public void stopMedia() {
        if (player != null) {
            player.release();
        }
    }
}

package vafilonov.msd;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vafilonov.msd.controllers.MainSceneController;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        MainSceneController controller = new MainSceneController();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainScene.fxml"));
        Scene scene = new Scene(root, 600, 400);

        stage.setScene(scene);
        stage.show();
     }
}

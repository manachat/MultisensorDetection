package vafilonov.msd;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vafilonov.msd.controllers.MainSceneController;
import weka.core.SerializationHelper;

import java.awt.Toolkit;
import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Properties prop = new Properties();
        final String file = "appconfig.xml";
        try (var propStream = new FileInputStream(file)) {
            prop.loadFromXML(propStream);
        } catch (IOException ioex) {
            System.err.println("Couldn't load configuration file.");
            System.exit(-1);
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/MainScene.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, screenSize.getWidth(), screenSize.getHeight());
        stage.setScene(scene);

        MainSceneController controller = loader.getController();
        controller.postInitialize(stage, prop);

        stage.show();
     }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
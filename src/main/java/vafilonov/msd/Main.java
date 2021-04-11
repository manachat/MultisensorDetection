package vafilonov.msd;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vafilonov.msd.controllers.MainSceneController;
import weka.classifiers.trees.RandomForest;
import weka.core.SerializationHelper;

import java.awt.Toolkit;
import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main extends Application {

    public static Scene scene;
    public static Stage stage;

    public static String forestPath;

    public static void main(String[] args) {
        Properties prop = new Properties();
        final String file = "app.config";
        try (var propStream = new FileInputStream(file)) {
            prop.load(propStream);
            System.out.println(SerializationHelper.SERIAL_VERSION_UID);
        } catch (IOException ioex) {
            System.err.println("Couldn't load configuration file.");
            System.exit(-1);
        }

        System.out.println(prop.getProperty("app.workdir"));

        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        forestPath = getClass().getResource("/models/random_forest_comb.model").getPath();
        System.out.println(forestPath);
        Main.stage = stage;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/MainScene.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, screenSize.getWidth() * 3 / 4, screenSize.getHeight() * 3 / 4);
        Main.scene = scene;
        stage.setScene(scene);

        ((MainSceneController) loader.getController()).postInitialize();

        stage.show();
     }
}
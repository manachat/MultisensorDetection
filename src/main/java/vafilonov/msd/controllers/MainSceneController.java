package vafilonov.msd.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;


public class MainSceneController {

    @FXML
    private Label label;

    @FXML
    private Button button;

    @FXML
    void buttonClickHandler(MouseEvent e) {
        label.setText("Crazy son of a bitch, you did it!");
    }

}

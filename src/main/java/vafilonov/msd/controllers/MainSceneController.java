package vafilonov.msd.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import vafilonov.msd.Main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainSceneController {


    private  ComboBox[] boxes;

    @FXML
    private Button fileChooseT1;

    @FXML
    private Button fileChooseT2;

    @FXML
    private VBox vBoxT1;

    @FXML
    private VBox vBoxT2;

    @FXML
    private ImageView view;


    @FXML
    private Button button;

    @FXML
    public void initialize() {

    }

    public void postInitialize() {

        Main.scene.widthProperty().addListener(value -> {
            view.setFitWidth(Main.scene.getWidth() - button.getWidth());
        });
        Main.scene.heightProperty().addListener(value -> {
            view.setFitHeight(Main.scene.getHeight());
        });

        initializeComboBoxes();

    }

    @SuppressWarnings("unchecked")
    private void initializeComboBoxes() {
        // set presentation for combo boxes
        // 26 is bands number in total
        boxes = new ComboBox[26];

        /*
            Register all boxes in arr
            8A BANDS ARE PLACED AT THE END OF BOXES
         */
        int index = 0;
        for (var child : vBoxT1.getChildren()) {
            if (child instanceof ComboBox) {
                if (child.getId().equals("b8aComboBoxT1")) {
                    boxes[12] = (ComboBox<File>) child;
                } else {
                    boxes[index] = (ComboBox<File>) child;
                    index++;
                }
            }
        }
        index++;
        for (var child : vBoxT2.getChildren()) {
            if (child instanceof ComboBox) {
                if (child.getId().equals("b8aComboBoxT2")) {
                    boxes[25] = (ComboBox<File>) child;
                } else {
                    boxes[index] = (ComboBox<File>) child;
                    index++;
                }
            }
        }

        for (ComboBox<File> b : boxes) {
            b.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(File item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });
        }
    }

    @FXML
    private void fileChooseT1Click(MouseEvent e) {
        final FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose files");

        var files = chooser.showOpenMultipleDialog(Main.stage);

        if (files == null) {
            return;
        }

        // first half of boxes are for t1
        for (int i = 0; i < boxes.length / 2 ; i++) {
            @SuppressWarnings("unchecked")
            final ComboBox<File> box  = (ComboBox<File>) boxes[i];
            box.getItems().addAll(files);
        }

    }


    @FXML
    private void buttonClickHandler(MouseEvent e) throws Exception {
        String path = "/home/vfilonov/programming/geodata/Ramenki_jp.jp2";


    }





    @FXML
    private void saveMenuHandler(ActionEvent event) {

    }

    @FXML
    private void saveAsMenuHandler(ActionEvent event) {

    }

    @FXML
    private void infoMenuHandler(ActionEvent event) {

    }

    @FXML
    private void loadMenuHandler(ActionEvent event) {

    }

    @FXML
    private void aboutMenuHandler(ActionEvent event) {

    }



}

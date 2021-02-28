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
import vafilonov.msd.utils.Renderer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainSceneController {

    private static final int MENU_BAR_PREF_HEIGHT = 25;

    @FXML
    private Button fileChooseT1;

    @FXML
    private Button fileChooseT2;

    @FXML
    private VBox vBoxT1;

    @FXML
    private VBox vBoxT2;

    @FXML
    private VBox controlsVBox;

    @FXML
    private VBox fileVBox;

    @FXML
    private ImageView view;

    @FXML
    private Button button;



    @FXML
    private ComboBox<File> b1ComboBoxT1;

    @FXML
    private ComboBox<File> b2ComboBoxT1;

    @FXML
    private ComboBox<File> b3ComboBoxT1;

    @FXML
    private ComboBox<File> b4ComboBoxT1;

    @FXML
    private ComboBox<File> b5ComboBoxT1;

    @FXML
    private ComboBox<File> b6ComboBoxT1;

    @FXML
    private ComboBox<File> b7ComboBoxT1;

    @FXML
    private ComboBox<File> b8ComboBoxT1;

    @FXML
    private ComboBox<File> b8aComboBoxT1;

    @FXML
    private ComboBox<File> b9ComboBoxT1;

    @FXML
    private ComboBox<File> b10ComboBoxT1;

    @FXML
    private ComboBox<File> b11ComboBoxT1;

    @FXML
    private ComboBox<File> b12ComboBoxT1;

    private final ArrayList<ComboBox<File>> t1Boxes = new ArrayList<>(13);



    @FXML
    private ComboBox<File> b1ComboBoxT2;

    @FXML
    private ComboBox<File> b2ComboBoxT2;

    @FXML
    private ComboBox<File> b3ComboBoxT2;

    @FXML
    private ComboBox<File> b4ComboBoxT2;

    @FXML
    private ComboBox<File> b5ComboBoxT2;

    @FXML
    private ComboBox<File> b6ComboBoxT2;

    @FXML
    private ComboBox<File> b7ComboBoxT2;

    @FXML
    private ComboBox<File> b8ComboBoxT2;

    @FXML
    private ComboBox<File> b8aComboBoxT2;

    @FXML
    private ComboBox<File> b9ComboBoxT2;

    @FXML
    private ComboBox<File> b10ComboBoxT2;

    @FXML
    private ComboBox<File> b11ComboBoxT2;

    @FXML
    private ComboBox<File> b12ComboBoxT2;

    private final ArrayList<ComboBox<File>> t2Boxes = new ArrayList<>(13);


    @FXML
    public void initialize() {
        initializeComboBoxes();
    }

    public void postInitialize() {

        Main.scene.widthProperty().addListener(value -> {

            view.setFitWidth(Main.scene.getWidth() - fileVBox.getPrefWidth() - controlsVBox.getPrefWidth());
        });
        Main.scene.heightProperty().addListener(value -> {
            view.setFitHeight(Main.scene.getHeight() - MENU_BAR_PREF_HEIGHT);
            fileVBox.setPrefHeight(Main.scene.getHeight());
            controlsVBox.setPrefHeight(Main.scene.getHeight());
        });
    }


    /**
     * Sets presentation options for combo boxes
     */
    @SuppressWarnings("unchecked")
    private void initializeComboBoxes() {

        for (var child : vBoxT1.getChildren()) {
            if (child instanceof ComboBox) {
                ComboBox<File> b = (ComboBox<File>) child;
                t1Boxes.add(b);
                b.setButtonCell(new ListCell<>() {
                    @Override
                    protected void updateItem(File item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else if (item == null) {
                            setText("--");
                        } else {
                            setText(item.getName());
                        }
                    }
                });
            }
        }

        for (var child : vBoxT2.getChildren()) {
            if (child instanceof ComboBox) {
                ComboBox<File> b = (ComboBox<File>) child;
                t2Boxes.add(b);
                b.setButtonCell(new ListCell<>() {
                    @Override
                    protected void updateItem(File item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else if (item == null) {
                            setText("--");
                        }else {
                            setText(item.getName());
                        }
                    }
                });
            }
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

        for (int i = 0; i < t1Boxes.size(); i++) {
            ComboBox<File> cb = t1Boxes.get(i);
            cb.getItems().clear();
            cb.getItems().addAll(files);
            cb.getItems().add(null);
            if (i < files.size()) {
                cb.setValue(files.get(i));
            }
        }
    }

    @FXML
    private void fileChooseT2Click(MouseEvent e) {
        final FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose files");


        var files = chooser.showOpenMultipleDialog(Main.stage);

        if (files == null) {
            return;
        }

        for (int i = 0; i < t2Boxes.size(); i++) {
            ComboBox<File> cb = t2Boxes.get(i);

            cb.getItems().clear();
            cb.getItems().addAll(files);
            cb.getItems().add(null);
            if (i < files.size()) {
                cb.setValue(files.get(i));
            }
        }
    }


    @FXML
    private void buttonClickHandler(MouseEvent e) throws Exception {
        String blue = b2ComboBoxT1.getValue().getPath();
        String green = b3ComboBoxT1.getValue().getPath();
        String red = b4ComboBoxT1.getValue().getPath();

        int[] pixels = Renderer.renderRGB(red, green, blue);

        int x = pixels[0];
        int y = pixels[1];

        WritableImage img = new WritableImage(x, y);
        PixelWriter writer = img.getPixelWriter();

        writer.setPixels(0, 0, x, y, PixelFormat.getIntArgbInstance(), pixels, 2, x);

        view.setImage(img);

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

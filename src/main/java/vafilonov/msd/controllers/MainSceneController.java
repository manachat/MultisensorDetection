package vafilonov.msd.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import vafilonov.msd.Main;
import vafilonov.msd.utils.Renderer;

import java.io.File;
import java.util.ArrayList;


public class MainSceneController {

    private static final int MENU_BAR_PREF_HEIGHT = 25;

    @FXML
    private VBox toolsVBox;

    @FXML
    private Button renderRgbButton;

    @FXML
    private TextField classMarkTextField;

    @FXML
    private Button createDatasetButton;


    @FXML
    private ImageView view;

    /* FILE MANAGEMENT VBOX */

    @FXML
    private VBox filesVBox;

    /* file choose for T1*/

    @FXML
    private VBox vBoxT1;

    @FXML
    private Button fileChooseT1;

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


    /* File choose for T2*/

    @FXML
    private VBox vBoxT2;

    @FXML
    private Button fileChooseT2;

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

            view.setFitWidth(Main.scene.getWidth() - filesVBox.getPrefWidth() - toolsVBox.getPrefWidth());
        });

        filesVBox.prefHeightProperty().bind(Main.scene.heightProperty());
        toolsVBox.prefHeightProperty().bind(Main.scene.heightProperty());

        Main.scene.heightProperty().addListener(value -> {
            view.setFitHeight(Main.scene.getHeight() - MENU_BAR_PREF_HEIGHT);
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
    private void renderRGBClickHandler(MouseEvent e) throws Exception {
        String blue = b2ComboBoxT1.getValue().getPath();
        String green = b3ComboBoxT1.getValue().getPath();
        String red = b4ComboBoxT1.getValue().getPath();

        int[] pixels = Renderer.renderRGB(red, green, blue);
        if (pixels == null)
            return;

        int x = pixels[0];
        int y = pixels[1];


        WritableImage img = new WritableImage(x, y);
        PixelWriter writer = img.getPixelWriter();

        writer.setPixels(0, 0, x, y, PixelFormat.getIntArgbInstance(), pixels, 2, x);
        view.setImage(img);

        pixels = null;
        renderRgbButton.setText("finished");
        Runtime.getRuntime().gc();

    }

    @FXML
    private void createDatasetClickHandler(MouseEvent e) {
        int mark = -1;
        if (classMarkTextField.getText() == null || classMarkTextField.getText().isBlank()) {
            showAlertMessage("Error", "Class mark not set.");
            return;
        }
        try {
            mark = Integer.parseInt(classMarkTextField.getText());
        } catch (NumberFormatException numEx) {
            showAlertMessage("Error", "Invalid integer format");
            return;
        }
        String[] paths = new String[t1Boxes.size()];
        int i = 0;
        for (var box : t1Boxes) {
            if (box == null) {
                showAlertMessage("Error", "Not all bands present.");
                return;
            }
            paths[i++] = box.getValue().getPath();
        }
        if (1<2) {
            throw new RuntimeException("Not implemented");
        }
        Renderer.createDataset(mark, "/home/vfilonov/all_2020.csv", paths);// TODO remove
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


    /**
     * Helper method to construct dialog alert message
     * @param title title of message
     * @param content message itself
     */
    private void showAlertMessage(String title, String content) {
        Alert msg = new Alert(Alert.AlertType.ERROR, content, ButtonType.OK);
        msg.setTitle(title);
        msg.setHeaderText(null);
        msg.setGraphic(null);
        msg.show();
    }

}

package vafilonov.msd.controllers;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ListCell;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import vafilonov.msd.Main;
import vafilonov.msd.core.PixelClassifier;
import vafilonov.msd.core.RasterDataset;
import vafilonov.msd.core.renders.ClassifierRender;
import vafilonov.msd.core.renders.RGBRender;
import vafilonov.msd.core.sentinel2.Sentinel2PixelClassifier;
import vafilonov.msd.core.sentinel2.Sentinel2RasterDataset;
import vafilonov.msd.core.sentinel2.Sentinel2RasterTraverser;
import vafilonov.msd.core.sentinel2.utils.Constants;
import vafilonov.msd.core.sentinel2.utils.Sentinel2Band;
import vafilonov.msd.core.Renderer;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;


public class MainSceneController {

    private static final int MENU_BAR_PREF_HEIGHT = 25;

    private Path outputDatasetFilePath = null;

    @FXML
    private VBox toolsVBox;

    @FXML
    private Button renderRgbButton;

    @FXML
    private TextField classMarkTextField;

    @FXML
    private Button outputDatasetButton;

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

        //filesVBox.prefHeightProperty().bind(Main.scene.heightProperty());
        //toolsVBox.prefHeightProperty().bind(Main.scene.heightProperty());

        Main.scene.heightProperty().addListener(value -> {
            view.setFitHeight(Main.scene.getHeight() - MENU_BAR_PREF_HEIGHT);
            filesVBox.setPrefHeight(Main.scene.getHeight() - MENU_BAR_PREF_HEIGHT-10);
            toolsVBox.setPrefHeight(Main.scene.getHeight() - MENU_BAR_PREF_HEIGHT-10);
        });
    }


    /**
     * Sets presentation options for comboboxes.
     */
    private void initializeComboBoxes() {

        initBoxes(vBoxT1, t1Boxes);

        initBoxes(vBoxT2, t2Boxes);
    }

    /**
     * Initializes boxes' presentation mechanisms.
     * Adds boxes to specified collection.
     * Helper method for code deduplication.
     * @param vBox vBox with comboboxes
     * @param collection collection for boxes
     */
    @SuppressWarnings("unchecked")
    private void initBoxes(VBox vBox, ArrayList<ComboBox<File>> collection) {
        for (var child : vBox.getChildren()) {
            if (child instanceof ComboBox) {
                ComboBox<File> b = (ComboBox<File>) child;
                collection.add(b);
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
    }




    @FXML
    private void fileChooseT1Click(MouseEvent e) {
        chooseFilesForBoxes(t1Boxes);
    }

    @FXML
    private void fileChooseT2Click(MouseEvent e) {
        chooseFilesForBoxes(t2Boxes);
    }

    private void chooseFilesForBoxes(ArrayList<ComboBox<File>> boxes) {
        final FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose files");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Satellite-2 data (*.tif, *.jp2)", "*.jp2", "*.tif"));
        //chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Satellite-2 data: ", "*.jp2", "*.tif"));

        var files = chooser.showOpenMultipleDialog(Main.stage);

        if (files == null) {
            return;
        }

        for (int i = 0; i < boxes.size(); i++) {
            ComboBox<File> cb = boxes.get(i);
            cb.getItems().clear();
            cb.getItems().addAll(files);
            cb.getItems().add(null);
            if (i < files.size()) {
                cb.setValue(files.get(i));
            }
        }
    }


    @FXML
    private void renderRGBClickHandler(MouseEvent e) {
        if (t1Boxes.get(Sentinel2Band.B2.ordinal()).getValue() == null ||
                t1Boxes.get(Sentinel2Band.B3.ordinal()).getValue() == null ||
                t1Boxes.get(Sentinel2Band.B4.ordinal()).getValue() == null) {
            showAlertMessage("Error", "RGB bands (2,3,4) not set.");
            return;
        }
        String[] paths = new String[Constants.BANDS_NUM];
        int i = 0;
        for (ComboBox<File> b : t1Boxes) {
            if (b.getValue() != null)
                paths[i++] = b.getValue().getPath();
            else
                paths[i++] = null;
        }
        System.out.println(Arrays.toString(paths));
        Sentinel2RasterDataset set = Sentinel2RasterDataset.loadDataset(paths);
        RGBRender renderer = new RGBRender(set);
        Sentinel2RasterTraverser tr = new Sentinel2RasterTraverser();
        tr.traverseRaster(renderer, new RasterDataset[]{set}, renderer.getTraverseMask());

        final int[] pixels = renderer.getRaster();
        if (pixels == null)
            return;
        // width and height
        int x = renderer.getRasterWidth();
        int y = renderer.getRasterHeight();

        WritableImage img = new WritableImage(x, y);
        PixelWriter writer = img.getPixelWriter();
        writer.setPixels(0, 0, x, y, PixelFormat.getIntArgbInstance(), pixels, 0, x);
        view.setImage(img);
        view.setViewport(new Rectangle2D(0, 0, x, y));

        Runtime.getRuntime().gc();

    }

    @FXML
    private void renderInfraredClickHandler(MouseEvent e) {
        if (t1Boxes.get(Sentinel2Band.B8.ordinal()).getValue() == null ||
                t1Boxes.get(Sentinel2Band.B3.ordinal()).getValue() == null ||
                t1Boxes.get(Sentinel2Band.B4.ordinal()).getValue() == null) {
            showAlertMessage("Error", "RGB bands (2,3,4) not set.");
            return;
        }

        String blue = t1Boxes.get(Sentinel2Band.B3.ordinal()).getValue().getPath();
        String green = t1Boxes.get(Sentinel2Band.B4.ordinal()).getValue().getPath();
        String red = t1Boxes.get(Sentinel2Band.B8.ordinal()).getValue().getPath();

        final int[] pixels = Renderer.renderRGBLike(red, green, blue);
        if (pixels == null)
            return;
        // width and height
        int x = pixels[0];
        int y = pixels[1];

        WritableImage img = new WritableImage(x, y);
        PixelWriter writer = img.getPixelWriter();
        writer.setPixels(0, 0, x, y, PixelFormat.getIntArgbInstance(), pixels, 2, x);
        view.setImage(img);
        view.setViewport(new Rectangle2D(0, 0, x, y));
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

    @Deprecated
    @FXML
    private void classify(MouseEvent e) throws Exception {
        String[] paths1 = new String[t1Boxes.size()];
        String[] paths2 = new String[t2Boxes.size()];
        int i = 0;
        for (var box : t1Boxes) {
            if (box == null || box.getValue() == null) {
                showAlertMessage("Error", "Not all bands present.");
                return;
            }
            paths1[i++] = box.getValue().getPath();
        }
        i = 0;
        for (var box : t2Boxes) {
            if (box == null || box.getValue() == null) {
                showAlertMessage("Error", "Not all bands present.");
                return;
            }
            paths2[i++] = box.getValue().getPath();
        }
        // Main.class.getResource("/models/logistic_full.model").getPath()
        RasterDataset present = Sentinel2RasterDataset.loadDataset(paths1);
        RasterDataset past = Sentinel2RasterDataset.loadDataset(paths2);
        PixelClassifier classifierPresent = Sentinel2PixelClassifier.loadClassifier(Main.class.getResource("/models/svm_full20_arif.model").getPath());
        PixelClassifier classifierPast = Sentinel2PixelClassifier.loadClassifier(Main.class.getResource("/models/svm_full15_arif.model").getPath());
        ClassifierRender render = new ClassifierRender(present, new PixelClassifier[]{classifierPresent, classifierPast});
        Sentinel2RasterTraverser tr = new Sentinel2RasterTraverser();
        tr.traverseRaster(render, new RasterDataset[]{present, past}, render.getTraverseMask());

        int[] pixels = render.getRaster();

        if (pixels == null)
            return;
        // width and height
        int x = render.getRasterWidth();
        int y = render.getRasterHeight();

        WritableImage img = new WritableImage(x, y);
        PixelWriter writer = img.getPixelWriter();
        writer.setPixels(0, 0, x, y, PixelFormat.getIntArgbInstance(), pixels, 0, x);
        view.setImage(img);
        view.setViewport(new Rectangle2D(0, 0, x, y));
        present.delete();
        past.delete();

    }

}

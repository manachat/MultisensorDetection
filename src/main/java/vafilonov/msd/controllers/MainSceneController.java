package vafilonov.msd.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.embed.swing.SwingFXUtils;

import vafilonov.msd.core.PixelClassifier;
import vafilonov.msd.core.RasterDataset;
import vafilonov.msd.core.renders.*;
import vafilonov.msd.core.sentinel2.Sentinel2PixelClassifier;
import vafilonov.msd.core.sentinel2.Sentinel2RasterDataset;
import vafilonov.msd.core.RasterTraverser;
import vafilonov.msd.core.sentinel2.utils.Constants;
import vafilonov.msd.core.sentinel2.utils.Sentinel2Band;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;


public class MainSceneController {

    private static final int MENU_BAR_PREF_HEIGHT = 25;

    private Stage stage;

    private Scene scene;

    private Properties config;

    private RGBRender rgbCache;

    private InfraRender infraCache;

    private ShortWaveInfraredRender shortWaveCache;

    private AgricultureRender agricultureCache;

    private GeologyRenderer geologyCache;

    private ClassifierRender classifierCache;

    // file to save image
    private String savepath;

    @FXML
    private VBox toolsVBox;


    @FXML
    private ImageView view;

    @FXML
    private BorderPane viewBox;

    /* FILE MANAGEMENT VBOX */

    @FXML
    private VBox filesVBox;

    /* file choose for T1*/

    @FXML
    private VBox vBoxPresent;

    private final ArrayList<ComboBox<File>> t1Boxes = new ArrayList<>(13);


    /* File choose for T2*/

    @FXML
    private VBox vBoxPast;

    private final ArrayList<ComboBox<File>> t2Boxes = new ArrayList<>(13);


    @FXML
    public void initialize() {
        initializeComboBoxes();

    }

    public void postInitialize(Stage stage, Properties props) {
        this.stage = stage;
        config = props;
        scene = stage.getScene();

        scene.widthProperty().addListener(value -> {
            //view.setFitWidth(scene.getWidth() - filesVBox.getPrefWidth() - toolsVBox.getPrefWidth());

        });

        scene.heightProperty().addListener(value -> {
            //view.setFitHeight(scene.getHeight() - MENU_BAR_PREF_HEIGHT);

            filesVBox.setPrefHeight(scene.getHeight() - MENU_BAR_PREF_HEIGHT-10);
            toolsVBox.setPrefHeight(scene.getHeight() - MENU_BAR_PREF_HEIGHT-10);
        });
        view.fitWidthProperty().bind(viewBox.widthProperty());
        view.fitHeightProperty().bind(viewBox.heightProperty());
        view.setPreserveRatio(false);

        // mouse image drag-and-drop
        view.setOnMousePressed(e->{
            initX = e.getSceneX();
            initY = e.getSceneY();
            viewBox.setCursor(Cursor.CLOSED_HAND);
        });
        view.setOnMouseDragged(e->{
            viewBox.setCursor(Cursor.CLOSED_HAND);
            double moveX = initX - e.getSceneX();
            double moveY = initY - e.getSceneY();
            initRectX += moveX;
            initRectY += moveY;
            view.setViewport(new Rectangle2D(initRectX, initRectY, width/zoom, height/zoom));

            initX = e.getSceneX();
            initY = e.getSceneY();
        });
        view.setOnMouseReleased(e->{
            viewBox.setCursor(Cursor.OPEN_HAND);
        });

        // image zoom
        view.setOnScroll( e -> {
            double ratioX = e.getSceneX() - initRectX;
            double ratioY = e.getSceneY() - initRectY;

            double d = e.getDeltaY();
            if (d > 0) {
                zoom *= 1.1;
            } else if (d < 0){
                zoom /= 1.1;
            }

            zoom = Math.max(zoom, 0.5);
            zoom = Math.min(zoom, 10);

            initRectX = e.getSceneX() - ratioX;
            initRectY = e.getSceneY() - ratioY;
            view.setViewport(new Rectangle2D(initRectX, initRectY, width/zoom, height/zoom));
        });


    }

    double initX;
    double initY;
    double zoom;
    double initRectX;
    double initRectY;
    double width;
    double height;




    /**
     * Sets presentation options for comboboxes.
     */
    private void initializeComboBoxes() {

        initBoxes(vBoxPresent, t1Boxes);

        initBoxes(vBoxPast, t2Boxes);
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

    private void clearCache() {
        rgbCache = null;
        infraCache = null;
        agricultureCache = null;
        geologyCache = null;
        shortWaveCache = null;
        classifierCache = null;
    }

    private void chooseFilesForBoxes(ArrayList<ComboBox<File>> boxes) {
        final FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(config.getProperty("WORKDIR")));
        chooser.setTitle("Choose files");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Satellite-2 data (*.tif, *.jp2)", "*.jp2", "*.tif"));

        var files = chooser.showOpenMultipleDialog(stage);

        if (files == null) {
            return;
        }
        clearCache();

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

    private Sentinel2RasterDataset createPresentDataset() {
        return createDataset(t1Boxes);
    }

    private Sentinel2RasterDataset createPastDataset() {
        return createDataset(t2Boxes);
    }

    private Sentinel2RasterDataset createDataset(ArrayList<ComboBox<File>> comboBoxes) {

        String[] paths = new String[Constants.BANDS_NUM];
        int i = 0;
        for (ComboBox<File> b : comboBoxes) {
            if (b.getValue() != null)
                paths[i++] = b.getValue().getPath();
            else
                paths[i++] = null;
        }

        return Sentinel2RasterDataset.loadDataset(paths);
    }

    private void setRenderView(AbstractRender render) {
        final int[] pixels = render.getRaster();
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
        width = x;
        height = y;
        initRectX = 0;
        initRectY = 0;
        zoom = 1;
    }


    @FXML
    private void onRenderRGB(MouseEvent e) {
        if (t1Boxes.get(Sentinel2Band.B4.ordinal()).getValue() == null ||
                t1Boxes.get(Sentinel2Band.B3.ordinal()).getValue() == null ||
                t1Boxes.get(Sentinel2Band.B2.ordinal()).getValue() == null) {
            showAlertMessage("Error", "RGB bands (2,3,4) not set.");
            return;
        }

        if (rgbCache == null) {
            RasterDataset presentSet = createPresentDataset();
            RGBRender renderer = new RGBRender(presentSet);
            RasterTraverser.traverseRaster(renderer, new RasterDataset[]{presentSet}, renderer.getTraverseMask());
            presentSet.delete();
            rgbCache = renderer;
        }

        setRenderView(rgbCache);
    }

    @FXML
    private void onRenderInfrared(MouseEvent e) {
        if (t1Boxes.get(Sentinel2Band.B8.ordinal()).getValue() == null ||
                t1Boxes.get(Sentinel2Band.B4.ordinal()).getValue() == null ||
                t1Boxes.get(Sentinel2Band.B3.ordinal()).getValue() == null) {
            showAlertMessage("Error", "Infrared bands (8,4,3) not set.");
            return;
        }

        if (infraCache == null) {
            RasterDataset presentSet = createPresentDataset();
            InfraRender render = new InfraRender(presentSet);
            RasterTraverser.traverseRaster(render, new RasterDataset[]{presentSet}, render.getTraverseMask());
            presentSet.delete();
            infraCache = render;
        }
        setRenderView(infraCache);
    }

    @FXML
    private void onRenderShortWave(MouseEvent e) {
        if (t1Boxes.get(Sentinel2Band.B12.ordinal()).getValue() == null ||
                t1Boxes.get(Sentinel2Band.B8A.ordinal()).getValue() == null ||
                t1Boxes.get(Sentinel2Band.B4.ordinal()).getValue() == null) {
            showAlertMessage("Error", "Short Wave Infrared bands (12,8A,4) not set.");
            return;
        }

        if (shortWaveCache == null) {
            RasterDataset presentSet = createPresentDataset();
            ShortWaveInfraredRender render = new ShortWaveInfraredRender(presentSet);
            RasterTraverser.traverseRaster(render, new RasterDataset[]{presentSet}, render.getTraverseMask());
            presentSet.delete();
            shortWaveCache = render;
        }
        setRenderView(shortWaveCache);
    }

    @FXML
    private void onRenderAgriculture(MouseEvent e) {
        if (t1Boxes.get(Sentinel2Band.B11.ordinal()).getValue() == null ||
                t1Boxes.get(Sentinel2Band.B8.ordinal()).getValue() == null ||
                t1Boxes.get(Sentinel2Band.B2.ordinal()).getValue() == null) {
            showAlertMessage("Error", "Agriculture bands (11,8,2) not set.");
            return;
        }

        if (agricultureCache == null) {
            RasterDataset presentSet = createPresentDataset();
            AgricultureRender render = new AgricultureRender(presentSet);
            RasterTraverser.traverseRaster(render, new RasterDataset[]{presentSet}, render.getTraverseMask());
            presentSet.delete();
            agricultureCache = render;
        }
        setRenderView(agricultureCache);
    }

    @FXML
    private void onRenderGeology(MouseEvent e) {
        if (t1Boxes.get(Sentinel2Band.B12.ordinal()).getValue() == null ||
                t1Boxes.get(Sentinel2Band.B11.ordinal()).getValue() == null ||
                t1Boxes.get(Sentinel2Band.B2.ordinal()).getValue() == null) {
            showAlertMessage("Error", "Geology bands (12,11,2) not set.");
            return;
        }

        if (geologyCache == null) {
            RasterDataset presentSet = createPresentDataset();
            GeologyRenderer render = new GeologyRenderer(presentSet);
            RasterTraverser.traverseRaster(render, new RasterDataset[]{presentSet}, render.getTraverseMask());
            presentSet.delete();
            geologyCache = render;
        }
        setRenderView(geologyCache);
    }

    @FXML
    private void onClassify(MouseEvent e) {
        if (classifierCache == null) {
            PixelClassifier classifier;
            try {
                classifier = Sentinel2PixelClassifier.loadClassifier(config.getProperty("FULL_CLASSIFIER"));
            } catch (Exception ex) {
                showAlertMessage("Error", "Could not load classifier.");
                return;
            }

            RasterDataset presetSet = createPresentDataset();
            RasterDataset pastSet = createPastDataset();

            for (var band : presetSet.getBands()) {
                if (band == null) {
                    showAlertMessage("Error", "Not all bands are present in present set" );
                    presetSet.delete();
                    pastSet.delete();
                    return;
                }
            }
            for (var band : pastSet.getBands()) {
                if (band == null) {
                    showAlertMessage("Error", "Not all bands are present in past set" );
                    presetSet.delete();
                    pastSet.delete();
                    return;
                }
            }

            ClassifierRender render = new ClassifierRender(presetSet, classifier);
            RasterTraverser.traverseRaster(render, new RasterDataset[]{presetSet, pastSet}, render.getTraverseMask());
            presetSet.delete();
            pastSet.delete();
            classifierCache = render;
        }
        setRenderView(classifierCache);
    }


    @FXML
    private void saveMenuHandler(ActionEvent event) {
        if (savepath != null) {  // file path was set previously
            saveSession(savepath);  // try to save session
        } else {                  // file wasn't set previously
            saveAsMenuHandler(null);
        }
    }

    @FXML
    private void saveAsMenuHandler(ActionEvent event) {
        // choose filepath
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save session");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("IHR", "*.ihr"));
        File path = chooser.showSaveDialog(scene.getWindow());

        // user haven't aborted choice
        if (path != null) {
            if (saveSession(path.getPath())) {  // try to save session
                savepath = path.getPath();      //set filepath in case of success
            }
        }
    }

    @FXML
    private void infoMenuHandler(ActionEvent event) {
        showAlertMessage("Help",
                "Present: даанные за текущий (или более поздний) период.\n " +
                        "Past: данные за прошлый (или более ранний) период.\n" +
                        "Render RGB: отрисовка изображения в видимом спектре\n" +
                        "Render Infrared: отрисовка в инфракрасном спктре.\n" +
                        "Render SWIR: отрисовка в коротковолновом инфракрасном спктре.\n" +
                        "Render Agriculture: отрисовка c выделением растительности.\n" +
                        "Render Geology: отрисовка с выделением особенностей рельефа.\n" +
                        "Detect changes: сравнивает 2 изображения на предмет изменений.\n" +
                        "File: меню работы с файлами для сохранения изображений"
        );
    }


    @FXML
    private void aboutMenuHandler(ActionEvent event) {
        showAlertMessage("About", "Программа разработана в рамках выполения курсовой работы ОП ПИ НИУ ВШЭ\n " +
                "Исполнитель: \n Филонов Всеволод Андреевич\n группа БПИ185");
    }

    @FXML
    private void colormapMenuHandler(ActionEvent event) {

        Image img;
        try (var fin = new FileInputStream(config.getProperty("COLORMAP"))) {
            img = new Image(fin);
        } catch (IOException ioex) {
            showAlertMessage("Error.", "Colormap image not found");
            return;
        }
        GridPane grid = new GridPane();
        ImageView iv = new ImageView();
        iv.setFitWidth(500);
        iv.setFitHeight(500);
        grid.add(iv, 0, 0);
        iv.setImage(img);
        iv.setPreserveRatio(true);

        Stage stg = new Stage();
        Scene sc = new Scene(grid, 500, 500);
        iv.fitWidthProperty().bind(sc.widthProperty());
        iv.fitHeightProperty().bind(sc.heightProperty());
        stg.setScene(sc);
        stg.show();

    }

    /**
     * Helper method. saves session to .ihr serialization file
     * @param path path to file
     * @return true in case of success
     */
    private boolean saveSession(String path) {
        try { // open IO stream
            BufferedImage toSave = SwingFXUtils.fromFXImage(view.getImage(), null);
            ImageIO.write(toSave, "png", new File(path));
            return true;    // return success
        } catch (IOException ioex) {    // notify user in case of error
            showAlertMessage("Error.", "Can't save file.");
            return false;   // return failure
        }
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

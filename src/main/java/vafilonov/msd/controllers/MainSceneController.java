package vafilonov.msd.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.gdal.gdal.*;
import vafilonov.msd.Main;

import java.io.FileInputStream;
import java.util.Vector;


public class MainSceneController {


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

    }


    @FXML
    void buttonClickHandler(MouseEvent e) throws Exception {
        Dataset data = null;
        Band band = null;



        try {

        }

        Image im = new Image(new FileInputStream("/home/vfilonov/Pictures/inn.png"));
        view.setFitHeight(im.getRequestedHeight());
        view.setFitWidth(im.getRequestedWidth());
        view.setImage(im);



    }




    private void clipRasterByCoords() {
        // -projwin 37.140077458 55.991533828 37.814126275 55.73792501 -of GTiff
        TranslateOptions options = null;
        Dataset source = null;

        Vector ops = new Vector();
        ops.add("-projwin");
        ops.add("37.140077458");
        ops.add("55.991533828");
        ops.add("37.814126275");
        ops.add("55.73792501");
        try {
            options = new TranslateOptions(ops);
            gdal.AllRegister();
            source = gdal.Open("/home/vfilonov/programming/geodata/MSK_2017_vv.tif");

            gdal.Translate("/home/vfilonov/programming/geodata/translated.tif", source, options);


        } catch (RuntimeException rex) {
            System.out.println("Runtime caught");
            rex.printStackTrace();
        } finally {
            if (source != null) {
                source.delete();
            }
            if (options != null) {
                options.delete();
            }
        }
    }



}

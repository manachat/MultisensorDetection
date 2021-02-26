package vafilonov.msd.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import org.gdal.gdal.*;
import org.gdal.gdalconst.gdalconst;
import org.gdal.gdalconst.gdalconstConstants;
import vafilonov.msd.Main;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
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
        gdal.AllRegister();
        Dataset data = null;
        Band band = null;
        int x,y;

        String path = "/home/vfilonov/programming/geodata/MSK_2017_vv.tif";

        WritableImage img = null;
        int[][] raw = null;
        double[] stats = new double[2];


        try {
            data = gdal.Open(path, gdalconstConstants.GA_ReadOnly);


            band = data.GetRasterBand(1);

            band.ComputeBandStats(stats);

            double min = stats[0] - 2*stats[1];
            double max = stats[0] + 2*stats[1];

            min = Math.max(0.0, min);

            x = band.GetXSize();
            y = band.getYSize();

            raw = new int[y][x];
            short[] intermediate = new short[x];


            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(2*x);
            byteBuffer.order(ByteOrder.nativeOrder());

            for (int j = 0; j < y; j++) {
                band.ReadRaster_Direct(0, j, x, 1, x, 1, gdalconst.GDT_Int16, byteBuffer);
                byteBuffer.asShortBuffer().get(intermediate);
                for (int i = 0; i < x; i++) {
                    int normalized = (int) ((intermediate[i] - min) * 255 / (max - min));

                    normalized = Math.max(0, normalized);
                    normalized = Math.min(255, normalized);

                    raw[j][i] = 255 << 24 | normalized << 16 | normalized << 8 | normalized;
                }

            }



        } catch (Exception ex) {

            System.err.println("failed to open");
            return;
        } finally {
            if (data != null)
                data.delete();
        }

        img = new WritableImage(x, y);

        PixelWriter writer = img.getPixelWriter();
        PixelFormat<IntBuffer> format =  PixelFormat.getIntArgbInstance();

        for (int j = 0; j < y; j++) {
            writer.setPixels(0, j, x, 1, format, raw[j], 0, x);
        }


        //Image im = new Image(new FileInputStream("/home/vfilonov/Pictures/inn.png"));
        //view.setFitHeight(img.getRequestedHeight());
        //view.setFitWidth(img.getRequestedWidth());

        view.setViewport(new Rectangle2D(x / 2, y / 2, x /2, y/2));
        view.setImage(img);



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

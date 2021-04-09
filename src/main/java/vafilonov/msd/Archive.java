package vafilonov.msd;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.util.Callback;
import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.TranslateOptions;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.gdalconst.gdalconstConstants;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Vector;

public class Archive {

    public static void main(String[] args) {
        gdal.AllRegister();
        final String path1 = "/home/vfilonov/programming/geodata/test/learnings/forest_20_b1.tif";
        final String path2 = "/home/vfilonov/programming/geodata/test/learnings/forest_20_b2.tif";
        final String path5 = "/home/vfilonov/programming/geodata/test/learnings/forest_20_b5.tif";
        Dataset set1 = gdal.Open(path1);
        Band band1 = set1.GetRasterBand(1);
        Dataset set2 = gdal.Open(path2);
        Band band2 = set2.GetRasterBand(1);
        Dataset set5 = gdal.Open(path5);
        Band band5 = set5.GetRasterBand(1);

        double[] trans1 = new double[6];
        set1.GetGeoTransform(trans1);
        double[] trans2 = new double[6];
        set2.GetGeoTransform(trans2);
        double[] trans5 = new double[6];
        set5.GetGeoTransform(trans5);

        System.out.println("trans1: " + trans1[0] + "; " + (trans1[1]*band1.GetXSize()));
        System.out.println("trans2: " + trans2[0] + "; " + (trans2[1]*band2.GetXSize()));
        System.out.println("trans5: " + trans5[0] + "; " + (trans5[1]*band5.GetXSize()));

        System.out.println("Band1: size - " + band1.GetXSize() + "; natrual size - " + band1.GetBlockXSize());
        System.out.println("Band2: size - " + band2.GetXSize() + "; natrual size - " + band2.GetBlockXSize());
        System.out.println("Band5: size - " + band5.GetXSize() + "; natrual size - " + band5.GetBlockXSize());


        set1.delete();
        set2.delete();
        set5.delete();
    }

    private void readPixels(String path) {
        gdal.AllRegister();
        Dataset data = null;
        Band band;
        int x,y;


        WritableImage img = null;
        short[][] raw = null;
        double[] stats = new double[2];
        double min, max;


        try {
            data = gdal.Open(path, gdalconstConstants.GA_ReadOnly);


            band = data.GetRasterBand(1);

            band.ComputeBandStats(stats);

            min = stats[0] - 2*stats[1];
            max = stats[0] + 2*stats[1];

            min = Math.max(0.0, min);

            x = band.GetXSize();
            y = band.getYSize();

            raw = new short[y][x];
            short[] intermediate = new short[x];


            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(2*x);
            byteBuffer.order(ByteOrder.nativeOrder());

            for (int j = 0; j < y; j++) {
                band.ReadRaster_Direct(0, j, x, 1, x, 1, gdalconst.GDT_Int16, byteBuffer);
                byteBuffer.asShortBuffer().get(raw[j]);
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
            for (int i = 0; i < x; i++) {
                int normalized = (int) ((raw[j][i] - min) * 255 / (max - min));

                normalized = Math.max(0, normalized);
                normalized = Math.min(255, normalized);

                normalized = 255 << 24 | normalized << 16 | normalized << 8 | normalized;
                writer.setArgb(i,j, normalized);
            }
        }
        //view.setImage(img);
    }

    private void readBlockBytes(String path) {
        gdal.AllRegister();
        Dataset data = null;
        Band band = null;
        int x,y;


        WritableImage img = null;
        byte[] raw = null;
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

            raw = new byte[y*x*3];
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
                    byte val = (byte) normalized;
                    raw[j*x*3 + 3*i] = val;
                    raw[j*x*3 + 3*i + 1] = val;
                    raw[j*x*3 + 3*i + 2] = val;
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
        PixelFormat<ByteBuffer> format =  PixelFormat.getByteRgbInstance();

        //for (int j = 0; j < y; j++) {
        writer.setPixels(0, 0, x, y, format, raw, 0, 3*x);
        //}
        //view.setImage(img);
    }

    private void readBlockInt(String path) {
        gdal.AllRegister();
        Dataset data = null;
        Band band = null;
        int x,y;


        WritableImage img = null;
        int[] raw = null;
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

            raw = new int[y*x];
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

                    raw[j*x + i] = 255 << 24 | normalized << 16 | normalized << 8 | normalized;
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

        //for (int j = 0; j < y; j++) {
        writer.setPixels(0, 0, x, y, format, raw, 0, x);
        //}
        //view.setImage(img);
    }


    @SuppressWarnings("unchecked")
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

    /*
    b1ComboBox.setCellFactory(new Callback<ListView<File>, ListCell<File>>() {
        @Override
        public ListCell<File> call(ListView<File> fileListView) {
            final ListCell<File> cell = new ListCell<>() {
                @Override
                protected void updateItem(File file, boolean empty) {
                    super.updateItem(file, empty);
                    if (empty || file == null) {
                        setText(null);
                    } else {
                        setText(file.getName());
                    }
                }
            };
            return cell;
        }
    });

     */
}

package vafilonov.msd.utils;

import javafx.scene.image.WritableImage;
import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.gdalconst.gdalconstConstants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Renderer {


    /**
     * Computes raw pixel values of raster in IntARGB format.
     *
     *
     * @param redPath file with red band
     * @param greenPath file with green band
     * @param bluePath file with blue band
     * @return array of pixel values of size 2 + x*y, where arr[0] - X size of raster, arr[1] - Y size of raster
     */
    public static int[] renderRGB(final String redPath, final String greenPath, final String bluePath) {
        gdal.AllRegister();
        Dataset redData = null;
        Dataset greenData = null;
        Dataset blueData = null;
        Band red = null;
        Band green = null;
        Band blue = null;
        int x,y;


        int[] raw = null;
        double[] redStats = new double[2];
        double[] greenStats = new double[2];
        double[] blueStats = new double[2];


        try {
            redData = gdal.Open(redPath, gdalconstConstants.GA_ReadOnly);
            greenData = gdal.Open(greenPath, gdalconstConstants.GA_ReadOnly);
            blueData = gdal.Open(bluePath, gdalconstConstants.GA_ReadOnly);

            red = redData.GetRasterBand(1);
            green = greenData.GetRasterBand(1);
            blue = blueData.GetRasterBand(1);


            red.ComputeBandStats(redStats);
            green.ComputeBandStats(greenStats);
            blue.ComputeBandStats(blueStats);

            
            double redMin = redStats[0] - 3*redStats[1];
            double redMax = redStats[0] + 3*redStats[1];
            double greenMin = greenStats[0] - 3*greenStats[1];
            double greenMax = greenStats[0] + 3*greenStats[1];
            double blueMin = blueStats[0] - 3*blueStats[1];
            double blueMax = blueStats[0] + 3*blueStats[1];

            /*
            double redMin = redStats[0];
            double redMax = redStats[1];
            double greenMin = greenStats[0];
            double greenMax = greenStats[1];
            double blueMin = blueStats[0];
            double blueMax = blueStats[1];

             */


            System.err.println("Red stats: " + Arrays.toString(redStats));
            System.err.println("Green stats: " + Arrays.toString(greenStats));
            System.err.println("Blue stats: " + Arrays.toString(blueStats));
            System.err.println();
            System.err.println(redMin + " " + redMax);
            System.err.println(greenMin + " " + greenMax);
            System.err.println(blueMin + " " + blueMax);
            double[] trans = redData.GetGeoTransform();
            System.err.println();
            System.err.println(Arrays.toString(trans));

            x = red.GetXSize();
            y = red.getYSize();

            raw = new int[2 + y*x];
            raw[0] = x;
            raw[1] = y;

            short[] tempRed = new short[x];
            short[] tempGreen = new short[x];
            short[] tempBlue = new short[x];




            ByteBuffer redBuffer = ByteBuffer.allocateDirect(2*x);
            ByteBuffer greenBuffer = ByteBuffer.allocateDirect(2*x);
            ByteBuffer blueBuffer = ByteBuffer.allocateDirect(2*x);

            redBuffer.order(ByteOrder.nativeOrder());
            greenBuffer.order(ByteOrder.nativeOrder());
            blueBuffer.order(ByteOrder.nativeOrder());

            for (int j = 0; j < y; j++) {
                red.ReadRaster_Direct(0, j, x, 1, x, 1, gdalconst.GDT_Int16, redBuffer);
                redBuffer.asShortBuffer().get(tempRed);
                green.ReadRaster_Direct(0, j, x, 1, x, 1, gdalconst.GDT_Int16, greenBuffer);
                greenBuffer.asShortBuffer().get(tempGreen);
                blue.ReadRaster_Direct(0, j, x, 1, x, 1, gdalconst.GDT_Int16, blueBuffer);
                blueBuffer.asShortBuffer().get(tempBlue);


                for (int i = 0; i < x; i++) {
                    int r = (int) ((tempRed[i] - redMin) * 255 / (redMax - redMin));
                    int g = (int) ((tempGreen[i] - greenMin) * 255 / (greenMax - greenMin));
                    int b = (int) ((tempBlue[i] - blueMin) * 255 / (blueMax - blueMin));

                    int value = 255 << 24;
                    value = value | r << 16;
                    value = value | g << 8;
                    value = value | b;

                    raw[2 + j*x + i] = value;
                }

            }



        } catch (Exception ex) {
            System.out.println("Falure opening file, retard");
            System.out.println(ex.getMessage());
            raw = null;
        } finally { //  free allocated resources
            if (redData != null)
                redData.delete();
            if (greenData != null)
                greenData.delete();
            if (blueData != null)
                blueData.delete();
        }
        return raw;
    }
}

package vafilonov.msd.utils;

import javafx.scene.image.WritableImage;
import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.gdalconst.gdalconstConstants;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.charset.StandardCharsets;

import static vafilonov.msd.utils.Constants.BANDS_NUM;
import static vafilonov.msd.utils.Constants.PIXEL_RESOLUTIONS;
import static vafilonov.msd.utils.Constants.BAND_NAMES;

public class Renderer {


    /**
     * Computes raw pixel values of raster in IntARGB format.
     *
     *
     * @param redPath file with red band
     * @param greenPath file with green band
     * @param bluePath file with blue band
     * @return array of pixel values of size 2 + x*y, where arr[0] - X size of raster, arr[1] - Y size of raster
     * @throws IllegalArgumentException - in case bands are not aligned by coordinates
     */
    public static int[] renderRGBLike(final String redPath, final String greenPath, final String bluePath) {
        gdal.AllRegister();
        Dataset redData = null;
        Dataset greenData = null;
        Dataset blueData = null;
        Band red = null;
        Band green = null;
        Band blue = null;
        int x,y;

        int[] raw = null;

        try {
            redData = gdal.Open(redPath, gdalconstConstants.GA_ReadOnly);
            greenData = gdal.Open(greenPath, gdalconstConstants.GA_ReadOnly);
            blueData = gdal.Open(bluePath, gdalconstConstants.GA_ReadOnly);

            red = redData.GetRasterBand(1);
            green = greenData.GetRasterBand(1);
            blue = blueData.GetRasterBand(1);


            double[] transformRed = new double[6];
            double[] transformGreen = new double[6];
            double[] transformBlue = new double[6];
            redData.GetGeoTransform(transformRed);
            greenData.GetGeoTransform(transformGreen);
            blueData.GetGeoTransform(transformBlue);

            if (transformRed[0] != transformBlue[0] || transformRed[0] != transformGreen[0] ||
                    red.getXSize() != green.getXSize() || red.GetXSize() != blue.GetXSize() ||
                    red.GetYSize() != blue.GetYSize() || red.GetYSize() != green.GetYSize()) {
                throw new IllegalArgumentException("Render Error: Coordinates not aligned.");
            }

            raw = fillRender(red, green, blue);

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

    public static int[] renderIndex(final String extracted, final String exctractor) {
        throw new RuntimeException("renderIndex not implemented");
    }

    /**
     * Создает массив размера 2 + x*y с RGb значениями пикселей.
     * в первых двух элементах записаны x и y размеры растра соответственно
     * далее идут непосредственно значения построчно по x элементов
     * @param red красный канал
     * @param green зеленый канал
     * @param blue синий канал
     * @return массив размера 2 + x*y
     */
    private static int[] fillRender(Band red, Band green, Band blue ) {
        double[] redStats = new double[2];
        double[] greenStats = new double[2];
        double[] blueStats = new double[2];

        red.ComputeBandStats(redStats);
        green.ComputeBandStats(greenStats);
        blue.ComputeBandStats(blueStats);

        // 3 standard deviations
        int stdnum = 3;
        double redMin = redStats[0] - stdnum*redStats[1];
        double redMax = redStats[0] + stdnum*redStats[1];
        double greenMin = greenStats[0] - stdnum*greenStats[1];
        double greenMax = greenStats[0] + stdnum*greenStats[1];
        double blueMin = blueStats[0] - stdnum*blueStats[1];
        double blueMax = blueStats[0] + stdnum*blueStats[1];

        int x = red.GetXSize();
        int y = red.getYSize();

        int[] raw = new int[2 + y*x];
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

                r = Math.max(0, r);
                r = Math.min(255, r);
                g = Math.max(0, g);
                g = Math.min(255, g);
                b = Math.max(0, b);
                b = Math.min(255, b);

                int value = 255 << 24;
                value = value | r << 16;
                value = value | g << 8;
                value = value | b;

                raw[2 + j*x + i] = value;
            }

        }

        return raw;
    }


    /**
     * Создает датасет для обучения с заданной меткой класса и признаками из растровых каналов
     * @param classMark метка класс
     * @param output путь к выходному файлу
     * @param sourcefiles исходные файлы с растрами в порядке от B1 до B12
     */
    public static void createDataset(int classMark, String output, String[] sourcefiles) {

        if (sourcefiles.length != BANDS_NUM) {
            throw new IllegalArgumentException("Invalid band number. Should be " + BANDS_NUM);
        }

        gdal.AllRegister();
        Dataset[] datasets = new Dataset[BANDS_NUM];
        Band[] bands = new Band[BANDS_NUM];
        double[] transform = new double[6];

        try {
            for (int i = 0; i < BANDS_NUM; i++) {
                // load raster
                datasets[i] = gdal.Open(sourcefiles[i], gdalconst.GA_ReadOnly);
                datasets[i].GetGeoTransform(transform);
                bands[i] = datasets[i].GetRasterBand(1);

                // check pixel resolution
                if ((int)transform[1] != PIXEL_RESOLUTIONS[i]) {
                    throw new IllegalArgumentException("Invalid band resolution of  " + BAND_NAMES[i] +
                            ". Expected: " + PIXEL_RESOLUTIONS[i] + ". Actual: " + transform[1]);
                }
            }

            // write features to csv
            writeData(classMark, output, bands);

        } catch(IllegalArgumentException ilEx) {
            throw ilEx; // rethrow ex so it is not caught by general catch

        } catch(Exception ex) {
            throw new RuntimeException("Error in file opening.");

        } finally {
            for (var set : datasets) {
                if (set != null)
                    set.delete();
            }

        }
    }

    private static void writeData(int classMark, String output, Band[] bands) {
        final String noDataMatcher = classMark + ",0,0,0,0,0,0,0,0,0,0,0,0,0";

        // get geotransforms for bands of different resolutions
        double[] transform10 = new double[6];
        double[] transform20 = new double[6];
        double[] transform60 = new double[6];
        bands[1].GetDataset().GetGeoTransform(transform10);
        bands[4].GetDataset().GetGeoTransform(transform20);
        bands[0].GetDataset().GetGeoTransform(transform60);

        // get absolute coordinates of upper-left corner
        int x10 = (int) transform10[0];
        int y10 = (int) transform10[3];
        int x20 = (int) transform20[0];
        int y20 = (int) transform20[3];
        int x60 = (int) transform60[0];
        int y60 = (int) transform60[3];

        // get offsets of 20m and 60m resolutions
        int xOffset20 = x20 - x10;
        int yOffset20 = y20 - y10;
        int xOffset60 = x60 - x10;
        int yOffset60 = y60 - y10;

        // calculate offset for 10m resolution
        int xOffset10 = Math.max(xOffset20, xOffset60);
        xOffset10 = Math.max(0, xOffset10);
        int yOffset10 = Math.max(yOffset20, yOffset60);
        yOffset10 = Math.max(0, yOffset10);

        // width of intersection in meters
        int width = x10 + PIXEL_RESOLUTIONS[1]*bands[1].getXSize();
        width = Math.min(width, x20 + PIXEL_RESOLUTIONS[4]*bands[4].getXSize());
        width = Math.min(width, x60 + PIXEL_RESOLUTIONS[0]*bands[0].getXSize());
        width -= x10;
        width -= xOffset10;

        // height of intersection in meters
        int height = y10 + PIXEL_RESOLUTIONS[1]*bands[1].getYSize();
        height = Math.min(height, y20 + PIXEL_RESOLUTIONS[4]*bands[4].getYSize());
        height = Math.min(height, y60 + PIXEL_RESOLUTIONS[0]*bands[0].getYSize());
        height -= y10;
        height -= yOffset10;

        // rewrite offsets relatively to 10m
        xOffset20 = xOffset10 - xOffset20;
        yOffset20 = yOffset10 - yOffset20;
        xOffset60 = xOffset10 - xOffset60;
        yOffset60 = yOffset10 - yOffset60;

        // buffers for rows of data
        ByteBuffer[] rows = new ByteBuffer[BANDS_NUM];
        ShortBuffer[] shorts = new ShortBuffer[BANDS_NUM];
        for (int i = 0; i < rows.length; i++) {
            rows[i] = ByteBuffer.allocateDirect(2*bands[i].GetXSize()).order(ByteOrder.nativeOrder());
            shorts[i] = rows[i].asShortBuffer();
        }

        // write CSV
        try (PrintWriter csv = new PrintWriter(output, StandardCharsets.UTF_8)) {
            csv.println("Mark, b1, b2, b3, b4, b5, b6, b7, b8, b8a, b9, b10, b11, b12");
            StringBuilder builder;

            for (int y = 0; y < height; y += 10) {
                // refill buffers
                for (int i = 0; i < BANDS_NUM; i++) {
                    if (PIXEL_RESOLUTIONS[i] == 10) {
                        rows[i].clear();
                        bands[i].ReadRaster_Direct(xOffset10 / PIXEL_RESOLUTIONS[i], (yOffset10 + y) / PIXEL_RESOLUTIONS[i],
                                width / PIXEL_RESOLUTIONS[i], 1, width / PIXEL_RESOLUTIONS[i], 1,
                                gdalconst.GDT_Int16, rows[i]);

                    } else if (PIXEL_RESOLUTIONS[i] == 20) {
                        if ((yOffset20 + y) % PIXEL_RESOLUTIONS[i] == 0 || y == 0) {
                            rows[i].clear();
                            bands[i].ReadRaster_Direct(xOffset20 / PIXEL_RESOLUTIONS[i], (yOffset20 + y) / PIXEL_RESOLUTIONS[i],
                                    width / PIXEL_RESOLUTIONS[i], 1, width / PIXEL_RESOLUTIONS[i], 1,
                                    gdalconst.GDT_Int16, rows[i]);
                        }

                    } else {
                        if ((yOffset60 + y) % PIXEL_RESOLUTIONS[i] == 0 || y == 0) {
                            rows[i].clear();
                            bands[i].ReadRaster_Direct(xOffset60 / PIXEL_RESOLUTIONS[i], (yOffset60 + y) / PIXEL_RESOLUTIONS[i],
                                    width / PIXEL_RESOLUTIONS[i], 1, width / PIXEL_RESOLUTIONS[i], 1,
                                    gdalconst.GDT_Int16, rows[i]);
                        }

                    }

                }

                // write features for each pixel
                for (int x = 0; x < width; x += 10) {

                    builder = new StringBuilder();
                    builder.append(classMark);
                    builder.append(',');


                    for (int i = 0; i < BANDS_NUM; i++) {
                        builder.append(shorts[i].get(x / PIXEL_RESOLUTIONS[i]));

                        if (i != BANDS_NUM - 1)
                            builder.append(',');
                    }

                    String res = builder.toString();
                    if (!res.equals(noDataMatcher)) {   //  checks nodata
                        csv.println(res);
                    }
                }

            }

        } catch (IOException ioException) {
            throw new IllegalArgumentException("Error opening csv file.");

        }
    }
}
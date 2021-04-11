package vafilonov.msd.core.sentinel2;

import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import vafilonov.msd.core.RasterDataset;

import vafilonov.msd.core.sentinel2.utils.Constants;

import static vafilonov.msd.core.sentinel2.utils.Constants.BANDS_NUM;
import static vafilonov.msd.core.sentinel2.utils.Constants.getResolution;
import static vafilonov.msd.core.sentinel2.utils.Resolution.res10M;
import static vafilonov.msd.core.sentinel2.utils.Resolution.res20M;
import static vafilonov.msd.core.sentinel2.utils.Resolution.res60m;

public class Sentinel2RasterDataset implements RasterDataset {

    private Band[] bands;
    private double[][] geoTransforms;

    public Sentinel2RasterDataset(String[] bandPaths) {
        if (bandPaths.length != BANDS_NUM) {
            throw new IllegalArgumentException("Invalid band number. Should be " + BANDS_NUM);
        }

        gdal.AllRegister();
        Dataset[] datasets = new Dataset[BANDS_NUM];
        bands = new Band[BANDS_NUM];
        geoTransforms = new double[BANDS_NUM][];
        double[] transform;

        try {
            for (int i = 0; i < BANDS_NUM; i++) {
                // load raster
                if (bands[i] != null) {
                    transform = new double[6];
                    datasets[i] = gdal.Open(bandPaths[i], gdalconst.GA_ReadOnly);
                    datasets[i].GetGeoTransform(transform);
                    bands[i] = datasets[i].GetRasterBand(1);
                    geoTransforms[i] = transform;

                    // check pixel resolution
                    if ((int) transform[1] != Constants.PIXEL_RESOLUTIONS[i]) {
                        throw new IllegalArgumentException("Invalid band resolution of  " + Constants.BAND_NAMES[i] +
                                ". Expected: " + Constants.PIXEL_RESOLUTIONS[i] + ". Actual: " + transform[1]);
                    }
                }
            }

        } catch(IllegalArgumentException ilEx) {
            delete();
            throw ilEx; // rethrow ex so it is not caught by general catch

        } catch(Exception ex) {
            delete();
            throw new RuntimeException("Error in file opening.");

        }
    }

    public void delete() {
        for (var b : bands) {
            if (b != null)
                b.GetDataset().delete();
        }
    }

    @Override
    public Band[] getAvailableBands() {
        return new Band[0];
    }

    @Override
    public int[] computeOffsets() {
        return new int[0];
    }

    private int[] compute3ResOffsets(int band10idx, int band20idx, int band60idx) {
        if (bands[band10idx] == null || bands[band20idx] == null || bands[band60idx] == null) {
            throw new IllegalArgumentException("Not all bands present");
        }
        Band band10, band20, band60;
        band10 = bands[band10idx];
        band20 = bands[band20idx];
        band60 = bands[band60idx];

        // get geotransforms for bands of different resolutions
        double[] transform10 = geoTransforms[band10idx];
        double[] transform20 = geoTransforms[band20idx];
        double[] transform60 = geoTransforms[band60idx];


        int x10,y10,x20,y20,x60,y60;
        // get absolute coordinates of upper-left corner
        x10 = (int) transform10[0];
        y10 = (int) transform10[3];
        x20 = (int) transform20[0];
        y20 = (int) transform20[3];
        x60 = (int) transform60[0];
        y60 = (int) transform60[3];

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
        int width = x10 + getResolution(res10M)*band10.getXSize();               //  right border of 10
        width = Math.min(width, x20 + getResolution(res20M)*band20.getXSize());  //  right border of 20
        width = Math.min(width, x60 + getResolution(res60m)*band60.getXSize());  //  right border of 60
        width -= x10;
        width -= xOffset10;

        // height of intersection in meters
        int height = y10 + getResolution(res10M)*band10.getYSize();
        height = Math.min(height, y20 + getResolution(res20M)*band20.getYSize());
        height = Math.min(height, y60 + getResolution(res60m)*band60.getYSize());
        height -= y10;
        height -= yOffset10;

        // rewrite offsets relatively to 10m
        xOffset20 = xOffset10 - xOffset20;
        yOffset20 = yOffset10 - yOffset20;
        xOffset60 = xOffset10 - xOffset60;
        yOffset60 = yOffset10 - yOffset60;

        return new int[] {width, height, xOffset10, yOffset10, xOffset20, yOffset20, xOffset60, yOffset60};
    }
}

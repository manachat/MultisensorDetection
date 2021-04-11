package vafilonov.msd.core.sentinel2;

import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import vafilonov.msd.core.RasterDataset;

import vafilonov.msd.core.sentinel2.utils.Constants;

public class Sentinel2RasterDataset implements RasterDataset {

    private Band[] bands;

    public Sentinel2RasterDataset(String[] bandPaths) {
        if (bandPaths.length != Constants.BANDS_NUM) {
            throw new IllegalArgumentException("Invalid band number. Should be " + Constants.BANDS_NUM);
        }

        gdal.AllRegister();
        Dataset[] datasets = new Dataset[Constants.BANDS_NUM];
        bands = new Band[Constants.BANDS_NUM];
        double[] transform = new double[6];

        try {
            for (int i = 0; i < Constants.BANDS_NUM; i++) {
                // load raster
                if (bands[i] != null) {
                    datasets[i] = gdal.Open(bandPaths[i], gdalconst.GA_ReadOnly);
                    datasets[i].GetGeoTransform(transform);
                    bands[i] = datasets[i].GetRasterBand(1);

                    // check pixel resolution
                    if ((int) transform[1] != Constants.PIXEL_RESOLUTIONS[i]) {
                        throw new IllegalArgumentException("Invalid band resolution of  " + Constants.BAND_NAMES[i] +
                                ". Expected: " + Constants.PIXEL_RESOLUTIONS[i] + ". Actual: " + transform[1]);
                    }
                }
            }

            this.bands = bands;
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

    private int[] compute3ResOffsets(Band band10, Band band20, Band band60) {
        // get geotransforms for bands of different resolutions
        double[] transform10 = new double[6];
        double[] transform20 = new double[6];
        double[] transform60 = new double[6];
        band10.GetDataset().GetGeoTransform(transform10);
        band20.GetDataset().GetGeoTransform(transform20);
        band60.GetDataset().GetGeoTransform(transform60);

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
        int width = x10 + Constants.PIXEL_RESOLUTIONS[1]*band10.getXSize();               //  right border of 10
        width = Math.min(width, x20 + Constants.PIXEL_RESOLUTIONS[4]*band20.getXSize());  //  right border of 20
        width = Math.min(width, x60 + Constants.PIXEL_RESOLUTIONS[0]*band60.getXSize());  //  right border of 60
        width -= x10;
        width -= xOffset10;

        // height of intersection in meters
        int height = y10 + Constants.PIXEL_RESOLUTIONS[1]*band10.getYSize();
        height = Math.min(height, y20 + Constants.PIXEL_RESOLUTIONS[4]*band20.getYSize());
        height = Math.min(height, y60 + Constants.PIXEL_RESOLUTIONS[0]*band60.getYSize());
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

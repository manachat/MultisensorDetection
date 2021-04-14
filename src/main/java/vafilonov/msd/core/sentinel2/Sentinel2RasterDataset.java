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
import static vafilonov.msd.core.sentinel2.utils.Resolution.res60M;
import static vafilonov.msd.core.sentinel2.utils.Resolution.res60m;

/**
 * Dataset implementation for Sentinel-2 imagery
 */
public class Sentinel2RasterDataset implements RasterDataset {

    /**
     * indices of bands with 10m resolution
     */
    private static final int[] res10indices = {1, 2, 3, 7};

    /**
     * indices of bands with 20m resolution
     */
    private static final int[] res20indices = {4, 5, 6, 8, 11, 12};

    /**
     * indices of bands with 60m resolution
     */
    private static final int[] res60indices = {0, 9, 10};


    /**
     * Fabric method.
     * Loads datasets and validates them.
     * @param bandPaths path to band files in order
     * @return dataset instance
     * @throws IllegalArgumentException invalid band number, resolution or alignment
     */
    public static Sentinel2RasterDataset loadDataset(String[] bandPaths) {
        if (bandPaths.length != BANDS_NUM) {
            throw new IllegalArgumentException("Invalid band number. Should be " + BANDS_NUM);
        }

        gdal.AllRegister();
        Dataset[] datasets = new Dataset[BANDS_NUM];
        Band[] bands = new Band[BANDS_NUM];
        double[][] geoTransforms = new double[BANDS_NUM][];
        double[] transform;

        try {
            for (int i = 0; i < BANDS_NUM; i++) {
                // load raster
                if (bandPaths[i] != null) {
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

            checkAlignment(bands, geoTransforms);

            return new Sentinel2RasterDataset(bands, geoTransforms);
        } catch(IllegalArgumentException ilEx) {
            for (var set : datasets) {
                if (set != null)
                    set.delete();
            }
            throw ilEx; // rethrow ex so it is not caught by general catch

        } catch(Exception ex) {
            for (var set : datasets) {
                if (set != null)
                    set.delete();
            }
            throw new RuntimeException("Error in file opening.");

        }
    }

    /**
     * Checks band alignment of different resolutions
     * @param bands bands
     * @param geoTransforms geo transformations
     * @throws IllegalArgumentException in case rasters not aligned
     */
    private static void checkAlignment(Band[] bands, double[][] geoTransforms) {
        int lastX = -1;
        int lastY = -1;
        for (int i : res10indices) {
            if (bands[i] != null) {
                if (lastX == -1) {
                    lastX = (int) geoTransforms[i][0];
                    lastY = (int) geoTransforms[i][3];
                } else {
                    if (lastX != (int) geoTransforms[i][0] || lastY != geoTransforms[i][3])
                        throw new IllegalArgumentException("Rasters 10m not aligned");
                }
            }
        }
        lastX = -1;
        lastY = -1;
        for (int i : res20indices) {
            if (bands[i] != null) {
                if (lastX == -1) {
                    lastX = (int) geoTransforms[i][0];
                    lastY = (int) geoTransforms[i][3];
                } else {
                    if (lastX != (int) geoTransforms[i][0] || lastY != geoTransforms[i][3])
                        throw new IllegalArgumentException("Rasters 20m not aligned");
                }
            }
        }
        lastX = -1;
        lastY = -1;
        for (int i : res60indices) {
            if (bands[i] != null) {
                if (lastX == -1) {
                    lastX = (int) geoTransforms[i][0];
                    lastY = (int) geoTransforms[i][3];
                } else {
                    if (lastX != (int) geoTransforms[i][0] || lastY != geoTransforms[i][3])
                        throw new IllegalArgumentException("Rasters 60m not aligned");
                }
            }
        }
    }

    private final Band[]                bands;
    private final double[][]    geoTransforms;
    private boolean                  disposed;

    /**
     * Constructor
     * @param bands bands
     * @param geoTransforms bands' geo transformations
     */
    private Sentinel2RasterDataset(Band[] bands, double[][] geoTransforms) {
        this.bands = bands;
        this.geoTransforms = geoTransforms;
        disposed = false;
    }

    @Override
    public boolean isDisposed() {
        return disposed;
    }

    @Override
    public void delete() {
        for (var b : bands) {
            if (b != null)
                b.GetDataset().delete();
        }
        disposed = true;
    }

    @Override
    public Band[] getBands() {
        return bands;
    }


    @Override
    public int[] computeOffsets() {
        int idx10 = -1;
        for (int i : res10indices) {
            if (bands[i] != null) {
                idx10 = i;
                break;
            }
        }

        int idx20 = -1;
        for (int i : res20indices) {
            if (bands[i] != null) {
                idx20 = i;
                break;
            }
        }

        int idx60 = -1;
        for (int i : res60indices) {
            if (bands[i] != null) {
                idx60 = i;
                break;
            }
        }

        if (idx10 != -1 && idx20 != -1 && idx60 != -1) {
            return compute3ResOffsets(idx10, idx20, idx60);
        } else if (idx10 != -1 && idx20 != -1) {
            return computeOffsets10and20(idx10, idx20);
        } else if (idx10 != -1 && idx60 != -1) {
            return computeOffsets10and60(idx10, idx60);
        } else if (idx10 != -1) {
            return new int[] {(int) geoTransforms[idx10][0], (int) geoTransforms[idx10][3], 0,0,0,0,0,0};
        } else if (idx20 != -1) {
            return new int[] {(int) geoTransforms[idx20][0], (int) geoTransforms[idx20][3], 0,0,0,0,0,0};
        } else if (idx60 != -1) {
            return new int[] {(int) geoTransforms[idx60][0], (int) geoTransforms[idx60][3], 0,0,0,0,0,0};
        } else {
            throw new IllegalStateException("Not enough bands.");
        }
    }

    /**
     * Calculates offsets in meters for all 3 resolutions
     * @param band10idx band index with 10m resolution
     * @param band20idx band index with 20m resolution
     * @param band60idx band index with 60m resolution
     * @return 8 elements array: [width, height, 6 values with offsets]
     * @throws IllegalArgumentException - lacks one of bands
     */
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
        width = Math.min(width, x20 + getResolution(res60M)*band20.getXSize());  //  right border of 20
        width = Math.min(width, x60 + getResolution(res60m)*band60.getXSize());  //  right border of 60
        width -= x10;
        width -= xOffset10;

        // height of intersection in meters
        int height = y10 + getResolution(res10M)*band10.getYSize();
        height = Math.min(height, y20 + getResolution(res60M)*band20.getYSize());
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

    /**
     * Calculates offsets in meters for 10m and 20m resolutions
     * @param band10idx band index with 10m resolution
     * @param band20idx band index with 20m resolution
     * @return 8 elements array: [width, height, 6 values with offsets] 60m alignments are 0
     * @throws IllegalArgumentException - lacks one of bands
     */
    private int[] computeOffsets10and20(int band10idx, int band20idx) {
        if (bands[band10idx] == null || bands[band20idx] == null) {
            throw new IllegalArgumentException("Not all bands present");
        }

        Band band10, band20;
        band10 = bands[band10idx];
        band20 = bands[band20idx];

        double[] transform10 = geoTransforms[band10idx];
        double[] transform20 = geoTransforms[band20idx];

        int x10,y10,x20,y20;
        x10 = (int) transform10[0];
        y10 = (int) transform10[3];
        x20 = (int) transform20[0];
        y20 = (int) transform20[3];

        int xOffset20 = x20 - x10;
        int yOffset20 = y20 - y10;

        int xOffset10 = Math.max(0, xOffset20);
        int yOffset10 = Math.max(0, yOffset20);

        // width of intersection in meters
        int width = x10 + getResolution(res10M)*band10.getXSize();               //  right border of 10
        width = Math.min(width, x20 + getResolution(res60M)*band20.getXSize());  //  right border of 20
        width -= x10;
        width -= xOffset10;

        // height of intersection in meters
        int height = y10 + getResolution(res10M)*band10.getYSize();
        height = Math.min(height, y20 + getResolution(res60M)*band20.getYSize());
        height -= y10;
        height -= yOffset10;

        xOffset20 = xOffset10 - xOffset20;
        yOffset20 = yOffset10 - yOffset20;

        return new int[] {width, height, xOffset10, yOffset10, xOffset20, yOffset20, 0, 0};
    }

    /**
     * Calculates offsets in meters for 10m and 60m resolutions
     * @param band10idx band index with 10m resolution
     * @param band60idx band index with 60m resolution
     * @return 8 elements array: [width, height, 6 values with offsets] 20m alignments are 0
     * @throws IllegalArgumentException - lacks one of bands
     */
    private int[] computeOffsets10and60(int band10idx, int band60idx) {
        if (bands[band10idx] == null || bands[band60idx] == null) {
            throw new IllegalArgumentException("Not all bands present");
        }

        Band band10, band60;
        band10 = bands[band10idx];
        band60 = bands[band60idx];

        double[] transform10 = geoTransforms[band10idx];
        double[] transform60 = geoTransforms[band60idx];

        int x10,y10,x60,y60;
        x10 = (int) transform10[0];
        y10 = (int) transform10[3];
        x60 = (int) transform60[0];
        y60 = (int) transform60[3];

        int xOffset60 = x60 - x10;
        int yOffset60 = y60 - y10;

        int xOffset10 = Math.max(0, xOffset60);
        int yOffset10 = Math.max(0, yOffset60);

        // width of intersection in meters
        int width = x10 + getResolution(res10M)*band10.getXSize();               //  right border of 10
        width = Math.min(width, x60 + getResolution(res60M)*band60.getXSize());  //  right border of 60
        width -= x10;
        width -= xOffset10;

        // height of intersection in meters
        int height = y10 + getResolution(res10M)*band10.getYSize();
        height = Math.min(height, y60 + getResolution(res60M)*band60.getYSize());
        height -= y10;
        height -= yOffset10;

        xOffset60 = xOffset10 - xOffset60;
        yOffset60 = yOffset10 - yOffset60;

        return new int[] {width, height, xOffset10, yOffset10,  0, 0, xOffset60, yOffset60};
    }
}

package vafilonov.msd.core.sentinel2.utils;

/**
 * Constants for Sentinel-2 bands
 */
public final class Constants {
    /**
     * Bands number
     */
    public static final int BANDS_NUM = 13;

    /**
     * Resolutions of bands in meters
     */
    public static final int[] PIXEL_RESOLUTIONS = {60, 10, 10, 10, 20, 20, 20, 10, 20 ,60 ,60 ,20 ,20};

    /**
     * Band names
     */
    public static final String[] BAND_NAMES = {"B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B8A", "B9", "B10", "B11", "B12"};

    /**
     * Transforms band into resolution
     * @param band band
     * @return resolution in meters
     */
    public static int getResolution(Sentinel2Band band) {
        return PIXEL_RESOLUTIONS[band.ordinal()];
    }

    /**
     * Transforms resolution enum into integer
     * @param res resolution enum
     * @return resolution in meters
     */
    public static int getResolution(Resolution res) {
        return PIXEL_RESOLUTIONS[res.ordinal() + 7];    //  0 -> [7] = 10, 1 -> [8] = 20, 2 -> [9] = 60
    }

    /**
     * returns name of the band
     * @param band band
     * @return name in String
     */
    public static String getBandName(Sentinel2Band band) {
        return BAND_NAMES[band.ordinal()];
    }

}

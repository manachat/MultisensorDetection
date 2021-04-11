package vafilonov.msd.core.sentinel2.utils;

public final class Constants {
    public static final int BANDS_NUM = 13;
    public static final int[] PIXEL_RESOLUTIONS = {60, 10, 10, 10, 20, 20, 20, 10, 20 ,60 ,60 ,20 ,20};
    public static final String[] BAND_NAMES = {"B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B8A", "B9", "B10", "B11", "B12"};

    public static int getResolution(Sentinel2Band band) {
        return PIXEL_RESOLUTIONS[band.ordinal()];
    }

    public static int getResolution(Resolution res) {
        switch (res) {
            case res10M:
                return 10;
            case res20M:
                return 20;
            case res60m:
                return 60;
        }
    }

    public static String getBandName(Sentinel2Band band) {
        return BAND_NAMES[band.ordinal()];
    }

}

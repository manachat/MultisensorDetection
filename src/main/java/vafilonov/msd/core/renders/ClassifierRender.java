package vafilonov.msd.core.renders;

import vafilonov.msd.core.PixelClassifier;
import vafilonov.msd.core.RasterDataset;
import vafilonov.msd.core.sentinel2.utils.Constants;
import vafilonov.msd.core.sentinel2.utils.Sentinel2Band;

import java.nio.ShortBuffer;



public class ClassifierRender extends RGBRender {
    PixelClassifier presentClassifier;
    PixelClassifier pastClassifier;

    public ClassifierRender(RasterDataset dataset, PixelClassifier[] classifiers) {
        super(dataset);
        presentClassifier = classifiers[0];
        if (classifiers.length > 1) {
            pastClassifier = classifiers[1];
        }
        traverseMask = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true, true};
    }


    @Override
    public void processPixel(ShortBuffer[][] values, int[] params) {
        int rasterRow = params[0];

        ShortBuffer[] presentRows = values[0]; // it is assumed that first dataset is presented

        //  +1 in case of ndvi
        double[] presentVals = new double[Constants.BANDS_NUM + 1];
        double[] pastVals = new double[Constants.BANDS_NUM + 1];

        for (int i = 0; i < rasterWidth; i++) {

            int r = (int) ((presentRows[Sentinel2Band.B4.ordinal()].get(i) - redMin) * 255 / (redMax - redMin));
            int g = (int) ((presentRows[Sentinel2Band.B3.ordinal()].get(i) - greenMin) * 255 / (greenMax - greenMin));
            int b = (int) ((presentRows[Sentinel2Band.B2.ordinal()].get(i) - blueMin) * 255 / (blueMax - blueMin));

            r = Math.max(0, r);
            r = Math.min(255, r);
            g = Math.max(0, g);
            g = Math.min(255, g);
            b = Math.max(0, b);
            b = Math.min(255, b);

            int value = 128 << 24;
            value = value | r << 16;
            value = value | g << 8;
            value = value | b;

            for (int j = 0; j < Constants.BANDS_NUM; j++) {
                presentVals[j] = values[0][j].get(i*10 / Constants.PIXEL_RESOLUTIONS[j]);
            }
            int classPresent = presentClassifier.classifyPixel(presentVals);

            for (int j = 0; j < Constants.BANDS_NUM; j++) {
                pastVals[j] = values[1][j].get(i*10 / Constants.PIXEL_RESOLUTIONS[j]);
            }
            int classPast = pastClassifier.classifyPixel(pastVals);

            int classColor = colorMapper(classPast, classPresent);  // classified color difference
            value = classColor == -1 ? value : classColor;

            raster[rasterWidth*rasterRow + i] = value;
        }

    }

    /**
     * Maps class change into color
     * Clouds are not colored
     * @param pastClass previous class
     * @param presentClass present class
     * @return argb color int
     */
    public int colorMapper(int pastClass, int presentClass) {
        if (pastClass == presentClass){
            return -1;
        } else {
            return (255 << 24) | map[pastClass][presentClass] ; //TODO переделать
        }

    }
    // 2894892 - grey
    // 805403 - dark green
    // 2666570 - light green
    // 599776 - blue
    // 13942845 - ohra
    private static int[][] map ={
            {    -1, 2666570, 2894892, 805403, 13942845},
            {599776,      -1, 2894892, 805403, 13942845},
            {599776, 2666570,      -1, 805403, 13942845},
            {599776, 2666570, 2894892,     -1, 13942845},
            {599776, 2666570, 2894892, 805403,       -1}
    };

}

package vafilonov.msd.core.renders;

import vafilonov.msd.core.PixelClassifier;
import vafilonov.msd.core.RasterDataset;
import vafilonov.msd.core.sentinel2.utils.Constants;
import vafilonov.msd.core.sentinel2.utils.Biom;
import vafilonov.msd.core.sentinel2.utils.Sentinel2Band;

import java.nio.ShortBuffer;



public class ClassifierRender extends RGBRender {
    PixelClassifier classifier;

    public ClassifierRender(RasterDataset dataset, PixelClassifier classifier) {
        super(dataset);
        this.classifier = classifier;
        traverseMask = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true, true};
    }


    @Override
    public void processPixel(ShortBuffer[][] values, int[] params) {
        ShortBuffer[] rows = values[0]; // it is assumed that first dataset is presented
        int rasterRow = params[0];

        double[] featureVals = new double[Constants.BANDS_NUM];
        for (int i = 0; i < rasterWidth; i++) {

            int r = (int) ((rows[Sentinel2Band.B4.ordinal()].get(i) - redMin) * 255 / (redMax - redMin));
            int g = (int) ((rows[Sentinel2Band.B3.ordinal()].get(i) - greenMin) * 255 / (greenMax - greenMin));
            int b = (int) ((rows[Sentinel2Band.B2.ordinal()].get(i) - blueMin) * 255 / (blueMax - blueMin));

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

            for (int j = 0; j < featureVals.length; j++) {
                featureVals[j] = values[0][j].get(i*10 / Constants.PIXEL_RESOLUTIONS[j]);
            }
            int classPresent = classifier.classifyPixel(featureVals);

            for (int j = 0; j < featureVals.length; j++) {
                featureVals[j] = values[1][j].get(i*10 / Constants.PIXEL_RESOLUTIONS[j]);
            }
            int classPast = classifier.classifyPixel(featureVals);

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
        if (pastClass == presentClass || pastClass == Biom.CLOUDS.ordinal() || presentClass == Biom.CLOUDS.ordinal()){
            return -1;
        } else {
            return Integer.MAX_VALUE; //TODO переделать
        }

    }

}

package vafilonov.msd.core.renders;

import vafilonov.msd.core.PixelClassifier;
import vafilonov.msd.core.RasterDataset;
import vafilonov.msd.core.sentinel2.utils.Constants;

import java.awt.*;
import java.nio.ShortBuffer;

/**
 * Applies classification algorithm on pixel and highlights change pixels.
 * Unchanged pixels are rendered in standard RGB.
 */
public class ClassifierRender extends RGBRender {
    /**
     * Pixel classifier
     */
    PixelClassifier classifier;

    /**
     * Constructor.
     * @param dataset dataset to render in RGB
     * @param classifier pixel classifier
     */
    public ClassifierRender(RasterDataset dataset, PixelClassifier classifier) {
        super(dataset);
        this.classifier = classifier;
        traverseMask = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true, true};
    }

    @Override
    protected int modifyPixel(int i, int j, int colorValue, ShortBuffer[][] values, int[] params) {
        double[] featureVals = new double[Constants.BANDS_NUM];
        for (int k = 0; k < featureVals.length; k++) {
            featureVals[k] = values[0][k].get(j*10 / Constants.PIXEL_RESOLUTIONS[k]);
        }
        int classPresent = classifier.classifyPixel(featureVals);

        for (int k = 0; k < featureVals.length; k++) {
            featureVals[k] = values[1][k].get(j*10 / Constants.PIXEL_RESOLUTIONS[k]);
        }
        int classPast = classifier.classifyPixel(featureVals);

        int classColor = colorMapper(classPast, classPresent);  // classified color difference

        return classColor == -1 ? colorValue : classColor;
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
            return (255 << 24) | map[pastClass][presentClass].getRGB() ;
        }

    }

    /**
     * Color map for pixel change
     */
    private static Color[][] map ={
            {    null, new Color(0x93F1B9), new Color(0xFFCCCC), new Color(0x0C7D77), new Color(0xFFFFCC) },
            {new Color(0x86A8EC),      null, new Color(0xE85F9F), new Color(0x149714), new Color(0xFFFF99)},
            {new Color(0x6B9CF5), new Color(0x65E00F), null, new Color(0x008A22), new Color(0xFFFF33)},
            {new Color(0x2B6DEF), new Color(0x7ACC29), new Color(0xE72727),     null, new Color(0xCCCC00)},
            {new Color(0x0909EA), new Color(0xA5F56D), new Color(0x660000), new Color(0x003300),  null}
    };

}

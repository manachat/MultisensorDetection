package vafilonov.msd.core.renders;

import vafilonov.msd.core.PixelClassifier;
import vafilonov.msd.core.RasterDataset;
import vafilonov.msd.core.sentinel2.utils.Constants;
import vafilonov.msd.core.sentinel2.utils.Biom;
import vafilonov.msd.core.sentinel2.utils.Sentinel2Band;

import java.awt.*;
import java.nio.ShortBuffer;



public class ClassifierRender extends RGBRender {
    PixelClassifier classifier;

    public ClassifierRender(RasterDataset dataset, PixelClassifier classifier) {
        super(dataset);
        this.classifier = classifier;
        traverseMask = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true, true};
    }

    @Override
    protected int modifyPixel(int i, int j, int colorValue, ShortBuffer[][] values, int[] params) {
        double[] featureVals = new double[Constants.BANDS_NUM];
        for (int k = 0; k < featureVals.length; k++) {
            featureVals[k] = values[0][k].get(i*10 / Constants.PIXEL_RESOLUTIONS[k]);
        }
        int classPresent = classifier.classifyPixel(featureVals);

        for (int k = 0; k < featureVals.length; k++) {
            featureVals[k] = values[1][k].get(i*10 / Constants.PIXEL_RESOLUTIONS[k]);
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
            return (255 << 24) | map[pastClass][presentClass].getRGB() ; //TODO переделать
        }

    }
    // 2894892 - grey
    // 805403 - dark green
    // 2666570 - light green
    // 599776 - blue
    // 13942845 - ohra
    private static Color[][] map ={
            {    null, new Color(0x93F1B9), new Color(0x9E9ED0), new Color(0x0C7D77), new Color(0x698D1B) },
            {new Color(0x86A8EC),      null, new Color(0xE85F9F), new Color(0x149714), new Color(0xBDBD37)},
            {new Color(0x6B9CF5), new Color(0x65E00F), null, new Color(0x00781E), new Color(0x787878)},
            {new Color(0x2B6DEF), new Color(0x929528), new Color(0xE72727),     null, new Color(0xFAFA3F)},
            {new Color(0x0909EA), new Color(0xA5F56D), new Color(0x7B237B), new Color(0x567160),  null}
    };

}

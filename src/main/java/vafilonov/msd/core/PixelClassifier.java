package vafilonov.msd.core;

/**
 * Classifier of pixel values
 */
public interface PixelClassifier {
    /**
     * pixel classification method
     * @param featureValues features
     * @return pixel class
     */
    int classifyPixel(double[] featureValues);


}

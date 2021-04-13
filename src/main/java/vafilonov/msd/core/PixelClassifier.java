package vafilonov.msd.core;

import weka.classifiers.Classifier;
import weka.core.SerializationHelper;

public interface PixelClassifier {
    int classifyPixel(double[] featureValues);

}

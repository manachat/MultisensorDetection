package vafilonov.msd.core.sentinel2;

import vafilonov.msd.core.PixelClassifier;
import vafilonov.msd.core.sentinel2.utils.Sentinel2Band;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.util.ArrayList;
import java.util.Arrays;

import static vafilonov.msd.core.sentinel2.utils.Constants.*;

/**
 * Classifier of Sentinel-2 imagery
 */
public class Sentinel2PixelClassifier implements PixelClassifier {

    /**
     * loaded classifier model
     */
    private final Classifier classifier;

    /**
     * Data model for dataset
     */
    private final Instances dataModel;

    /**
     * instance for classification
     */
    private final DenseInstance instance;

    /**
     * path to model file
     */
    private String modelPath;

    /**
     * Creates class instance with loaded classifire
     * @param cl classifier instance
     */
    private Sentinel2PixelClassifier(Classifier cl) {
        classifier = cl;

        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(BAND_NAMES[Sentinel2Band.B1.ordinal()]));
        attributes.add(new Attribute(BAND_NAMES[Sentinel2Band.B2.ordinal()]));
        attributes.add(new Attribute(BAND_NAMES[Sentinel2Band.B3.ordinal()]));
        attributes.add(new Attribute(BAND_NAMES[Sentinel2Band.B4.ordinal()]));
        attributes.add(new Attribute(BAND_NAMES[Sentinel2Band.B5.ordinal()]));
        attributes.add(new Attribute(BAND_NAMES[Sentinel2Band.B6.ordinal()]));
        attributes.add(new Attribute(BAND_NAMES[Sentinel2Band.B7.ordinal()]));
        attributes.add(new Attribute(BAND_NAMES[Sentinel2Band.B8.ordinal()]));
        attributes.add(new Attribute(BAND_NAMES[Sentinel2Band.B8A.ordinal()]));
        attributes.add(new Attribute(BAND_NAMES[Sentinel2Band.B9.ordinal()]));
        attributes.add(new Attribute(BAND_NAMES[Sentinel2Band.B10.ordinal()]));
        attributes.add(new Attribute(BAND_NAMES[Sentinel2Band.B11.ordinal()]));
        attributes.add(new Attribute(BAND_NAMES[Sentinel2Band.B12.ordinal()]));
        attributes.add(new Attribute("Mark", Arrays.asList("1","2","3","4","5")));
        dataModel = new Instances("pixels", attributes, 0);
        dataModel.setClassIndex(13);

        instance = new DenseInstance(1 + BANDS_NUM);    // bands + class + index
        instance.setDataset(dataModel);
    }

    /**
     * Fabric method.
     * Loads classifier from given path and returns instance
     * @param modelPath path to model binary file
     * @return classifier instance
     * @throws Exception on classifier load error
     */
    public static PixelClassifier loadClassifier(String modelPath) throws Exception {
        Classifier clf = (Classifier) SerializationHelper.read(modelPath);
        Sentinel2PixelClassifier created = new Sentinel2PixelClassifier(clf);
        created.modelPath = modelPath;
        return created;
    }


    @Override
    public int classifyPixel(double[] featureValues) {
        for (int i = 0; i < BANDS_NUM; i++) {
            instance.setValue(i, featureValues[i]);
        }

        double res;
        try {
            res = classifier.classifyInstance(instance);
            return (int) res;
        } catch (Exception ex) {
            res = -1;
        }
        return (int) res;
    }


}

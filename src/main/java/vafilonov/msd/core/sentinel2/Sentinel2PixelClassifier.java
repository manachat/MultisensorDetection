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

public class Sentinel2PixelClassifier implements PixelClassifier {

    private final Classifier classifier;
    private final Instances dataModel;
    private final DenseInstance instance;


    private Sentinel2PixelClassifier(Classifier cl) {
        classifier = cl;

        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("Mark", Arrays.asList("0","1","2","3","4","5")));
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
        dataModel = new Instances("pixels", attributes, 0);
        dataModel.setClassIndex(0);
        
        instance = new DenseInstance(1 + BANDS_NUM);
        instance.setDataset(dataModel);
    }

    public static PixelClassifier loadClassifier(String modelPath) throws Exception {
        Classifier clf = (Classifier) SerializationHelper.read(modelPath);
        Sentinel2PixelClassifier created = new Sentinel2PixelClassifier(clf);
        return created;
    }

    @Override
    public int classifyPixel(double[] featureValues) {
        for (int i = 0; i < BANDS_NUM; i++) {
            instance.setValue(i + 1, featureValues[i]);
            
        }
        double res;
        try {
            res = classifier.classifyInstance(instance);
            instance.setClassValue(res);
            res = instance.classIndex();
        } catch (Exception ex) {
            res = -1;
            System.err.println("classification error" + ex.toString()); //TODO убрать на релиз
        }
        return (int) res;
    }


}

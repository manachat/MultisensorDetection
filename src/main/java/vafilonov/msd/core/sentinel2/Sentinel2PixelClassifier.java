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

    public double getSignificance() {
        return significance;
    }

    public void setSignificance(double significance) {
        this.significance = significance;
    }

    private double significance;


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
        
        instance = new DenseInstance(1 + BANDS_NUM);
        instance.setDataset(dataModel);
    }

    public static PixelClassifier loadClassifier(String modelPath) throws Exception {
        return loadClassifier(modelPath, 0.2);
    }

    public static PixelClassifier loadClassifier(String modelPath, double significance) throws Exception {
        Classifier clf = (Classifier) SerializationHelper.read(modelPath);
        Sentinel2PixelClassifier created = new Sentinel2PixelClassifier(clf);
        created.significance = significance;
        return created;
    }

    /**
     * Возвращает индекс предсказанного класса
     * Если предсказание ниже уровня значимости, возвращает -1
     * @param featureValues
     * @return
     */
    @Override
    public int classifyPixel(double[] featureValues) {
        for (int i = 0; i < BANDS_NUM; i++) {
            instance.setValue(i, featureValues[i]);
            
        }
        double res;
        try {
            res = classifier.classifyInstance(instance);
            double p = classifier.distributionForInstance(instance)[(int)res];

            if (p < significance) {
                return -1;
            } else {
                return (int) res;
            }
        } catch (Exception ex) {
            res = -1;
            System.err.println("classification error" + ex); //TODO убрать на релиз
        }
        return (int) res;
    }


}

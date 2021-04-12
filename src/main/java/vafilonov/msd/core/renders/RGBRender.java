package vafilonov.msd.core.renders;

import vafilonov.msd.core.PixelAction;
import vafilonov.msd.core.RasterDataset;
import vafilonov.msd.core.sentinel2.utils.Constants;
import vafilonov.msd.core.sentinel2.utils.Sentinel2Band;

import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public class RGBRender extends AbstractRender implements PixelAction<ShortBuffer[][], int[]> {
    protected final double redMin, redMax, greenMin, greenMax, blueMin, blueMax;

    public RGBRender(RasterDataset dataset) {
        super(dataset);

        traverseMask = new boolean[]{false,true,true,true,false,false,false,false,false,false,false,false,false};

        double[] redStats = new double[2];
        double[] greenStats = new double[2];
        double[] blueStats = new double[2];


        dataset.getBands()[Sentinel2Band.B4.ordinal()].ComputeBandStats(redStats);
        dataset.getBands()[Sentinel2Band.B3.ordinal()].ComputeBandStats(greenStats);
        dataset.getBands()[Sentinel2Band.B2.ordinal()].ComputeBandStats(blueStats);

        // 3 standard deviations
        int stdnum = 3;
        redMin = redStats[0] - stdnum*redStats[1];
        redMax = redStats[0] + stdnum*redStats[1];
        greenMin = greenStats[0] - stdnum*greenStats[1];
        greenMax = greenStats[0] + stdnum*greenStats[1];
        blueMin = blueStats[0] - stdnum*blueStats[1];
        blueMax = blueStats[0] + stdnum*blueStats[1];

    }

    /**
     * v
     * @param values строка растра для датасетов и их каналов
     * @param params параметры
     */
    @Override
    public void processPixel(ShortBuffer[][] values, int[] params) {
        ShortBuffer[] rows = values[0]; // it is assumed that first dataset is presented
        int rasterRow = params[0];
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

            raster[rasterRow*rasterWidth + i] = value;
        }
    }

}

package vafilonov.msd.core.renders;

import vafilonov.msd.core.PixelAction;
import vafilonov.msd.core.RasterDataset;

import static vafilonov.msd.core.sentinel2.utils.Constants.PIXEL_RESOLUTIONS;

import java.nio.ShortBuffer;

/**
 * Basic class for 3-channel render in RGB-like manner.
 */
public abstract class ThreeChannelRenderer extends AbstractRender implements PixelAction<ShortBuffer[][], int[]> {

    /**
     * Channel statistics
     */
    protected final double redMin, redMax, greenMin, greenMax, blueMin, blueMax;

    /**
     * index of rendered bands
     */
    private final int redIndex, greenIndex, blueIndex;

    /**
     * Constructor.
     * @param dataset dataset to render
     * @param redIndex index for red channel
     * @param greenIndex index for green channel
     * @param blueIndex index for blue channel
     */
    public ThreeChannelRenderer(RasterDataset dataset, int redIndex, int greenIndex, int blueIndex) {
        super(dataset);

        this.redIndex = redIndex;
        this.greenIndex = greenIndex;
        this.blueIndex = blueIndex;

        double[] redStats = new double[2];
        double[] greenStats = new double[2];
        double[] blueStats = new double[2];


        dataset.getBands()[redIndex].ComputeBandStats(redStats);
        dataset.getBands()[greenIndex].ComputeBandStats(greenStats);
        dataset.getBands()[blueIndex].ComputeBandStats(blueStats);

        // 3 standard deviations
        int stdnum = 3;
        redMin = redStats[0] - stdnum*redStats[1];
        redMax = redStats[0] + stdnum*redStats[1];
        greenMin = greenStats[0] - stdnum*greenStats[1];
        greenMax = greenStats[0] + stdnum*greenStats[1];
        blueMin = blueStats[0] - stdnum*blueStats[1];
        blueMax = blueStats[0] + stdnum*blueStats[1];
    }

    @Override
    public void processPixel(ShortBuffer[][] values, int[] params) {
        ShortBuffer[] rows = values[0];
        int rasterRow = params[0];
        for (int i = 0; i < rasterWidth; i++) {

            int r = (int) ((rows[redIndex].get(i * 10 / PIXEL_RESOLUTIONS[redIndex]) - redMin) * 255 / (redMax - redMin));
            int g = (int) ((rows[greenIndex].get(i * 10 / PIXEL_RESOLUTIONS[greenIndex]) - greenMin) * 255 / (greenMax - greenMin));
            int b = (int) ((rows[blueIndex].get(i * 10 / PIXEL_RESOLUTIONS[blueIndex]) - blueMin) * 255 / (blueMax - blueMin));

            r = Math.max(0, r);
            r = Math.min(255, r);
            g = Math.max(0, g);
            g = Math.min(255, g);
            b = Math.max(0, b);
            b = Math.min(255, b);

            int value = 255 << 24;
            value = value | r << 16;
            value = value | g << 8;
            value = value | b;

            value = modifyPixel(rasterRow, i, value, values, params);

            raster[rasterRow * rasterWidth + i] = value;
        }
    }

    /**
     * Modifies pixel color during traversl
     * @param i row index
     * @param j column index
     * @param colorValue initial color value
     * @param values pixel values
     * @param params additional parameters
     * @return modified color
     */
    protected int modifyPixel(int i, int j, int colorValue, ShortBuffer[][] values, int[] params) {
        return colorValue;
    }

}

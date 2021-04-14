package vafilonov.msd.core.renders;

import vafilonov.msd.core.RasterDataset;

import java.util.Arrays;

/**
 * Base class for render implementations
 */
public abstract class AbstractRender {

    /**
     * Constructor.
     * Creates raster matrix from dataset parameters.
     * @param set dataset to render
     */
    public AbstractRender(RasterDataset set) {
        int[] offs = set.computeOffsets();
        System.out.println(Arrays.toString(offs));
        int width = offs[0];
        int height = offs[1];
        int rasterWidth = width / 10;
        int rasterHeight = height / 10;
        raster = new int[rasterWidth*rasterHeight];
        this.rasterWidth = rasterWidth;
        this.rasterHeight = rasterHeight;
    }

    /**
     * raster matrix
     */
    protected int[] raster;

    /**
     * raster width
     */
    protected int rasterWidth;

    /**
     * raster height
     */
    protected int rasterHeight;

    /**
     * traversal mask
     */
    protected boolean[] traverseMask = {true, true, true, true, true, true, true, true, true, true, true, true, true};

    /**
     * raster getter method
     * @return raster matrix
     */
    public int[] getRaster() {
        return raster;
    }

    /**
     * raster width getter
     * @return raster width
     */
    public int getRasterWidth() {
        return rasterWidth;
    }

    /**
     * raster height getter
     * @return raster height
     */
    public int getRasterHeight() {
        return rasterHeight;
    }

    /**
     * mask getter
     * @return traversal mask
     */
    public boolean[] getTraverseMask() {
        return traverseMask;
    }
}

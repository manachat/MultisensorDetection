package vafilonov.msd.core.renders;

import vafilonov.msd.core.RasterDataset;

import java.util.Arrays;

public abstract class AbstractRender {

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

    protected int[] raster;

    protected int rasterWidth;
    protected int rasterHeight;

    protected boolean[] traverseMask = {true, true, true, true, true, true, true, true, true, true, true, true, true};

    public int[] getRaster() {
        return raster;
    }

    public int getRasterWidth() {
        return rasterWidth;
    }

    public int getRasterHeight() {
        return rasterHeight;
    }

    public boolean[] getTraverseMask() {
        return traverseMask;
    }
}

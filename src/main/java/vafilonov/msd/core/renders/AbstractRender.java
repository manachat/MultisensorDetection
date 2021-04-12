package vafilonov.msd.core.renders;

import vafilonov.msd.core.RasterDataset;

public abstract class AbstractRender {

    public AbstractRender(RasterDataset set) {
        int[] offs = set.computeOffsets();
        int width = offs[0];
        int height = offs[1];
        int rasterWidth = width / 10;
        int rasterHeight = height / 10;
        raster = new int[offs[0]*offs[1]];
        this.rasterWidth = rasterWidth;
        this.rasterHeight = rasterHeight;
    }

    protected int[] raster;

    protected int rasterWidth;
    protected int rasterHeight;

    public int[] getRaster() {
        return raster;
    }

    public int getRasterWidth() {
        return rasterWidth;
    }

    public int getRasterHeight() {
        return rasterHeight;
    }
}

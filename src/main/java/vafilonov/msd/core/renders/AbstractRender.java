package vafilonov.msd.core.renders;

public abstract class AbstractRender {

    protected int[] raster;

    protected int width;
    protected int height;

    public int[] getRaster() {
        return raster;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

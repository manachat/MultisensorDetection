package vafilonov.msd.core;

public interface RasterTraverser<V, P> {
    void traverseRaster(PixelAction<V, P> action, RasterDataset[] sets, boolean[] traverseMask);
}

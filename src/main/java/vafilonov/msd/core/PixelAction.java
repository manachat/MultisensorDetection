package vafilonov.msd.core;

/**
 * Action performed on raster pixel
 * @param <V> pixel values type
 * @param <P> additional parameters type
 */
public interface PixelAction<V,P>{
    /**
     * Performs an action on pixel
     * @param values pixel values
     * @param params parameters
     */
    void processPixel(V values, P params);
}

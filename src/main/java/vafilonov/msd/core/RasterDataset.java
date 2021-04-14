package vafilonov.msd.core;

import org.gdal.gdal.Band;

/**
 * Presentation of bands collection for some raster data
 */
public interface RasterDataset {

    /**
     * Returns bands associated with dataset
     * @return bands
     */
    Band[] getBands();

    /**
     * computes offsets for bands' rasters
     * @return
     */
    int[] computeOffsets();

    /**
     * Frees resources associated with dataset
     */
    void delete();

    /**
     * Tells if dataset was already disposed
     * @return disposal state
     */
    boolean isDisposed();

}

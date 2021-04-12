package vafilonov.msd.core;

import org.gdal.gdal.Band;

public interface RasterDataset {

    Band[] getBands();

    int[] computeOffsets();

    void delete();

    boolean isDisposed();

}

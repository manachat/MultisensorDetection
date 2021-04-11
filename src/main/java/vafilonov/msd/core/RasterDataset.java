package vafilonov.msd.core;

import org.gdal.gdal.Band;

public interface RasterDataset {

    public abstract Band[] getAvailableBands();

    public abstract int[] computeOffsets();

}

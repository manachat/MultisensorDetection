package vafilonov.msd.core.renders;

import vafilonov.msd.core.RasterDataset;
import vafilonov.msd.core.sentinel2.utils.Sentinel2Band;

/**
 * Render for geological presentation.
 * Utilizes B12, B11 and B2.
 */
public class GeologyRenderer extends ThreeChannelRenderer {

    /**
     * Constructor.
     * @param dataset dataset to render
     */
    public GeologyRenderer(RasterDataset dataset) {
        super(dataset, Sentinel2Band.B12.ordinal(), Sentinel2Band.B11.ordinal(), Sentinel2Band.B2.ordinal());
        traverseMask = new boolean[]{false,true,false,false,false,false,false,false,false,false,false,true,true};
    }
}

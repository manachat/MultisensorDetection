package vafilonov.msd.core.renders;

import vafilonov.msd.core.RasterDataset;
import vafilonov.msd.core.sentinel2.utils.Sentinel2Band;

/**
 * render for agricultural presentation.
 * Utilises B11, B8 and B2
 */
public class AgricultureRender extends ThreeChannelRenderer {

    /**
     * Constructor.
     * @param dataset dataset to render
     */
    public AgricultureRender(RasterDataset dataset) {
        super(dataset, Sentinel2Band.B11.ordinal(), Sentinel2Band.B8.ordinal(), Sentinel2Band.B2.ordinal());
        traverseMask = new boolean[]{false,true,false,false,false,false,false,true,false,false,false,true,false};
    }
}

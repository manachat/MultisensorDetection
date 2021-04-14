package vafilonov.msd.core.renders;

import vafilonov.msd.core.RasterDataset;
import vafilonov.msd.core.sentinel2.utils.Sentinel2Band;

/**
 * Render of visible light spectre presentation.
 * Utilizes B4, B3 and B2
 */
public class RGBRender extends ThreeChannelRenderer {

    /**
     * Constructor.
     * @param dataset dataset to render
     */
    public RGBRender(RasterDataset dataset) {
        super(dataset, Sentinel2Band.B4.ordinal(), Sentinel2Band.B3.ordinal(), Sentinel2Band.B2.ordinal());
        traverseMask = new boolean[]{false,true,true,true,false,false,false,false,false,false,false,false,false};
    }

}

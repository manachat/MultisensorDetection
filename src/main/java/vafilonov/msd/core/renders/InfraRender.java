package vafilonov.msd.core.renders;

import vafilonov.msd.core.RasterDataset;
import vafilonov.msd.core.sentinel2.utils.Sentinel2Band;

/**
 * render for infrared presentation.
 * Utilizes B8, B4 and B3
 */
public class InfraRender extends ThreeChannelRenderer{

    /**
     * Constructor.
     * @param dataset dataset to render
     */
    public InfraRender(RasterDataset dataset) {
        super(dataset, Sentinel2Band.B8.ordinal(), Sentinel2Band.B4.ordinal(), Sentinel2Band.B3.ordinal());
        traverseMask = new boolean[]{false,false,true,true,false,false,false,true,false,false,false,false,false};
    }

}

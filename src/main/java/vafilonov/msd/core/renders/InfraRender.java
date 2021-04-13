package vafilonov.msd.core.renders;

import vafilonov.msd.core.RasterDataset;
import vafilonov.msd.core.sentinel2.utils.Sentinel2Band;

public class InfraRender extends ThreeChannelRenderer{

    public InfraRender(RasterDataset dataset) {
        super(dataset, Sentinel2Band.B8.ordinal(), Sentinel2Band.B4.ordinal(), Sentinel2Band.B3.ordinal());
        traverseMask = new boolean[]{false,false,true,true,false,false,false,true,false,false,false,false,false};
    }

}

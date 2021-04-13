package vafilonov.msd.core.renders;

import vafilonov.msd.core.RasterDataset;
import vafilonov.msd.core.sentinel2.utils.Sentinel2Band;

public class AgricultureRender extends ThreeChannelRenderer {
    public AgricultureRender(RasterDataset dataset) {
        super(dataset, Sentinel2Band.B11.ordinal(), Sentinel2Band.B8.ordinal(), Sentinel2Band.B2.ordinal());
        traverseMask = new boolean[]{false,true,false,false,false,false,false,true,false,false,false,true,false};
    }
}

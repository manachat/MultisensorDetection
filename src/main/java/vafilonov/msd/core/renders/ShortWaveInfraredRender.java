package vafilonov.msd.core.renders;

import vafilonov.msd.core.RasterDataset;
import vafilonov.msd.core.sentinel2.utils.Sentinel2Band;

/**
 * Render for short-wave infrared (SWIR) spectre.
 * Utilizes B12, B8A and B4
 */
public class ShortWaveInfraredRender extends ThreeChannelRenderer{

    public ShortWaveInfraredRender(RasterDataset dataset) {
        super(dataset, Sentinel2Band.B12.ordinal(), Sentinel2Band.B8A.ordinal(), Sentinel2Band.B4.ordinal());
        traverseMask = new boolean[]{false,false,false,true,false,false,false,false,true,false,false,false,true};
    }
}

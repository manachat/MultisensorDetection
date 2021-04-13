package vafilonov.msd.core.renders;

import vafilonov.msd.core.PixelAction;
import vafilonov.msd.core.RasterDataset;
import vafilonov.msd.core.sentinel2.utils.Constants;
import vafilonov.msd.core.sentinel2.utils.Sentinel2Band;

import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public class RGBRender extends ThreeChannelRenderer {

    public RGBRender(RasterDataset dataset) {
        super(dataset, Sentinel2Band.B4.ordinal(), Sentinel2Band.B3.ordinal(), Sentinel2Band.B2.ordinal());
        traverseMask = new boolean[]{false,true,true,true,false,false,false,false,false,false,false,false,false};
    }

}

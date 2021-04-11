package vafilonov.msd.core;

public interface TwoPixelAction extends PixelAction {

    void processPxielPair(RasterDataset first, RasterDataset second);
}

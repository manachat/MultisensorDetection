package vafilonov.msd.core;

public interface PixelAction<V,P>{

    void processPixel(V values, P params);
}

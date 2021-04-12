package vafilonov.msd.core;

public interface TwoPixelAction<V, P> extends PixelAction<V, P> {

    void processPixelPair(V values1, V values2, P params);
}

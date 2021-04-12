package vafilonov.msd.controllers;

import vafilonov.msd.core.RasterDataset;

import java.util.ArrayList;
import java.util.List;

public class RasterDatasetManager {

    List<RasterDataset> sets = new ArrayList<>();

    public void deleteSet(RasterDataset set) {
        if (set != null && !set.isDisposed()) {
            set.delete();
        }
        sets.remove(set);

    }

    public void deleteDatasets() {
        for (var set : sets) {
            if (set != null && !set.isDisposed()) {
                set.delete();
            }
        }
        sets.clear();
    }

    public void addSet(RasterDataset set) {
        if (!sets.contains(set))
            sets.add(set);
    }

}

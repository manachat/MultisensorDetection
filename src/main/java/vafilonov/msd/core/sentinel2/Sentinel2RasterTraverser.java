package vafilonov.msd.core.sentinel2;

import org.gdal.gdal.Band;
import org.gdal.gdalconst.gdalconst;
import vafilonov.msd.core.PixelAction;
import vafilonov.msd.core.RasterDataset;
import vafilonov.msd.core.RasterTraverser;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.Arrays;

import static vafilonov.msd.core.sentinel2.utils.Constants.BANDS_NUM;
import static vafilonov.msd.core.sentinel2.utils.Constants.PIXEL_RESOLUTIONS;

public class Sentinel2RasterTraverser implements RasterTraverser<ShortBuffer[][],int[]> {

    @Override
    public void traverseRaster(PixelAction<ShortBuffer[][], int[]> action, RasterDataset[] sets, boolean[] traverseMask) {

        Sentinel2RasterDataset[] datasets = new Sentinel2RasterDataset[sets.length];
        for (int i = 0; i < sets.length; i++) {
            if (sets[i] != null && sets[i] instanceof Sentinel2RasterDataset) {
                datasets[i] = (Sentinel2RasterDataset) sets[i];
            } else {
                throw new IllegalArgumentException("Set is either null or has incompatible type");
            }
        }

        Band[][] bands = new Band[datasets.length][];
        double[][][] transforms = new double[datasets.length][][];
        ByteBuffer[][] buffers = new ByteBuffer[sets.length][];
        ShortBuffer[][] shorts = new ShortBuffer[sets.length][];
        for (int i = 0; i < bands.length; i++) {
            bands[i] = datasets[i].getBands();
            transforms[i] = datasets[i].getGeoTransforms();
            buffers[i] = new ByteBuffer[BANDS_NUM];
            shorts[i] = new ShortBuffer[BANDS_NUM];
        }

        int[] offsets = sets[0].computeOffsets();
        int width = offsets[0];
        int height = offsets[1];
        int xOffset10 = offsets[2];
        int yOffset10 = offsets[3];
        int xOffset20 = offsets[4];
        int yOffset20 = offsets[5];
        int xOffset60 = offsets[6];
        int yOffset60 = offsets[7];

        int rasterWidth = width / 10;
        int rasterHeight = height / 10;

        // allocate buffers for bands
        for (int i = 0; i < datasets.length; i++) {
            for (int j = 0; j < BANDS_NUM; j++) {
                if (bands[i][j] != null && traverseMask[j]) {

                    buffers[i][j] = ByteBuffer.allocateDirect(2*bands[i][j].GetXSize()).order(ByteOrder.nativeOrder());
                    shorts[i][j] = buffers[i][j].asShortBuffer();
                }
            }
        }

        int[] params = new int[2];

        for (int y = 0; y < height; y += 10) {
            params[0] = y/10; // rgb resolution

            for (int i = 0; i < datasets.length; i++) {
                for (int j = 0; j < BANDS_NUM; j++) {

                    if (!traverseMask[j] || bands[i][j] == null) {
                        continue;
                    }

                    if (PIXEL_RESOLUTIONS[j] == 10) {
                        buffers[i][j].clear();
                        bands[i][j].ReadRaster_Direct(xOffset10 / PIXEL_RESOLUTIONS[j], (yOffset10 + y) / PIXEL_RESOLUTIONS[j],
                                width / PIXEL_RESOLUTIONS[j], 1, width / PIXEL_RESOLUTIONS[j], 1,
                                gdalconst.GDT_Int16, buffers[i][j]);


                    } else if (PIXEL_RESOLUTIONS[j] == 20) {
                        if ((yOffset20 + y) % PIXEL_RESOLUTIONS[j] == 0 || y == 0) {
                            buffers[i][j].clear();
                            bands[i][j].ReadRaster_Direct(xOffset20 / PIXEL_RESOLUTIONS[j], (yOffset20 + y) / PIXEL_RESOLUTIONS[j],
                                    width / PIXEL_RESOLUTIONS[j], 1, width / PIXEL_RESOLUTIONS[j], 1,
                                    gdalconst.GDT_Int16, buffers[i][j]);

                        }

                    } else {
                        if ((yOffset60 + y) % PIXEL_RESOLUTIONS[j] == 0 || y == 0) {
                            buffers[i][j].clear();
                            bands[i][j].ReadRaster_Direct(xOffset60 / PIXEL_RESOLUTIONS[j], (yOffset60 + y) / PIXEL_RESOLUTIONS[j],
                                    width / PIXEL_RESOLUTIONS[j], 1, width / PIXEL_RESOLUTIONS[j], 1,
                                    gdalconst.GDT_Int16, buffers[i][j]);

                        }

                    }

                }   // for-bands
            }   // for-datasets

            params[1] = 0;
            action.processPixel(shorts, params);
            /*
            for (int x = 0; x < width; x += 10) {
                params[1] = x;
                action.processPixel(shorts, params);

            }
             */

        }   //  for-y

    }
}

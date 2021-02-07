package vafilonov.msd.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;

import java.util.Arrays;


public class MainSceneController {




    @FXML
    private Label label;

    @FXML
    private Button button;

    @FXML
    private TextArea area;

    @FXML
    void buttonClickHandler(MouseEvent e) {
        gdal.AllRegister();
        Dataset set1 = gdal.Open("/home/vfilonov/programming/geodata/clipped_2020_MSK.tif", gdalconstConstants.GA_ReadOnly);
        Dataset set2 = gdal.Open("/home/vfilonov/programming/geodata/clipped_2017_MSK.tif", gdalconstConstants.GA_ReadOnly);
        if (set1 == null || set2 == null) {
            label.setText("You are such a failure");
        } else {
            area.appendText(set1.getRasterXSize() + " " + set1.getRasterYSize() + '\n' +
                    set1.GetDriver().getShortName() + set1.GetRasterCount() + '\n');
            double[] props = set1.GetGeoTransform();
            if (props != null) {
                area.appendText(Arrays.toString(props) + '\n');
            }
            var meta = set1.GetMetadata_List("");
            for (var el : meta) {
                area.appendText(el.toString() + " ");
            }

            area.appendText("\n\n");

            area.appendText(set2.getRasterXSize() + " " + set2.getRasterYSize() + '\n' +
                    set2.GetDriver().getShortName() + set2.GetProjection() + '\n');
            area.appendText(Arrays.toString(set1.GetGeoTransform()) + '\n');

            var meta2 = set2.GetMetadata_List("");
            for (var el : meta2) {
                area.appendText(el.toString() + " ");
            }

            GDALInfoReportCorner(set1, "Upper Left ", 0.0, 0.0);
            GDALInfoReportCorner(set1, "Lower Left ", 0.0, set1.getRasterYSize());
            GDALInfoReportCorner(set1, "Upper Right", set1.getRasterXSize(), 0.0);
            GDALInfoReportCorner(set1, "Lower Right", set1.getRasterXSize(), set1.getRasterYSize());
            System.out.println("\n");
            GDALInfoReportCorner(set2, "Upper Left ", 0.0, 0.0);
            GDALInfoReportCorner(set2, "Lower Left ", 0.0, set1.getRasterYSize());
            GDALInfoReportCorner(set2, "Upper Right", set2.getRasterXSize(), 0.0);
            GDALInfoReportCorner(set2, "Lower Right", set2.getRasterXSize(), set2.getRasterYSize());
            set1.delete();
            set2.delete();
        }

    }

    static boolean GDALInfoReportCorner(Dataset hDataset, String corner_name,
                                        double x, double y)

    {
        double dfGeoX, dfGeoY;
        String pszProjection;
        double[] adfGeoTransform = new double[6];
        CoordinateTransformation hTransform = null;

        System.out.print(corner_name + " ");

        /* -------------------------------------------------------------------- */
        /*      Transform the point into georeferenced coordinates.             */
        /* -------------------------------------------------------------------- */
        hDataset.GetGeoTransform(adfGeoTransform);
        {
            pszProjection = hDataset.GetProjectionRef();

            dfGeoX = adfGeoTransform[0] + adfGeoTransform[1] * x
                    + adfGeoTransform[2] * y;
            dfGeoY = adfGeoTransform[3] + adfGeoTransform[4] * x
                    + adfGeoTransform[5] * y;
        }

        if (adfGeoTransform[0] == 0 && adfGeoTransform[1] == 0
                && adfGeoTransform[2] == 0 && adfGeoTransform[3] == 0
                && adfGeoTransform[4] == 0 && adfGeoTransform[5] == 0) {
            System.out.println("(" + x + "," + y + ")");
            return false;
        }

        /* -------------------------------------------------------------------- */
        /*      Report the georeferenced coordinates.                           */
        /* -------------------------------------------------------------------- */
        System.out.print("(" + dfGeoX + "," + dfGeoY + ") ");

        /* -------------------------------------------------------------------- */
        /*      Setup transformation to lat/long.                               */
        /* -------------------------------------------------------------------- */
        if (pszProjection != null && pszProjection.length() > 0) {
            SpatialReference hProj, hLatLong = null;

            hProj = new SpatialReference(pszProjection);
            if (hProj != null)
                hLatLong = hProj.CloneGeogCS();

            if (hLatLong != null) {
                /* New in GDAL 1.10. Before was "new CoordinateTransformation(srs,dst)". */
                hTransform = CoordinateTransformation.CreateCoordinateTransformation(hProj, hLatLong);
            }

            if (hProj != null)
                hProj.delete();
        }

        /* -------------------------------------------------------------------- */
        /*      Transform to latlong and report.                                */
        /* -------------------------------------------------------------------- */
        if (hTransform != null) {
            double[] transPoint = new double[3];
            hTransform.TransformPoint(transPoint, dfGeoX, dfGeoY, 0);
            System.out.print("(" + gdal.DecToDMS(transPoint[0], "Long", 2));
            System.out
                    .print("," + gdal.DecToDMS(transPoint[1], "Lat", 2) + ")");
        }

        if (hTransform != null)
            hTransform.delete();

        System.out.println("");

        return true;
    }

}

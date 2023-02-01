package org.openstreetmap.josm.plugins.osm2streets;

import org.openstreetmap.josm.actions.mapmode.MapMode;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.ProjectionBounds;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.layer.MapViewPaintable;
import org.openstreetmap.josm.io.OsmWriter;
import org.openstreetmap.josm.io.OsmWriterFactory;
import org.openstreetmap.josm.tools.Logging;

import org.osm2streets.StreetNetwork;

import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.logging.Logger;

import static org.openstreetmap.josm.tools.I18n.tr;

public class StreetsMode  extends MapMode implements MouseListener, MouseMotionListener,
        MapViewPaintable {

    private StreetNetwork streets;

    public StreetsMode() {
        super(
            tr("Street Editing"),
            "streetsmode.png",
            tr("Visualise and edit streets and lanes"),
            // shortcut,
            Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
        );

        Logging.info("Hello streets editing mode!");
    }

    @Override
    public void paint(Graphics2D g, MapView mv, Bounds bbox) {
        if (mv == null || mv.getScale() > 16) return;

        if (streets == null) return;

        ProjectionBounds bounds = mv.getProjectionBounds();

        // Draw the road surface polygons.
        for (var surface: streets.getSurfaces()) {
            var n = surface.area.size();
            var xs = new int[n];
            var ys = new int[n];
            for (int i = 0; i < n; i++) {
                var loc = surface.area.get(i);
                var point = mv.getPoint(new LatLon(loc.lat, loc.lon));
                xs[i] = point.x;
                ys[i] = point.y;
            }

            switch (surface.material) {
                default:
                case "asphalt":      g.setColor(new Color( 77,  77,  77, 180)); break;
                case "fine_asphalt": g.setColor(new Color( 99,  55,  55, 180)); break;
                case "concrete":     g.setColor(new Color(141, 141, 141, 180)); break;
            }
            g.fillPolygon(xs, ys, n);
        }

        // Draw the paint polygons.
        for (var paint: streets.getPaintAreas()) {
            var n = paint.area.size();
            var xs = new int[n];
            var ys = new int[n];
            for (int i = 0; i < n; i++) {
                var loc = paint.area.get(i);
                var point = mv.getPoint(new LatLon(loc.lat, loc.lon));
                xs[i] = point.x;
                ys[i] = point.y;
            }

            switch (paint.color) {
                default:
                case "white":  g.setColor(new Color(255, 255, 255, 180)); break;
                case "yellow": g.setColor(new Color(255, 211,  49, 180)); break;
            }
            g.fillPolygon(xs, ys, n);
        }
    }

    @Override
    public void enterMode() {
        super.enterMode();

        if (getLayerManager().getEditDataSet() == null) return;

        streets = StreetNetwork.create(getCurrentLayerXml());

        MapFrame map = MainApplication.getMap();
//        map.mapView.addMouseListener(this);
        map.mapView.addTemporaryLayer(this);
//        updateStatusLine();
    }

    @Override
    public void exitMode() {
        super.exitMode();

        MapFrame map = MainApplication.getMap();
//        map.mapView.removeMouseListener(this);
        map.mapView.removeTemporaryLayer(this);
    }

    private String getCurrentLayerXml() {
        StringWriter str = new StringWriter();
        PrintWriter writer = new PrintWriter(str);
        OsmWriter osmWriter = OsmWriterFactory.createOsmWriter(writer, false, OsmWriter.DEFAULT_API_VERSION);
        osmWriter.write(MainApplication.getLayerManager().getEditDataSet());
        return str.toString();
    }
}

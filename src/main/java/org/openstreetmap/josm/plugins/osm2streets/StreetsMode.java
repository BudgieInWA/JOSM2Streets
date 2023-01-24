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

        ProjectionBounds bounds = mv.getProjectionBounds();

        Logging.info(bbox.toString());
        Logging.info(bounds.toString());

        // DEBUG: Draw a diagonal line across the viewport, so we know this code is active.
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(10));

        var a = mv.getPoint(bounds.getMin());
        var b = mv.getPoint(bounds.getMax());
        g.drawLine(a.x, a.y, b.x, b.y);

        if (streets == null) return;

        // Draw the road surface polygons.
        g.setColor(Color.DARK_GRAY);

        var surfaces = streets.getRoadSurface();
        for (var polygon: surfaces) {
            var n = polygon.size();
            var xs = new int[n];
            var ys = new int[n];
            for (int i = 0; i < n; i++) {
                var loc = polygon.get(i);
                var point = mv.getPoint(new LatLon(loc.lat, loc.lon));
                xs[i] = point.x;
                ys[i] = point.y;
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

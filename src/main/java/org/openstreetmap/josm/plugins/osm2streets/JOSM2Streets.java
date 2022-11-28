package org.openstreetmap.josm.plugins.osm2streets;

import org.openstreetmap.josm.data.Preferences;
import org.openstreetmap.josm.gui.IconToggleButton;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.gui.layer.MainLayerManager.ActiveLayerChangeEvent;
import org.openstreetmap.josm.gui.layer.MainLayerManager.ActiveLayerChangeListener;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

import java.io.IOException;

public class JOSM2Streets extends Plugin implements ActiveLayerChangeListener {

    private final String pluginDir = Preferences.main().getPluginsDirectory().toString();

    /**
     * Constructor for the plug-in.
     *
     * @param info general information about the plug-in
     */
    public JOSM2Streets(PluginInformation info) throws IOException {
        super(info);
//        MainApplication.getLayerManager().addAndFireActiveLayerChangeListener(this);
    }

    @Override
    public void mapFrameInitialized(MapFrame oldFrame, MapFrame newFrame) {
        super.mapFrameInitialized(oldFrame, newFrame);

        if (oldFrame == null && newFrame != null) {
            MainApplication.getMap().addMapMode(new IconToggleButton(new StreetsMode()));
        }
    }

    @Override
    public void activeOrEditLayerChanged(ActiveLayerChangeEvent e) {
        OsmDataLayer editLayer = MainApplication.getLayerManager().getEditLayer();
        if (editLayer != null) {
//            editLayer.addInvalidationListener(this);
        }
    }

}

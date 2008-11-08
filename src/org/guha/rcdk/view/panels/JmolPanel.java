package org.guha.rcdk.view.panels;

import org.jmol.api.JmolAdapter;
import org.jmol.api.JmolViewer;
import org.guha.rcdk.view.CdkJmolAdapter;

import javax.swing.*;
import java.awt.*;


public class JmolPanel extends JPanel {

    private static final long serialVersionUID = 4098187935404173916L;

    JmolViewer viewer;
    JmolAdapter adapter;

    public JmolPanel() {
        // use CDK IO
        adapter = new CdkJmolAdapter(null);
        viewer = JmolViewer.allocateViewer(this, adapter);
    }

    public JmolViewer getViewer() {
        return viewer;
    }

    final Dimension currentSize = new Dimension();
    final Rectangle rectClip = new Rectangle();

    public void paint(Graphics g) {
        viewer.setScreenDimension(getSize(currentSize));
        g.getClipBounds(rectClip);
        viewer.renderScreenImage(g, currentSize, rectClip);
    }
}


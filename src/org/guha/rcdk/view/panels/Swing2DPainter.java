package org.guha.rcdk.view.panels;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.renderer.Java2DRenderer;
import org.openscience.cdk.renderer.Renderer2DModel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * @cdk.author Rajarshi Guha
 * @cdk.svnrev $Revision: 9162 $
 */
public class Swing2DPainter extends JComponent {
    private Renderer2DModel rendererModel = new Renderer2DModel();
    private Java2DRenderer renderer = new Java2DRenderer(rendererModel);
    private IMolecule molecule;
    private Graphics2D graphics;
    AffineTransform affinelast = new AffineTransform();

    public Swing2DPainter() {
        this(300, 300);
    }

    public Swing2DPainter(int x, int y) {
        Dimension screenSize = new Dimension(x, y);
        setPreferredSize(screenSize);

        rendererModel.setScaleFactor(1);
        rendererModel.setBackgroundDimension(screenSize);
        rendererModel.setDrawNumbers(false);
        rendererModel.setUseAntiAliasing(true);
        rendererModel.setColorAtomsByType(true);
        rendererModel.setShowImplicitHydrogens(false);
        rendererModel.setShowAromaticity(true);
    }

    public void setMolecule(IMolecule molecule) {
        this.molecule = molecule;
    }

    public Java2DRenderer getRenderer() {
        return this.renderer;
    }

    public Renderer2DModel getModel() {
        return this.rendererModel;
    }

    public IMolecule getMolecule() {
        return this.molecule;
    }

    public Graphics2D getGraphics2D() {
        return this.graphics;
    }

    public void paint(Graphics g) {
        super.paint(g);

        graphics = (Graphics2D) g;
        rendererModel.setZoomFactor(rendererModel.getScaleFactor());
        Color bg = rendererModel.getBackColor();
        g.setColor(bg);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (!affinelast.equals(graphics.getTransform())) {
            affinelast = graphics.getTransform();
        }
        renderer.paintMolecule(molecule, (Graphics2D) g, getBounds());
    }


}


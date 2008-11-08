package org.guha.rcdk.view.panels;

import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.Java2DRenderer;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import javax.swing.*;
import java.awt.*;

/**
 * @author Rajarshi Guha
 */
public class Render2DPanel extends JPanel {
    IAtomContainer mol;
    boolean withHydrogen = true;

    Renderer2DModel r2dm;
    Java2DRenderer renderer;

    public Render2DPanel() {
    }

    public Render2DPanel(IAtomContainer mol, int x, int y, boolean withHydrogen) {
        this.mol = AtomContainerManipulator.removeHydrogens(mol);
        try {
            CDKHueckelAromaticityDetector.detectAromaticity(this.mol);
        } catch (CDKException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        r2dm = new Renderer2DModel();
        renderer = new Java2DRenderer(r2dm);
        Dimension screenSize = new Dimension(x, y);
        setPreferredSize(screenSize);
        r2dm.setBackgroundDimension(screenSize); // make sure it is synched with the JPanel size
        setBackground(r2dm.getBackColor());

        this.withHydrogen = withHydrogen;

        try {
            StructureDiagramGenerator sdg = new StructureDiagramGenerator();
            sdg.setMolecule((IMolecule) this.mol);
            sdg.generateCoordinates();
            this.mol = sdg.getMolecule();

            r2dm.setDrawNumbers(false);
            r2dm.setUseAntiAliasing(true);
            r2dm.setColorAtomsByType(true);
            r2dm.setShowImplicitHydrogens(true);
            r2dm.setShowAromaticity(true);
            r2dm.setShowReactionBoxes(false);
            r2dm.setKekuleStructure(false);

            GeometryTools.translateAllPositive(this.mol);
            GeometryTools.scaleMolecule(this.mol, getPreferredSize(), 0.8);
            GeometryTools.center(this.mol, getPreferredSize());

        }
        catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        renderer.paintMolecule(mol, (Graphics2D) g, getBounds());
    }

}

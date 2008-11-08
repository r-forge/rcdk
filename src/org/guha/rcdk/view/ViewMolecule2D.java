package org.guha.rcdk.view;

import org.guha.rcdk.util.Misc;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.Java2DRenderer;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class ViewMolecule2D extends JPanel {
    IAtomContainer molecule;
    JFrame frame;

    Renderer2DModel r2dm;
    Java2DRenderer renderer;

    int width = 300;
    int height = 300;
    double scale = 0.9;

    class ApplicationCloser extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            frame.dispose();
        }
    }

    public ViewMolecule2D(IAtomContainer molecule) throws CDKException {
        this.molecule = molecule;

        if (!ConnectivityChecker.isConnected(molecule)) throw new CDKException("Molecule must be connected");

        frame = new JFrame("2D Structure Viewer");
        frame.addWindowListener(new ApplicationCloser());
        frame.setSize(300, 300);

    }

    public void paint(Graphics g) {
        super.paint(g);
        renderer.paintMolecule(molecule, (Graphics2D) g, getBounds());
    }

    public void draw() {
        molecule = AtomContainerManipulator.removeHydrogens(molecule);
        try {
            CDKHueckelAromaticityDetector.detectAromaticity(molecule);
        } catch (CDKException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        r2dm = new Renderer2DModel();
        renderer = new Java2DRenderer(r2dm);
        Dimension screenSize = new Dimension(this.width, this.height);
        setPreferredSize(screenSize);
        r2dm.setBackgroundDimension(screenSize); // make sure it is synched with the JPanel size
        setBackground(r2dm.getBackColor());

        try {
            StructureDiagramGenerator sdg = new StructureDiagramGenerator();
            sdg.setMolecule((IMolecule) molecule);
            sdg.generateCoordinates();
            molecule = sdg.getMolecule();

            r2dm.setDrawNumbers(false);
            r2dm.setUseAntiAliasing(true);
            r2dm.setColorAtomsByType(true);
            r2dm.setShowImplicitHydrogens(true);
            r2dm.setShowAromaticity(true);
            r2dm.setShowReactionBoxes(false);
            r2dm.setKekuleStructure(false);

            GeometryTools.translateAllPositive(molecule);
            GeometryTools.scaleMolecule(molecule, getPreferredSize(), this.scale);
            GeometryTools.center(molecule, getPreferredSize());

        }
        catch (Exception exc) {
            exc.printStackTrace();
        }
        frame.getContentPane().add(this);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] arg) throws CDKException {
        String home = "/home/rguha/";
        String[] fname = {home + "src/R/trunk/rcdk/data/dan001.sdf",
                home + "src/R/trunk/rcdk/data/dan002.sdf",
                home + "src/R/trunk/rcdk/data/dan003.sdf"};
        IAtomContainer[] acs = null;
        try {
            acs = Misc.loadMolecules(fname, true, true);
        } catch (CDKException e) {
            e.printStackTrace();
        }

        ViewMolecule2D v2d = new ViewMolecule2D(acs[1]);

        v2d.draw();
    }
}

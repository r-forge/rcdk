package org.guha.rcdk.view;

import org.guha.rcdk.view.panels.Swing2DPainter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class ViewMolecule2Dv2 extends JFrame {

    int width = 300;
    int height = 300;
    double scale = 0.9;
    Swing2DPainter painter;

    static class ApplicationCloser extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            // we don't want to exit R!!
        }
    }

    public ViewMolecule2Dv2(IAtomContainer atomContainer) throws Exception {
        this(atomContainer, 300, 300);
    }

    public ViewMolecule2Dv2(IAtomContainer molecule, int x, int y) throws Exception {
        this.width = x;
        this.height = y;
        this.setTitle("2D Structure Viewer");
        this.addWindowListener(new ApplicationCloser());
        this.setSize(width, height);

        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        sdg.setMolecule((IMolecule) molecule);
        sdg.generateCoordinates();
        painter = new Swing2DPainter(width, height);
        painter.setMolecule(sdg.getMolecule());
        painter.setBackground(Color.WHITE);

        this.add(painter);
    }

    public void run() {
        this.setVisible(true);
    }

//    public static void main(String[] args) throws Exception {
////        String smiles = "CN1C(=O)CN=C(C2=C1C=CC(=C2)Cl)C3=CC=CC=C3";
//        String smiles = "c1ccccc1CC@@NC(Cl)(Cl)";
//        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
//        IAtomContainer molecule = sp.parseSmiles(smiles);
//        ViewMolecule2Dv2 v2d = new ViewMolecule2Dv2(molecule, 300, 300);
//        v2d.run();
//    }
}
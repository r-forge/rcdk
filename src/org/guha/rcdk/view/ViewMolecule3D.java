package org.guha.rcdk.view;

import org.guha.rcdk.util.Misc;
import org.guha.rcdk.view.panels.JmolPanel;
import org.jmol.api.JmolViewer;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class ViewMolecule3D {
    JmolPanel jmolPanel;

    IAtomContainer molecule = null;

    String filename = null;

    final static String strScript = "";

    public ViewMolecule3D(IAtomContainer molecule) {
        this.molecule = molecule;
    }

    public ViewMolecule3D(String filename) {
        this.filename = filename;
    }

    public void show() {
        JFrame frame = new JFrame("Molecule Viewer");
        frame.addWindowListener(new ApplicationCloser());
        Container contentPane = frame.getContentPane();

        this.jmolPanel = new JmolPanel();
        contentPane.add(jmolPanel);
        frame.setSize(300, 300);
        frame.setVisible(true);

        JmolViewer viewer = jmolPanel.getViewer();
        setScript("set loglevel 4");
//        ((Viewer)viewer).setAppletContext("", null, null, "-i");


        if (this.molecule != null) {

            // make a IChemFile out of this
            MoleculeSet moleculeSet = new MoleculeSet();
            moleculeSet.addMolecule(new Molecule(molecule));
            ChemModel model = new ChemModel();
            model.setMoleculeSet(moleculeSet);
            ChemSequence sequence = new ChemSequence();
            sequence.addChemModel(model);
            ChemFile chemFile = new ChemFile();
            chemFile.addChemSequence(sequence);

            viewer.openClientFile("", "", chemFile);
        } else if (this.filename != null) {
        }

        String strError = viewer.getOpenFileError();
        if (strError != null)
            System.out.println(strError);
    }

    public void setScript(String script) {
        JmolViewer viewer = this.jmolPanel.getViewer();
        viewer.evalString(script);
    }

    static class ApplicationCloser extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            // System.exit(0);
        }
    }

    public static void main(String[] args) {
        try {
            IAtomContainer[] acs = Misc.loadMolecules(new String[]{"/home/rajarshi/src/Rrepo/trunk/rcdk/data/dan007.hin"},
                    true, true, true);
            AtomContainer ac = (AtomContainer) DefaultChemObjectBuilder.getInstance().newAtomContainer(acs[0]);
            ViewMolecule3D vm3d = new ViewMolecule3D(ac);
            vm3d.show();
        } catch (CDKException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}

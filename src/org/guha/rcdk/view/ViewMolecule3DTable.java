package org.guha.rcdk.view;

import org.guha.rcdk.view.panels.JmolPanel;
import org.guha.rcdk.view.table.StructureTableCellEditor3D;
import org.guha.rcdk.view.table.StructureTableCellRenderer3D;
import org.guha.rcdk.view.table.StructureTableModel;
import org.jmol.api.JmolViewer;
import org.openscience.cdk.*;
import org.openscience.cdk.interfaces.IAtomContainer;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;

class StructureTable3D {

    private IAtomContainer[] v;
    private int cellx = 200;
    private int celly = 200;
    private int ncol = 4; // excludes the 1st column for row numbers

    public StructureTable3D(IAtomContainer[] structs) {
        this.v = structs;
    }

    public StructureTable3D(IAtomContainer[] structs, int ncol) {
        this.v = structs;
        this.ncol = ncol;
    }

    public StructureTable3D(IAtomContainer[] structs, int ncol, int cellx, int celly) {
        this.v = structs;
        this.ncol = ncol;
        this.cellx = cellx;
        this.celly = celly;
    }

    private ChemFile getAsChemFile(IAtomContainer atomContainer) {
        MoleculeSet moleculeSet = new MoleculeSet();
        moleculeSet.addMolecule(new Molecule(atomContainer));
        ChemModel model = new ChemModel();
        model.setMoleculeSet(moleculeSet);
        ChemSequence sequence = new ChemSequence();
        sequence.addChemModel(model);
        ChemFile chemFile = new ChemFile();
        chemFile.addChemSequence(sequence);
        return chemFile;
    }

    public void display() {

        int i;
        int j;
        int pad = 10;

        Object[][] ndata;
        String[] nm = new String[this.ncol + 1];

        int extra = v.length % this.ncol;
        int block = v.length - extra;
        int nrow = block / this.ncol;

        if (extra == 0) {
            ndata = new Object[nrow][this.ncol + 1];
        } else {
            ndata = new Object[nrow + 1][this.ncol + 1];
        }

        int cnt = 0;
        for (i = 0; i < nrow; i++) {
            for (j = 1; j < this.ncol + 1; j++) {
                ndata[i][j] = new JmolPanel();
                ((JmolPanel) ndata[i][j]).setSize(cellx, celly);
                JmolViewer viewer = ((JmolPanel) ndata[i][j]).getViewer();
                viewer.openClientFile("", "", getAsChemFile(v[cnt]));
                String strError = viewer.getOpenFileError();
                if (strError != null)
                    System.out.println(strError);
                cnt += 1;
            }
        }
        j = 1;
        while (cnt < v.length) {
            ndata[nrow][j] = new JmolPanel();
            ((JmolPanel) ndata[i][j]).setSize(cellx, celly);
            JmolViewer viewer = ((JmolPanel) ndata[nrow][j]).getViewer();
            viewer.openClientFile("", "", getAsChemFile(v[cnt]));
            String strError = viewer.getOpenFileError();
            if (strError != null)
                System.out.println(strError);
            cnt += 1;
            j += 1;
        }

        if (extra != 0) nrow += 1;

        for (i = 0; i < nrow; i++) {
            ndata[i][0] = i * this.ncol + 1;
        }

        JFrame frame = new JFrame("3D Structure Grid");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTable mtable = new JTable(new StructureTableModel(ndata, nm));
        mtable.setShowGrid(true);

        // set row heights
        for (i = 0; i < nrow; i++) {
            mtable.setRowHeight(i, this.celly);
        }

        // disallow cell selections
        mtable.setColumnSelectionAllowed(true);
        mtable.setRowSelectionAllowed(true);

        // set the TableCellRenderer for the all columns
        // we also set up a TableCellEditor so that events on a render2dPanel
        // cell get forwarded to the actual render2dPanel. Right now this does nothing
        TableColumn col = mtable.getColumnModel().getColumn(0);
        col.setCellRenderer(new RowLabelRenderer());
        for (i = 1; i < this.ncol + 1; i++) {
            col = mtable.getColumnModel().getColumn(i);
            col.setCellRenderer(new StructureTableCellRenderer3D());
            col.setCellEditor(new StructureTableCellEditor3D());
        }

        // set up scroll bars
        JScrollPane scrollpane = new JScrollPane(mtable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        if (nrow > 3) {
            mtable.setPreferredScrollableViewportSize(new Dimension(this.ncol * this.cellx + pad, 3 * this.celly + pad));
        } else {
            mtable.setPreferredScrollableViewportSize(new Dimension(this.ncol * this.cellx + pad, nrow * this.celly + pad));
        }
        frame.getContentPane().add(scrollpane);

        // start the show!
        frame.pack();
        if (nrow > 3) {
            frame.setSize(this.ncol * this.cellx + pad, 3 * this.celly + pad);
        } else {
            frame.setSize(this.ncol * this.cellx + pad, nrow * this.celly + pad);
        }

        frame.setVisible(true);
    }
}

public class ViewMolecule3DTable {
    private StructureTable3D st = null;

    public ViewMolecule3DTable(IAtomContainer[] molecules, int ncol, int cellx, int celly) {

        try {
            // some checks for visual niceness
            if (molecules.length < ncol) {
                st = new StructureTable3D(molecules, molecules.length, cellx, celly);
            } else {
                st = new StructureTable3D(molecules, ncol, cellx, celly);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void show() {
        st.display();
    }
}

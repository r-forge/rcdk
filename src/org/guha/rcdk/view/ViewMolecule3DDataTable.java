package org.guha.rcdk.view;

import org.guha.rcdk.util.Misc;
import org.guha.rcdk.view.panels.JmolPanel;
import org.guha.rcdk.view.table.StructureTableCellEditor3D;
import org.guha.rcdk.view.table.StructureTableCellRenderer3D;
import org.jmol.api.JmolViewer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

public class ViewMolecule3DDataTable {

    private static int STRUCTURE_COL = 0;

    public ViewMolecule3DDataTable(String[] fnames, String[] cnames,
                                   Object[][] tabledata) {


        int structureCellHeight = 100;

        JFrame frame = new JFrame("Table of Molecules");


        Object[][] data = new Object[fnames.length][cnames.length];
        IAtomContainer[] molecules = null;
        try {
            molecules = Misc.loadMolecules(fnames, true, true);
        } catch (CDKException e) {
            e.printStackTrace();
        }

        // set the structures
        assert molecules != null;
        for (int i = 0; i < molecules.length; i++) {
            data[i][0] = new JmolPanel();
            JmolViewer viewer = ((JmolPanel) data[i][0]).getViewer();
            viewer.openClientFile("", "", molecules[i]);
            String strError = viewer.getOpenFileError();
            if (strError != null)
                System.out.println(strError);
        }
        // set the data
        for (int i = 0; i < molecules.length; i++) {
            for (int j = 1; j < cnames.length; j++) {
                data[i][j] = tabledata[i][j - 1];
            }
        }

        JTable mtable = new JTable(new JmolPanelJTableModel(data, cnames));
        mtable.setShowGrid(true);

        // set row heights
        for (int i = 0; i < fnames.length; i++) {
            mtable.setRowHeight(i, structureCellHeight);
        }

        // add a TableolumnModelListener so we can catch column
        // resizes and change row heights accordingly
        mtable.getColumnModel().addColumnModelListener(
                new JmolColumnModelListener(mtable));

        // allow cell selections
        mtable.setColumnSelectionAllowed(true);
        mtable.setRowSelectionAllowed(true);

        // disable movement of columns. This is needed since we
        // set the CellRenderer and CellEditor for a specific column
        mtable.getTableHeader().setReorderingAllowed(false);

        // set up scroll bars
        JScrollPane scrollpane = new JScrollPane(mtable);
        mtable.setPreferredScrollableViewportSize(new Dimension(500, 70));
        frame.getContentPane().add(scrollpane);

        // set the cell renderer for the structure column
        // we also set up a TableCellEditor so that events on a JmolPanel
        // cell get forwarded to the actual JmolPanel
        TableColumn col = mtable.getColumnModel().getColumn(0);
        col.setCellRenderer(new StructureTableCellRenderer3D());
        col.setCellEditor(new StructureTableCellEditor3D());

        // start the show!
        frame.pack();
        frame.setSize(300, 300);
        frame.setVisible(true);
    }

    static class JmolColumnModelListener implements TableColumnModelListener {
        JTable table;

        public JmolColumnModelListener(JTable t) {
            this.table = t;
        }

        public void columnAdded(TableColumnModelEvent e) {
        }

        public void columnRemoved(TableColumnModelEvent e) {
        }

        public void columnMoved(TableColumnModelEvent e) {
        }

        public void columnMarginChanged(ChangeEvent e) {
            int colwidth = this.table.getColumnModel().getColumn(STRUCTURE_COL)
                    .getWidth();
            for (int i = 0; i < this.table.getRowCount(); i++) {
                this.table.setRowHeight(i, colwidth);
            }

        }

        public void columnSelectionChanged(ListSelectionEvent e) {
        }
    }

    static class JmolPanelJTableModel extends AbstractTableModel {

        private static final long serialVersionUID = -1029080447213047474L;

        private Object[][] rows;

        private String[] columns;

        public JmolPanelJTableModel(Object[][] objs, String[] cols) {
            rows = objs;
            columns = cols;
        }

        public String getColumnName(int column) {
            return columns[column];
        }

        public int getRowCount() {
            return rows.length;
        }

        public int getColumnCount() {
            return columns.length;
        }

        public Object getValueAt(int row, int column) {
            return rows[row][column];
        }

        public boolean isCellEditable(int row, int column) {
            return column == STRUCTURE_COL;
        }

        public Class getColumnClass(int column) {
            return getValueAt(0, column).getClass();
        }
    }

    static class JmolPanelCellRenderer extends JmolPanel implements
            TableCellRenderer {

        private static final long serialVersionUID = 3990689120717795379L;

        public Component getTableCellRendererComponent(JTable table,
                                                       Object value, boolean isSelected, boolean hasFocus,
                                                       int rowIndex, int vColIndex) {
            // return plist[rowIndex];
            return (JmolPanel) value;
        }

        // The following methods override the defaults for performance reasons
        public void validate() {
        }

        public void revalidate() {
        }

        protected void firePropertyChange(String propertyName, Object oldValue,
                                          Object newValue) {
        }

        public void firePropertyChange(String propertyName, boolean oldValue,
                                       boolean newValue) {
        }
    }

}

package org.guha.rcdk.view.table;

import org.guha.rcdk.view.panels.JmolPanel;

import javax.swing.*;
import java.awt.*;

/**
 * @author Rajarshi Guha
 */
public class StructureTableCellEditor3D extends StructureTableCellEditor {
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (column != 0) {
            return (JmolPanel) value;
        }
        return (JmolPanel) value;
    }
}

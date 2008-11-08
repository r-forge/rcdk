package org.guha.rcdk.view.table;

import org.guha.rcdk.view.panels.Render2DPanel;

import javax.swing.*;
import java.awt.*;

/**
 * @author Rajarshi Guha
 */
public class StructureTableCellEditor2D extends StructureTableCellEditor {
      public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (column != 0) {
            return (Render2DPanel) value;
        }
        return (Render2DPanel) value;
    }
}

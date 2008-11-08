package org.guha.rcdk.view.table;

import org.guha.rcdk.view.panels.Render2DPanel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * @author Rajarshi Guha
 */

public class StructureTableCellRenderer2D extends Render2DPanel implements TableCellRenderer {
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int rowIndex, int vColIndex) {
        if (vColIndex != 0) {
            return (Render2DPanel) value;
        }
        return (Render2DPanel) value;
    }

    // The following methods override the defaults for performance reasons
    public void validate() {
    }

    public void revalidate() {
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    }

    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    }
}

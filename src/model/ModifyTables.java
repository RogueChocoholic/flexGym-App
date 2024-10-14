package model;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class ModifyTables {

    private JTable table;
    private JScrollPane scrollpane;
    private JPanel panel;
    private boolean border;

    private void modifyProcess() {
        if (this.panel != null) {
            this.panel.putClientProperty(FlatClientProperties.STYLE, "arc:90");

        }
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);

        this.table.setDefaultRenderer(Object.class, renderer);
        JTableHeader header = this.table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setFont(new Font("Poppins", Font.BOLD, 12)); // Set font to bold
                label.setHorizontalAlignment(SwingConstants.CENTER); // Center the text
                label.setBorder(BorderFactory.createEmptyBorder()); // Remove border
                return label;
            }
        });
        if (!border) {
        this.scrollpane.setBorder(BorderFactory.createEmptyBorder()); // Remove JScrollPane border
            
        }

    }

    public void modifyTables(JPanel panel, JTable table, JScrollPane scrollpane, boolean border) {
        this.panel = panel;
        this.scrollpane = scrollpane;
        this.table = table;
        this.border = border;

        modifyProcess();
    }

    public void modifyTables(JTable table, JScrollPane scrollpane, boolean border) {
        this.scrollpane = scrollpane;
        this.table = table;
        this.border = border;

        modifyProcess();
    }
}

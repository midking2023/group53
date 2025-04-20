import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

/** 数据录入面板 **/
public class DataEntryPanel extends JPanel {
    private final DefaultTableModel model;

    public DataEntryPanel() {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // —— 顶部按钮区 ——
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        top.add(new JButton("Manual Entry"));
        top.add(new JButton("Automatic Entry"));
        add(top, BorderLayout.NORTH);

        // —— 表格区 ——
        String[] cols = {
                "Expense Type","Expense Amount","Belonging Category","AI Suggestion","Action"
        };
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 4;  // 只有“Action”列可点击
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(24);

        // 示例数据
        Object[][] sample = {
                {"Catering",      "¥150.00", "Life",      "Life",      "Confirm"},
                {"Transportation","¥15.00",  "-",         "Life",      "Confirm"},
                {"Utilities",     "¥80.00",  "-",         "Utilities", "Confirm"}
        };
        for (Object[] row : sample) model.addRow(row);

        // “Confirm” 按钮渲染 & 事件
        table.getColumn("Action").setCellRenderer((tbl, val, isSel, hasFoc, row, col) ->
                new JButton(val == null ? "" : val.toString())
        );
        table.getColumn("Action").setCellEditor(new DefaultCellEditor(new JTextField()) {
            private final JButton btn = new JButton("Confirm");
            @Override
            public Component getTableCellEditorComponent(
                    JTable tbl, Object val, boolean sel, int r, int c) {
                btn.addActionListener((ActionEvent e) -> {
                    model.setValueAt("", r, 4);  // 清空按钮文字
                    stopCellEditing();
                });
                return btn;
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}

import javax.swing.*;
import java.awt.*;

public class ManualEntryFrame extends JFrame {

    public ManualEntryFrame() {
        setTitle("录入数据");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(null);

        JLabel lblDate = new JLabel("交易日期");
        lblDate.setBounds(30, 20, 80, 25);
        JTextField txtDate = new JTextField("请选择交易日期");
        txtDate.setBounds(120, 20, 300, 25);

        JLabel lblAmount = new JLabel("交易金额");
        lblAmount.setBounds(30, 60, 80, 25);
        JTextField txtAmount = new JTextField("请输入交易金额");
        txtAmount.setBounds(120, 60, 300, 25);

        JLabel lblPay = new JLabel("支付方式");
        lblPay.setBounds(30, 100, 80, 25);
        String[] payTypes = {"现金", "支付宝", "微信", "银行卡"};
        JComboBox<String> cmbPay = new JComboBox<>(payTypes);
        cmbPay.setBounds(120, 100, 300, 25);

        JLabel lblNote = new JLabel("交易备注");
        lblNote.setBounds(30, 140, 80, 25);
        JTextArea txtNote = new JTextArea("请输入交易备注信息");
        JScrollPane scrollNote = new JScrollPane(txtNote);
        scrollNote.setBounds(120, 140, 300, 100);

        JButton btnSave = new JButton("保存");
        btnSave.setBounds(340, 260, 80, 30);

        panel.add(lblDate);
        panel.add(txtDate);
        panel.add(lblAmount);
        panel.add(txtAmount);
        panel.add(lblPay);
        panel.add(cmbPay);
        panel.add(lblNote);
        panel.add(scrollNote);
        panel.add(btnSave);

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ManualEntryFrame().setVisible(true);
        });
    }
}
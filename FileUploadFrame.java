import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FileUploadFrame extends JFrame {

    public FileUploadFrame() {
        setTitle("导入数据");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(null);

        JLabel label = new JLabel("文件上传");
        label.setBounds(30, 20, 100, 25);

        JButton btnChoose = new JButton("点击上传");
        btnChoose.setBounds(140, 20, 120, 30);

        JTextArea fileListArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(fileListArea);
        scrollPane.setBounds(30, 70, 420, 200);

        JButton btnReimport = new JButton("重新导入");
        btnReimport.setBounds(230, 290, 100, 30);

        JButton btnConfirm = new JButton("确认");
        btnConfirm.setBounds(350, 290, 100, 30);

        btnChoose.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            int result = fileChooser.showOpenDialog(FileUploadFrame.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File[] files = fileChooser.getSelectedFiles();
                StringBuilder sb = new StringBuilder();
                for (File file : files) {
                    sb.append("✔ ").append(file.getName()).append("\n");
                }
                fileListArea.setText(sb.toString());
            }
        });

        panel.add(label);
        panel.add(btnChoose);
        panel.add(scrollPane);
        panel.add(btnReimport);
        panel.add(btnConfirm);

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FileUploadFrame().setVisible(true);
        });
    }
}

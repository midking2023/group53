import javax.swing.*;

/** 程序入口 **/
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("AI‑Empowered Personal Finance Tracker");
            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Data Entry", new DataEntryPanel());
            tabs.addTab("Dashboard", new DashboardPanel());
            frame.setContentPane(tabs);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

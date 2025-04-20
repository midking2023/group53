import javax.swing.*;
import java.awt.*;

/** 仪表盘面板 **/
public class DashboardPanel extends JPanel {
    public DashboardPanel() {
        setLayout(new BorderLayout());

        // —— 左侧导航 ——
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(new Color(44, 62, 80));
        nav.setPreferredSize(new Dimension(180, 0));
        for (int i = 0; i < 5; i++) {
            JLabel lbl = new JLabel("Level 1 Navigation");
            lbl.setForeground(Color.WHITE);
            lbl.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
            nav.add(lbl);
        }
        add(nav, BorderLayout.WEST);

        // —— 顶部工具栏 ——
        JPanel top = new JPanel(new BorderLayout(5,0));
        top.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        JTextField search = new JTextField();
        JButton notify = new JButton("🔔");
        JLabel user = new JLabel("momo.zxy");
        top.add(search, BorderLayout.CENTER);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,5,0));
        right.add(notify); right.add(user);
        top.add(right, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // —— 中心内容区 ——
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // — 卡片：预算 & 存款 —
        JPanel cards = new JPanel(new FlowLayout(FlowLayout.LEFT,20,0));
        cards.add(new ChartPanel(
                "This month's budget: ¥8,846",
                ChartPanel.Type.LINE,
                new double[]{200,400,300,500}
        ));
        cards.add(new ChartPanel(
                "Savings this month: ¥3,860",
                ChartPanel.Type.LINE,
                new double[]{100,150,50,120}
        ));
        center.add(cards);
        center.add(Box.createVerticalStrut(20));

        // — 中部：柱状图 + 推荐列表 —
        JPanel mid = new JPanel(new BorderLayout(20,0));
        mid.add(new ChartPanel(
                "Historical Consumption Trend",
                ChartPanel.Type.BAR,
                new double[]{300,770,990,520,130,510,270,350,780,520,270,380}
        ), BorderLayout.CENTER);
        DefaultListModel<String> lm = new DefaultListModel<>();
        lm.addElement("1. Transportation 323,234");
        lm.addElement("2. Catering 323,234");
        lm.addElement("3. Red Packet 323,234");
        JList<String> list = new JList<>(lm);
        list.setBorder(BorderFactory.createTitledBorder("Cost Reduction Recommendations"));
        list.setPreferredSize(new Dimension(180,200));
        mid.add(list, BorderLayout.EAST);
        center.add(mid);
        center.add(Box.createVerticalStrut(20));

        // — 底部：预测折线图 —
        center.add(new ChartPanel(
                "Expenditure Forecast Curve",
                ChartPanel.Type.LINE,
                new double[]{15,30,10,45,35,60,40}
        ));

        add(new JScrollPane(center), BorderLayout.CENTER);
    }
}

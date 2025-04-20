import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class FinancialApp {

    // 消息数据模型
    private static class Message {
        String content;
        String time;

        public Message(String content, String time) {
            this.content = content;
            this.time = time;
        }
    }

    // 金融设置数据模型
    private static class FinancialSettings {
        String tradingRegion = "";
        String specialHolidays = "";
        String amount = "";
        String financialHabits = "";
        String processingMethod = "";
        String remark = "";
    }

    private final FinancialSettings settings = new FinancialSettings();
    private final List<Message> messages = new ArrayList<>();
    private JFrame frame;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FinancialApp().createAndShowGUI());
    }

    private void createAndShowGUI() {
        // 初始化测试数据
        initializeTestData();

        frame = new JFrame("金融应用");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 650);
        frame.setLocationRelativeTo(null);

        // 主布局 - 水平分割
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(200);
        frame.add(splitPane);

        // 左侧导航栏
        JPanel leftNav = createLeftNavigation();
        splitPane.setLeftComponent(leftNav);

        // 右侧内容区
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        splitPane.setRightComponent(contentPanel);

        // 创建不同内容面板
        JPanel homePanel = createHomePanel();
        JPanel financialPanel = createFinancialSettingsPanel();

        contentPanel.add(homePanel, "home");
        contentPanel.add(financialPanel, "financial");

        frame.setVisible(true);
    }

    private void initializeTestData() {
        // 添加测试消息
        messages.add(new Message("Lindong Dong posted a message on the project, please handle it as soon as possible", "3 days ago"));
        messages.add(new Message("Lindong Dong posted a message on the project, please handle it as soon as possible", "3 days ago"));
        messages.add(new Message("Lindong Dong posted a message on the project, please handle it as soon as possible", "3 days ago"));
        messages.add(new Message("Lindong Dong posted a message on the project, please handle it as soon as possible", "3 days ago"));
    }

    private JPanel createLeftNavigation() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(new Color(245, 245, 245));
        nav.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel navTitle = new JLabel("导航");
        navTitle.setFont(navTitle.getFont().deriveFont(Font.BOLD, 16));

        // 导航项
        JButton homeBtn = createNavButton("首页");
        JButton level1Btn = createNavButton("一级导航");
        JButton level2Btn = createNavButton("二级导航");
        JButton level3Btn = createNavButton("三级导航");
        JButton level4Btn = createNavButton("四级导航");
        JButton level5Btn = createNavButton("五级导航");
        JButton financialBtn = createNavButton("金融设置");

        // 添加按钮事件
        homeBtn.addActionListener(e -> cardLayout.show(contentPanel, "home"));
        financialBtn.addActionListener(e -> cardLayout.show(contentPanel, "financial"));

        // 布局调整
        nav.add(navTitle);
        nav.add(Box.createVerticalStrut(15));
        nav.add(homeBtn);
        nav.add(Box.createVerticalStrut(10));
        nav.add(level1Btn);
        nav.add(Box.createVerticalStrut(5));
        nav.add(level2Btn);
        nav.add(Box.createVerticalStrut(5));
        nav.add(level3Btn);
        nav.add(Box.createVerticalStrut(5));
        nav.add(level4Btn);
        nav.add(Box.createVerticalStrut(5));
        nav.add(level5Btn);
        nav.add(Box.createVerticalStrut(15));
        nav.add(financialBtn);

        return nav;
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
        button.setBackground(new Color(245, 245, 245));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        return button;
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 欢迎信息
        JLabel welcomeLabel = new JLabel("早上好，张三");
        welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(Font.BOLD, 24));
        JLabel wishLabel = new JLabel("祝您天天开心！");
        wishLabel.setFont(wishLabel.getFont().deriveFont(Font.PLAIN, 16));

        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.add(welcomeLabel);
        welcomePanel.add(Box.createVerticalStrut(5));
        welcomePanel.add(wishLabel);
        welcomePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 消息通知标题
        JLabel messageTitle = new JLabel("消息通知");
        messageTitle.setFont(messageTitle.getFont().deriveFont(Font.BOLD, 18));
        messageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 消息列表
        JPanel messageList = new JPanel();
        messageList.setLayout(new BoxLayout(messageList, BoxLayout.Y_AXIS));
        messageList.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (Message msg : messages) {
            JPanel messagePanel = createMessageItem(msg.content, msg.time);
            messageList.add(messagePanel);
            messageList.add(Box.createVerticalStrut(10));
        }

        panel.add(welcomePanel);
        panel.add(Box.createVerticalStrut(30));
        panel.add(messageTitle);
        panel.add(Box.createVerticalStrut(15));
        panel.add(messageList);

        return panel;
    }

    private JPanel createMessageItem(String content, String time) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea contentArea = new JTextArea(content);
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBackground(Color.WHITE);
        contentArea.setFont(contentArea.getFont().deriveFont(Font.PLAIN, 14));

        JLabel timeLabel = new JLabel(time);
        timeLabel.setForeground(Color.GRAY);
        timeLabel.setFont(timeLabel.getFont().deriveFont(Font.PLAIN, 12));
        timeLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        panel.add(Box.createVerticalStrut(5));
        panel.add(contentArea);
        panel.add(Box.createVerticalStrut(5));
        panel.add(timeLabel);
        panel.add(Box.createVerticalStrut(5));

        // 设置固定宽度
        panel.setMaximumSize(new Dimension(600, panel.getPreferredSize().height));

        return panel;
    }

    private JPanel createFinancialSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("本地金融环境设置");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 表单
        JPanel form = new JPanel();
        form.setLayout(new GridBagLayout());
        form.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 交易区域
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("交易区域:"), gbc);

        gbc.gridx = 1;
        JComboBox<String> regionCombo = new JComboBox<>(new String[]{"", "华东", "华北", "华南", "西部"});
        regionCombo.setSelectedItem(settings.tradingRegion);
        form.add(regionCombo, gbc);

        // 特殊节假日季节性支出
        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("特殊节假日季节性支出:"), gbc);

        gbc.gridx = 1;
        JComboBox<String> holidaysCombo = new JComboBox<>(new String[]{"", "春节", "国庆", "中秋", "元旦"});
        holidaysCombo.setSelectedItem(settings.specialHolidays);
        form.add(holidaysCombo, gbc);

        // 请输入支出金额
        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(new JLabel("请输入支出金额:"), gbc);

        gbc.gridx = 1;
        JTextField amountField = new JTextField(settings.amount, 20);
        form.add(amountField, gbc);

        // 金融习惯调整方式
        gbc.gridx = 0;
        gbc.gridy = 3;
        form.add(new JLabel("金融习惯调整方式:"), gbc);

        gbc.gridx = 1;
        JComboBox<String> habitsCombo = new JComboBox<>(new String[]{"", "保守型", "稳健型", "进取型"});
        habitsCombo.setSelectedItem(settings.financialHabits);
        form.add(habitsCombo, gbc);

        // 请输入处理方式
        gbc.gridx = 0;
        gbc.gridy = 4;
        form.add(new JLabel("请输入处理方式:"), gbc);

        gbc.gridx = 1;
        JTextField methodField = new JTextField(settings.processingMethod, 20);
        form.add(methodField, gbc);

        // 备注
        gbc.gridx = 0;
        gbc.gridy = 5;
        form.add(new JLabel("备注:"), gbc);

        gbc.gridx = 1;
        JTextArea remarkArea = new JTextArea(settings.remark, 3, 20);
        remarkArea.setLineWrap(true);
        remarkArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(remarkArea);
        form.add(scrollPane, gbc);

        // 保存按钮
        JButton saveBtn = new JButton("保存");
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            settings.tradingRegion = (String) regionCombo.getSelectedItem();
            settings.specialHolidays = (String) holidaysCombo.getSelectedItem();
            settings.amount = amountField.getText();
            settings.financialHabits = (String) habitsCombo.getSelectedItem();
            settings.processingMethod = methodField.getText();
            settings.remark = remarkArea.getText();

            JOptionPane.showMessageDialog(frame, "设置已保存", "成功", JOptionPane.INFORMATION_MESSAGE);
        });

        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        panel.add(form);
        panel.add(Box.createVerticalStrut(20));
        panel.add(saveBtn);

        return panel;
    }
}

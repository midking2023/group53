import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserSettingsApp {

    // 用户数据模型
    private static class UserSettings {
        String email = "15857788888@qq.com";
        String name = "momoChou";
        String phone = "0752-8888888";
        String theme = "Bright";
        String language = "China";
        boolean emailNotifications = true;
        boolean inAppNotifications = true;
    }

    private final UserSettings userSettings = new UserSettings();
    private JFrame frame;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserSettingsApp().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("用户设置");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
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

        // 创建不同设置面板
        JPanel basicInfoPanel = createBasicInfoPanel();
        JPanel themePanel = createThemePanel();
        JPanel notificationsPanel = createNotificationsPanel();

        contentPanel.add(basicInfoPanel, "basic");
        contentPanel.add(themePanel, "theme");
        contentPanel.add(notificationsPanel, "notifications");

        frame.setVisible(true);
    }

    private JPanel createLeftNavigation() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(new Color(245, 245, 245));
        nav.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel navTitle = new JLabel("设置");
        navTitle.setFont(navTitle.getFont().deriveFont(Font.BOLD, 16));

        // 基本设置
        JLabel basicLabel = new JLabel("基本设置");
        basicLabel.setFont(basicLabel.getFont().deriveFont(Font.BOLD));
        JButton basicInfoBtn = new JButton("基本信息");
        JButton themeBtn = new JButton("主题/语言设置");

        // 安全设置
        JLabel securityLabel = new JLabel("安全设置");
        securityLabel.setFont(securityLabel.getFont().deriveFont(Font.BOLD));
        JButton passwordBtn = new JButton("密码修改");
        JButton accountBindingBtn = new JButton("账户绑定");

        // 通知设置
        JLabel notificationsLabel = new JLabel("通知设置");
        notificationsLabel.setFont(notificationsLabel.getFont().deriveFont(Font.BOLD));
        JButton notificationsBtn = new JButton("消息通知设置");

        // 添加按钮事件
        basicInfoBtn.addActionListener(e -> cardLayout.show(contentPanel, "basic"));
        themeBtn.addActionListener(e -> cardLayout.show(contentPanel, "theme"));
        notificationsBtn.addActionListener(e -> cardLayout.show(contentPanel, "notifications"));

        // 布局调整
        nav.add(navTitle);
        nav.add(Box.createVerticalStrut(15));
        nav.add(basicLabel);
        nav.add(Box.createVerticalStrut(5));
        nav.add(basicInfoBtn);
        nav.add(themeBtn);
        nav.add(Box.createVerticalStrut(15));
        nav.add(securityLabel);
        nav.add(Box.createVerticalStrut(5));
        nav.add(passwordBtn);
        nav.add(accountBindingBtn);
        nav.add(Box.createVerticalStrut(15));
        nav.add(notificationsLabel);
        nav.add(Box.createVerticalStrut(5));
        nav.add(notificationsBtn);

        return nav;
    }

    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("基本信息");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 表单
        JPanel form = new JPanel();
        form.setLayout(new GridBagLayout());
        form.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 邮箱
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("邮箱:"), gbc);

        gbc.gridx = 1;
        JTextField emailField = new JTextField(userSettings.email, 25);
        emailField.setEditable(false);
        form.add(emailField, gbc);

        // 姓名
        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("姓名:"), gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(userSettings.name, 25);
        form.add(nameField, gbc);

        // 联系电话
        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(new JLabel("联系电话:"), gbc);

        gbc.gridx = 1;
        JTextField phoneField = new JTextField(userSettings.phone, 25);
        form.add(phoneField, gbc);

        // 保存按钮
        JButton saveBtn = new JButton("保存更改");
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            userSettings.name = nameField.getText();
            userSettings.phone = phoneField.getText();
            JOptionPane.showMessageDialog(frame, "基本信息已更新", "保存成功", JOptionPane.INFORMATION_MESSAGE);
        });

        // 头像设置
        JLabel avatarTitle = new JLabel("头像设置");
        avatarTitle.setFont(avatarTitle.getFont().deriveFont(Font.BOLD, 14));
        avatarTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel avatarBox = new JPanel();
        avatarBox.setLayout(new BoxLayout(avatarBox, BoxLayout.X_AXIS));
        avatarBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel avatarLabel = new JLabel();
        avatarLabel.setIcon(new ImageIcon("default-avatar.png")); // 需要准备一个默认头像图片
        avatarLabel.setPreferredSize(new Dimension(100, 100));

        JButton changeAvatarBtn = new JButton("更换头像");
        changeAvatarBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                avatarLabel.setIcon(new ImageIcon(fileChooser.getSelectedFile().getPath()));
            }
        });

        avatarBox.add(avatarLabel);
        avatarBox.add(Box.createHorizontalStrut(15));
        avatarBox.add(changeAvatarBtn);

        panel.add(title);
        panel.add(Box.createVerticalStrut(15));
        panel.add(form);
        panel.add(Box.createVerticalStrut(15));
        panel.add(saveBtn);
        panel.add(Box.createVerticalStrut(20));
        panel.add(avatarTitle);
        panel.add(Box.createVerticalStrut(10));
        panel.add(avatarBox);

        return panel;
    }

    private JPanel createThemePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("主题/语言设置");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel form = new JPanel();
        form.setLayout(new GridBagLayout());
        form.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 主题
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("主题:"), gbc);

        gbc.gridx = 1;
        ButtonGroup themeGroup = new ButtonGroup();
        JRadioButton brightTheme = new JRadioButton("明亮");
        brightTheme.setSelected(userSettings.theme.equals("Bright"));
        JRadioButton darkTheme = new JRadioButton("暗黑");
        themeGroup.add(brightTheme);
        themeGroup.add(darkTheme);

        JPanel themePanel = new JPanel();
        themePanel.setLayout(new BoxLayout(themePanel, BoxLayout.X_AXIS));
        themePanel.add(brightTheme);
        themePanel.add(Box.createHorizontalStrut(10));
        themePanel.add(darkTheme);
        form.add(themePanel, gbc);

        // 语言
        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("语言:"), gbc);

        gbc.gridx = 1;
        String[] languages = {"China", "English", "日本語"};
        JComboBox<String> languageCombo = new JComboBox<>(languages);
        languageCombo.setSelectedItem(userSettings.language);
        form.add(languageCombo, gbc);

        // 保存按钮
        JButton saveBtn = new JButton("保存更改");
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            userSettings.theme = brightTheme.isSelected() ? "Bright" : "Dark";
            userSettings.language = (String) languageCombo.getSelectedItem();
            JOptionPane.showMessageDialog(frame, "主题和语言设置已更新", "保存成功", JOptionPane.INFORMATION_MESSAGE);
        });

        panel.add(title);
        panel.add(Box.createVerticalStrut(15));
        panel.add(form);
        panel.add(Box.createVerticalStrut(15));
        panel.add(saveBtn);

        return panel;
    }

    private JPanel createNotificationsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("消息通知设置");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel form = new JPanel();
        form.setLayout(new GridBagLayout());
        form.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 邮件通知
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("接收邮件通知:"), gbc);

        gbc.gridx = 1;
        JCheckBox emailNotifCheck = new JCheckBox();
        emailNotifCheck.setSelected(userSettings.emailNotifications);
        form.add(emailNotifCheck, gbc);

        // 应用内通知
        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("接收应用内通知:"), gbc);

        gbc.gridx = 1;
        JCheckBox inAppNotifCheck = new JCheckBox();
        inAppNotifCheck.setSelected(userSettings.inAppNotifications);
        form.add(inAppNotifCheck, gbc);

        // 保存按钮
        JButton saveBtn = new JButton("保存更改");
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            userSettings.emailNotifications = emailNotifCheck.isSelected();
            userSettings.inAppNotifications = inAppNotifCheck.isSelected();
            JOptionPane.showMessageDialog(frame, "通知设置已更新", "保存成功", JOptionPane.INFORMATION_MESSAGE);
        });

        panel.add(title);
        panel.add(Box.createVerticalStrut(15));
        panel.add(form);
        panel.add(Box.createVerticalStrut(15));
        panel.add(saveBtn);

        return panel;
    }
}
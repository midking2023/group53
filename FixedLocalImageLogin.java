package org.example;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class FixedLocalImageLogin {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true); // 确保窗口可见
        });
    }

    static class LoginFrame extends JFrame {
        private final Color BRAND_BLUE = new Color(0, 112, 201);
        private static final String USERNAME = "123";
        private static final String PASSWORD = "123";

        public LoginFrame() {
            setTitle("登录系统");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(800, 600);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            try {
                initComponents();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "初始化失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void initComponents() {
            // 左侧图片面板
            JPanel imagePanel = createImagePanel();
            add(imagePanel, BorderLayout.WEST);

            // 右侧登录面板
            JPanel loginPanel = createLoginPanel();
            add(loginPanel, BorderLayout.CENTER);
        }

        private JPanel createImagePanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setPreferredSize(new Dimension(300, 600));
            panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            // 注意开头的斜杠
            String IMAGE_PATH = "天空1.jpg";
            try {
                URL imageUrl = getClass().getResource(IMAGE_PATH);
                if (imageUrl == null) {
                    throw new RuntimeException("图片未找到: " + IMAGE_PATH);
                }
                ImageIcon icon = new ImageIcon(imageUrl);
                Image scaledImg = icon.getImage().getScaledInstance(280, 560, Image.SCALE_SMOOTH);
                panel.add(new JLabel(new ImageIcon(scaledImg)), BorderLayout.CENTER);
            } catch (Exception e) {
                panel.add(new JLabel("<html>图片加载失败<br>路径: " + IMAGE_PATH + "</html>"), BorderLayout.CENTER);
            }
            return panel;
        }

        private JPanel createLoginPanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(20, 30, 20, 30);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // 标题
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            panel.add(new JLabel("用户登录"), gbc);

            // 用户名
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.EAST;
            panel.add(new JLabel("账号："), gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            JTextField userField = new JTextField(20);
            panel.add(userField, gbc);

            // 密码
            gbc.gridy = 2;
            gbc.anchor = GridBagConstraints.EAST;
            panel.add(new JLabel("密码："), gbc);

            gbc.gridx = 1;
            JPasswordField passField = new JPasswordField(20);
            panel.add(passField, gbc);

            // 登录按钮
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            JButton loginBtn = new JButton("登录");
            loginBtn.setBackground(BRAND_BLUE);
            loginBtn.setForeground(Color.WHITE);

            // 登录验证逻辑
            loginBtn.addActionListener(e -> {
                String username = userField.getText().trim();
                String password = new String(passField.getPassword());

                if (USERNAME.equals(username) && PASSWORD.equals(password)) {
                    JOptionPane.showMessageDialog(this, "登录成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "用户名或密码错误", "错误", JOptionPane.ERROR_MESSAGE);
                }
            });
            panel.add(loginBtn, gbc);

            return panel;
        }
    }
}
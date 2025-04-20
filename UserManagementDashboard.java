package org.example;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Random;

public class UserManagementDashboard {
    private static final Color BRAND_BLUE = new Color(0, 112, 201);
    private static final Color LIGHT_BLUE = new Color(173, 216, 230);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new DashboardFrame();
            frame.setVisible(true);
        });
    }

    static class DashboardFrame extends JFrame {
        public DashboardFrame() {
            setTitle("用户管理后台");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1200, 800);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            try {
                initComponents();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "初始化失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void initComponents() {
            // 顶部用户信息区
            JPanel topPanel = createTopPanel();
            add(topPanel, BorderLayout.NORTH);

            // 中间内容区
            JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            centerSplit.setDividerLocation(300);

            // 左侧消息通知区
            JPanel messagePanel = createMessagePanel();
            centerSplit.setLeftComponent(messagePanel);

            // 右侧数据概览区
            JPanel dataPanel = createDataPanel();
            centerSplit.setRightComponent(dataPanel);

            add(centerSplit, BorderLayout.CENTER);

            // 底部交易记录区
            JPanel bottomPanel = createBottomPanel();
            add(bottomPanel, BorderLayout.SOUTH);
        }

        private JPanel createTopPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.setBackground(BRAND_BLUE);
            panel.setPreferredSize(new Dimension(1200, 80));

            // 用户头像（使用默认头像）
            URL avatarUrl = getClass().getResource("天空1.jpg");
            JLabel avatar = new JLabel(new ImageIcon(avatarUrl));
            avatar.setPreferredSize(new Dimension(60, 60));
            panel.add(avatar);

            // 用户信息
            panel.add(new JLabel("早安，水一"));
            panel.add(new JLabel("今日注册 56 人"));
            panel.add(new JLabel("总访问量 2,223"));

            return panel;
        }

        private JPanel createMessagePanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // 消息列表
            String[] messages = {
                    "林东东提交了立项申请，请尽快处理",
                    "项目进度更新：需求分析已完成",
                    "新消息：财务对账通知",
                    "系统公告：版本升级通知"
            };

            JList<String> messageList = new JList<>(messages);
            messageList.setSelectionBackground(LIGHT_BLUE);
            JScrollPane scrollPane = new JScrollPane(messageList);
            panel.add(scrollPane, BorderLayout.CENTER);

            return panel;
        }

        private JPanel createDataPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // 快捷功能区
            JPanel quickPanel = new JPanel(new GridLayout(2, 3, 10, 10));
            String[] quickFunctions = {"快速开始", "便携式操作", "财务统计", "数据导出", "系统设置", "帮助中心"};
            for (String func : quickFunctions) {
                JButton btn = new JButton(func);
                btn.setBackground(LIGHT_BLUE);
                btn.setForeground(BRAND_BLUE);
                quickPanel.add(btn);
            }
            panel.add(quickPanel, BorderLayout.NORTH);

            // 财务图表区
            JPanel chartPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    drawChart(g);
                }
            };
            chartPanel.setPreferredSize(new Dimension(300, 200));
            panel.add(chartPanel, BorderLayout.CENTER);

            return panel;
        }

        private JPanel createBottomPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // 交易记录表格
            String[] columns = {"时间", "金额", "类型"};
            Object[][] data = {
                    {"2024-03-21 08:50:08", "¥20.6万", "收入"},
                    {"2024-03-21 09:05:21", "¥29.6万", "支出"},
                    {"2024-03-21 14:33:21", "¥20.6万", "收入"}
            };

            JTable table = new JTable(data, columns);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            JScrollPane scrollPane = new JScrollPane(table);
            panel.add(scrollPane, BorderLayout.CENTER);

            return panel;
        }

        private void drawChart(Graphics g) {
            Random random = new Random();
            int width = getWidth();
            int height = getHeight();

            // 绘制雷达图
            g.setColor(BRAND_BLUE);
            int centerX = width / 2;
            int centerY = height / 2;
            int radius = Math.min(width, height) / 3;

            // 收入
            g.fillArc(centerX - radius, centerY - radius, 2*radius, 2*radius, 0, 120);
            // 支出
            g.fillArc(centerX - radius, centerY - radius, 2*radius, 2*radius, 120, 120);
            // 盈余
            g.fillArc(centerX - radius, centerY - radius, 2*radius, 2*radius, 240, 120);
        }
    }
}
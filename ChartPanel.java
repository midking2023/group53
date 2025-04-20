import javax.swing.*;
import java.awt.*;

/**
 * 一个简单的通用图表面板，支持 BAR(柱状) & LINE(折线) 两种类型
 */
public class ChartPanel extends JPanel {
    public enum Type { BAR, LINE }
    private final Type type;
    private final double[] data;
    private final String title;

    public ChartPanel(String title, Type type, double[] data) {
        this.title = title;
        this.type = type;
        this.data = data;
        setPreferredSize(new Dimension(400, 200));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder(title));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data == null || data.length == 0) return;

        Graphics2D g2 = (Graphics2D) g;
        int w = getWidth(), h = getHeight();
        int margin = 30;
        double max = 0;
        for (double v : data) if (v > max) max = v;
        if (max == 0) max = 1;

        // 绘制坐标轴
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawLine(margin, h - margin, w - margin, h - margin); // X 轴
        g2.drawLine(margin, margin, margin, h - margin);         // Y 轴

        if (type == Type.BAR) {
            int n = data.length;
            double barWidth = (double)(w - 2*margin) / n * 0.8;
            for (int i = 0; i < n; i++) {
                int x = margin + (int)((w-2*margin)/n * i + ((w-2*margin)/n - barWidth)/2);
                int barH = (int)((h-2*margin) * (data[i]/max));
                g2.setColor(new Color(100, 149, 237)); // cornflower blue
                g2.fillRect(x, h - margin - barH, (int)barWidth, barH);
            }
        } else { // LINE
            int n = data.length;
            int prevX = -1, prevY = -1;
            for (int i = 0; i < n; i++) {
                int x = margin + (int)((w-2*margin) * (double)i/(n-1));
                int y = h - margin - (int)((h-2*margin) * (data[i]/max));
                g2.setColor(Color.BLUE);
                g2.fillOval(x-3, y-3, 6, 6);
                if (prevX >= 0) {
                    g2.drawLine(prevX, prevY, x, y);
                }
                prevX = x; prevY = y;
            }
        }
    }
}

package common.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

/** Nút có nền tự vẽ để Windows Look and Feel không đổi nền thành trắng. */
public final class ColoredButton extends JButton {
    private final Color normal;
    private final Color hover;
    private boolean hovered;

    public ColoredButton(String text, Color normal, Color hover, Color foreground) {
        super(text);
        this.normal = normal;
        this.hover = hover;
        setForeground(foreground);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    @Override protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(hovered || getModel().isPressed() ? hover : normal);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        } finally {
            g.dispose();
        }
        super.paintComponent(graphics);
    }
}

package common.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * He thong mau sac, font chu va thanh phan giao dien hien dai cho Swing.
 * Thiet ke cho du an Java Nang Cao: dep mat, nhe nhang, chuan Swing goc khong can thu vien ngoai.
 */
public final class HotelTheme {
    // Mau sac co ban giu tuong thich nguoc
    public static final Color INK = new Color(38, 56, 76);
    public static final Color ACCENT = new Color(60, 116, 170);
    public static final Color BACKGROUND = new Color(246, 248, 251);

    // Mau sac bo sung cho giao dien hien dai
    public static final Color PRIMARY_DARK = new Color(47, 76, 112);
    public static final Color SIDEBAR = new Color(47, 76, 112);
    public static final Color SIDEBAR_ACTIVE = new Color(64, 101, 143);
    public static final Color TITLE_BLUE = new Color(48, 82, 122);
    public static final Color PRIMARY_BLUE = new Color(60, 116, 170);
    public static final Color PRIMARY_BLUE_HOVER = new Color(48, 97, 146);
    public static final Color BUTTON_SUCCESS = new Color(57, 153, 103);
    public static final Color BUTTON_WARNING = new Color(230, 158, 53);
    public static final Color BUTTON_DANGER = new Color(197, 75, 63);
    public static final Color BUTTON_SECONDARY = new Color(126, 135, 142);
    public static final Color SURFACE = Color.WHITE;
    public static final Color SURFACE_HOVER = new Color(241, 245, 249);
    public static final Color BORDER = new Color(226, 232, 240);
    public static final Color BORDER_STRONG = new Color(203, 213, 225);
    public static final Color TEXT_PRIMARY = new Color(31, 41, 55);
    public static final Color TEXT_SECONDARY = new Color(100, 111, 124);
    public static final Color TEXT_MUTED = new Color(148, 163, 184);

    // Mau trang thai (Pills / Badges)
    public static final Color SUCCESS_BG = new Color(236, 253, 245);
    public static final Color SUCCESS_TEXT = new Color(5, 150, 105);
    public static final Color SUCCESS_BORDER = new Color(167, 243, 208);

    public static final Color WARNING_BG = new Color(254, 243, 199);
    public static final Color WARNING_TEXT = new Color(180, 83, 9);
    public static final Color WARNING_BORDER = new Color(253, 230, 138);

    public static final Color DANGER_BG = new Color(254, 242, 242);
    public static final Color DANGER_TEXT = new Color(220, 38, 38);
    public static final Color DANGER_BORDER = new Color(254, 202, 202);

    public static final Color INFO_BG = new Color(240, 249, 255);
    public static final Color INFO_TEXT = new Color(2, 132, 199);
    public static final Color INFO_BORDER = new Color(186, 230, 253);

    public static final Color PURPLE_BG = new Color(245, 243, 255);
    public static final Color PURPLE_TEXT = new Color(124, 58, 237);
    public static final Color PURPLE_BORDER = new Color(221, 214, 254);

    public static final Color GRAY_BG = new Color(241, 245, 249);
    public static final Color GRAY_TEXT = new Color(100, 116, 139);
    public static final Color GRAY_BORDER = new Color(203, 213, 225);

    // Font chu Segoe UI hien dai
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BADGE = new Font("Segoe UI", Font.BOLD, 11);

    private static final DecimalFormatSymbols VND_SYMBOLS = new DecimalFormatSymbols(new Locale("vi", "VN"));
    static {
        VND_SYMBOLS.setGroupingSeparator('.');
    }
    private static final DecimalFormat VND_FORMAT = new DecimalFormat("#,##0 ₫", VND_SYMBOLS);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    public enum ButtonType {
        PRIMARY, SECONDARY, SUCCESS, DANGER, WARNING
    }

    private HotelTheme() {}

    public static void install() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        Font baseFont = FONT_REGULAR;
        for (Object key : UIManager.getDefaults().keySet().toArray()) {
            if (UIManager.get(key) instanceof Font) {
                UIManager.put(key, baseFont);
            }
        }
        UIManager.put("control", BACKGROUND);
        UIManager.put("nimbusBase", PRIMARY_DARK);
        UIManager.put("nimbusSelectionBackground", PRIMARY_BLUE);
        UIManager.put("Table.alternateRowColor", new Color(248, 250, 252));
        UIManager.put("Table.rowHeight", 36);
        UIManager.put("TabbedPane.font", FONT_BOLD);
    }

    public static String formatVnd(BigDecimal value) {
        if (value == null) return "0 ₫";
        return VND_FORMAT.format(value);
    }

    public static String formatDateTime(LocalDateTime dt) {
        if (dt == null) return "-";
        return dt.format(DATE_TIME_FORMATTER);
    }

    /**
     * Tao the thong ke (KPI Stat Card) dep mat hien dai.
     */
    public static JPanel createStatCard(String title, String value, String subtext, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(accentColor, 1, 8),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_SMALL);
        lblTitle.setForeground(TEXT_SECONDARY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValue.setForeground(accentColor != null ? accentColor : TEXT_PRIMARY);

        JLabel lblSub = new JLabel(subtext);
        lblSub.setFont(FONT_SMALL);
        lblSub.setForeground(TEXT_MUTED);

        JPanel content = new JPanel(new BorderLayout(0, 2));
        content.setOpaque(false);
        content.add(lblTitle, BorderLayout.NORTH);
        content.add(lblValue, BorderLayout.CENTER);
        content.add(lblSub, BorderLayout.SOUTH);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    /**
     * Tao nut bam (JButton) bo goc dep theo tieu chuan Design System.
     */
    public static JButton createButton(String text, ButtonType type, Runnable action) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg = getBackground();
                if (!isEnabled()) {
                    bg = new Color(226, 232, 240);
                } else if (getModel().isPressed()) {
                    bg = bg.darker();
                } else if (getModel().isRollover()) {
                    bg = getHoverColor(bg);
                }

                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                Color borderCol = (Color) getClientProperty("borderColor");
                if (borderCol != null && isEnabled()) {
                    g2.setColor(borderCol);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(FONT_BOLD);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        switch (type) {
            case PRIMARY -> {
                button.setBackground(PRIMARY_BLUE);
                button.setForeground(Color.WHITE);
                button.putClientProperty("borderColor", null);
            }
            case SUCCESS -> {
                button.setBackground(BUTTON_SUCCESS);
                button.setForeground(Color.WHITE);
                button.putClientProperty("borderColor", null);
            }
            case DANGER -> {
                button.setBackground(BUTTON_DANGER);
                button.setForeground(Color.WHITE);
                button.putClientProperty("borderColor", null);
            }
            case WARNING -> {
                button.setBackground(BUTTON_WARNING);
                button.setForeground(Color.WHITE);
                button.putClientProperty("borderColor", null);
            }
            case SECONDARY -> {
                button.setBackground(BUTTON_SECONDARY);
                button.setForeground(Color.WHITE);
                button.putClientProperty("borderColor", null);
            }
        }

        if (action != null) {
            button.addActionListener(e -> action.run());
        }
        return button;
    }

    private static Color getHoverColor(Color c) {
        if (c.equals(PRIMARY_BLUE)) return PRIMARY_BLUE_HOVER;
        if (c.equals(SURFACE)) return SURFACE_HOVER;
        if (c.equals(BUTTON_SUCCESS)) return new Color(46, 133, 87);
        if (c.equals(BUTTON_WARNING)) return new Color(204, 131, 34);
        if (c.equals(BUTTON_DANGER)) return new Color(166, 58, 48);
        if (c.equals(BUTTON_SECONDARY)) return new Color(100, 109, 116);
        return c.darker();
    }

    /**
     * Cau hinh giao dien bang (JTable) hien dai, co pill badge va so dong dep.
     */
    public static void table(JTable table) {
        table.setRowHeight(34);
        table.setShowGrid(true);
        table.setGridColor(BORDER_STRONG);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setSelectionBackground(new Color(226, 237, 248));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setFont(FONT_REGULAR);
        table.setBackground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setFont(FONT_BOLD);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));
        header.setBackground(new Color(246, 247, 249));
        header.setForeground(TITLE_BLUE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_STRONG));

        table.setDefaultRenderer(
                Object.class,
                new DefaultTableCellRenderer() {
                    private Color pillBg = null;
                    private Color pillBorder = null;

                    @Override
                    public Component getTableCellRendererComponent(
                            JTable t,
                            Object value,
                            boolean selected,
                            boolean focus,
                            int row,
                            int column) {
                        super.getTableCellRendererComponent(t, value, selected, focus, row, column);
                        setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                        pillBg = null;
                        pillBorder = null;
                        setFont(FONT_REGULAR);

                        if (!selected) {
                            setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                            setForeground(TEXT_PRIMARY);
                        }

                        // Can le va dinh dang tien te / ngay thang
                        if (value instanceof BigDecimal bd) {
                            setText(formatVnd(bd));
                            setHorizontalAlignment(SwingConstants.RIGHT);
                        } else if (value instanceof LocalDateTime ldt) {
                            setText(formatDateTime(ldt));
                            setHorizontalAlignment(SwingConstants.CENTER);
                        } else if (value instanceof Number && !(value instanceof BigDecimal)) {
                            setHorizontalAlignment(SwingConstants.CENTER);
                        } else {
                            setHorizontalAlignment(SwingConstants.LEFT);
                        }

                        // Kiem tra neu la trang thai hoac uu tien thi dung pill badge
                        String text = value == null ? "" : value.toString();
                        PillStyle pill = getPillStyle(text);
                        if (pill != null) {
                            setText("● " + pill.text);
                            setHorizontalAlignment(SwingConstants.CENTER);
                            setFont(FONT_BADGE);
                            pillBg = pill.bg;
                            pillBorder = pill.border;
                            if (!selected) {
                                setForeground(pill.fg);
                            }
                        }

                        return this;
                    }

                    @Override
                    protected void paintComponent(Graphics g) {
                        if (pillBg != null) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            int textW = getFontMetrics(getFont()).stringWidth(getText());
                            int pillW = Math.min(getWidth() - 10, textW + 24);
                            int pillH = 26;
                            int x = (getWidth() - pillW) / 2;
                            int y = (getHeight() - pillH) / 2;

                            g2.setColor(pillBg);
                            g2.fillRoundRect(x, y, pillW, pillH, 14, 14);
                            g2.setColor(pillBorder);
                            g2.drawRoundRect(x, y, pillW - 1, pillH - 1, 14, 14);
                            g2.dispose();
                        }
                        super.paintComponent(g);
                    }
                });
    }

    /**
     * Dinh nghia mau sac va text cho pill badge dua tren trang thai / enum.
     */
    private record PillStyle(String text, Color bg, Color fg, Color border) {}

    private static PillStyle getPillStyle(String code) {
        return switch (code) {
            case "TRONG" -> new PillStyle("Trống (Sẵn sàng)", SUCCESS_BG, SUCCESS_TEXT, SUCCESS_BORDER);
            case "DA_DAT" -> new PillStyle("Đã đặt trước", PURPLE_BG, PURPLE_TEXT, PURPLE_BORDER);
            case "DANG_SU_DUNG" -> new PillStyle("Đang có khách", INFO_BG, INFO_TEXT, INFO_BORDER);
            case "DANG_DON" -> new PillStyle("Đang dọn dẹp", WARNING_BG, WARNING_TEXT, WARNING_BORDER);
            case "BAO_TRI" -> new PillStyle("Đang bảo trì", DANGER_BG, DANGER_TEXT, DANGER_BORDER);
            // Buong phong
            case "CHO_DON" -> new PillStyle("Chờ dọn dẹp", WARNING_BG, WARNING_TEXT, WARNING_BORDER);
            case "HOAN_THANH" -> new PillStyle("Đã hoàn tất", SUCCESS_BG, SUCCESS_TEXT, SUCCESS_BORDER);
            case "HUY" -> new PillStyle("Đã hủy bỏ", GRAY_BG, GRAY_TEXT, GRAY_BORDER);
            // Bao tri
            case "BAO_HONG" -> new PillStyle("Chờ sửa chữa", DANGER_BG, DANGER_TEXT, DANGER_BORDER);
            case "DANG_SUA" -> new PillStyle("Đang sửa chữa", WARNING_BG, WARNING_TEXT, WARNING_BORDER);
            case "HOAN_TAT" -> new PillStyle("Hoàn tất", SUCCESS_BG, SUCCESS_TEXT, SUCCESS_BORDER);
            // Muc do uu tien
            case "KHAN_CAP" -> new PillStyle("Khẩn cấp", DANGER_BG, DANGER_TEXT, DANGER_BORDER);
            case "CAO" -> new PillStyle("Ưu tiên cao", WARNING_BG, WARNING_TEXT, WARNING_BORDER);
            case "TRUNG_BINH" -> new PillStyle("Trung bình", INFO_BG, INFO_TEXT, INFO_BORDER);
            case "THAP" -> new PillStyle("Thấp", GRAY_BG, GRAY_TEXT, GRAY_BORDER);
            // Boolean Ngung su dung
            case "Ngừng SD", "Co" -> new PillStyle("Ngừng sử dụng", DANGER_BG, DANGER_TEXT, DANGER_BORDER);
            case "Hoạt động", "Khong" -> new PillStyle("Hoạt động", SUCCESS_BG, SUCCESS_TEXT, SUCCESS_BORDER);
            default -> null;
        };
    }

    /**
     * Vien bo goc (RoundedBorder).
     */
    public static class RoundedBorder implements Border {
        private final Color color;
        private final int thickness;
        private final int radius;

        public RoundedBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            for (int i = 0; i < thickness; i++) {
                g2.drawRoundRect(x + i, y + i, width - 1 - (i * 2), height - 1 - (i * 2), radius, radius);
            }
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}


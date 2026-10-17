package Module_TaiChinh.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {

    private final JPanel pnlContent;
    private final CardLayout cardLayout;
    private HoaDonPanel hoaDonPanel;
    private ChiTietHoaDonPanel chiTietHoaDonPanel;
    private ThanhToanPanel thanhToanPanel;
    private ThongKeTaiChinhPanel thongKeTaiChinhPanel;

    public MainFrame() {
        setTitle("HỆ THỐNG QUẢN LÝ KHÁCH SẠN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);

        // Layout chinh gom thanh ben va noi dung
        setLayout(new BorderLayout());

        // Thanh ben trai
        JPanel pnlSidebar = new JPanel();
        pnlSidebar.setBackground(new Color(23, 54, 93));
        pnlSidebar.setPreferredSize(new Dimension(220, 0));
        pnlSidebar.setLayout(new BoxLayout(
                pnlSidebar, BoxLayout.Y_AXIS));
        pnlSidebar.setBorder(new EmptyBorder(20, 12, 20, 12));

        JLabel lblLogo = new JLabel("HOTEL MANAGER");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Arial", Font.BOLD, 19));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("QUẢN LÝ KHÁCH SẠN");
        lblSubtitle.setForeground(new Color(210, 220, 235));
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 11));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlSidebar.add(lblLogo);
        pnlSidebar.add(Box.createVerticalStrut(8));
        pnlSidebar.add(lblSubtitle);
        pnlSidebar.add(Box.createVerticalStrut(35));

        // Khu vuc noi dung co the thay doi
        cardLayout = new CardLayout();
        pnlContent = new JPanel(cardLayout);
        pnlContent.setBackground(new Color(245, 247, 250));

        pnlContent.add(
                taoTrangCho("Trang chủ",
                        "Chào mừng đến hệ thống quản lý khách sạn."),
                "HOME");

        // Them cac muc dieu huong
        pnlSidebar.add(taoNutMenu("Trang chủ", "HOME"));
        pnlSidebar.add(Box.createVerticalStrut(8));

        pnlSidebar.add(taoNutMenu("Quản lý hóa đơn", "HOADON"));
        pnlSidebar.add(Box.createVerticalStrut(8));

        pnlSidebar.add(taoNutMenu("Chi tiết hóa đơn", "CHITIET"));
        pnlSidebar.add(Box.createVerticalStrut(8));

        pnlSidebar.add(taoNutMenu("Thanh toán", "THANHTOAN"));
        pnlSidebar.add(Box.createVerticalStrut(8));

        pnlSidebar.add(taoNutMenu("Thống kê tài chính", "THONGKE"));

        pnlSidebar.add(Box.createVerticalGlue());

        JLabel lblFooter = new JLabel("Phiên bản 1.0");
        lblFooter.setForeground(new Color(190, 205, 225));
        lblFooter.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlSidebar.add(lblFooter);

        add(pnlSidebar, BorderLayout.WEST);
        add(pnlContent, BorderLayout.CENTER);

        // Hien thi man hinh mac dinh
        cardLayout.show(pnlContent, "HOME");
    }

    private JButton taoNutMenu(String ten, String maTrang) {
        JButton btn = new JButton(ten);

        btn.setMaximumSize(new Dimension(
                Integer.MAX_VALUE, 45));
        btn.setPreferredSize(new Dimension(195, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(23, 54, 93));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 15, 10, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            switch (maTrang) {
                case "HOADON" -> moManHinhHoaDon();
                case "CHITIET" -> moManHinhChiTietHoaDon();
                case "THANHTOAN" -> moManHinhThanhToan();
                case "THONGKE" -> moManHinhThongKeTaiChinh();
                default -> cardLayout.show(pnlContent, maTrang);
            }
        });

        // Tao trang tam cho cac chuc nang chua xay dung
        if (!"HOME".equals(maTrang)
                && !"HOADON".equals(maTrang)
                && !"CHITIET".equals(maTrang)
                && !"THANHTOAN".equals(maTrang)
                && !"THONGKE".equals(maTrang)) {
            pnlContent.add(
                    taoTrangCho(ten,
                            "Chức năng này sẽ được xây dựng ở bước tiếp theo."),
                    maTrang);
        }

        return btn;
    }

    private JPanel taoTrangCho(String tieuDe, String moTa) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 250));

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);
        box.setBorder(new EmptyBorder(35, 45, 35, 45));

        JLabel lblTitle = new JLabel(tieuDe);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 25));
        lblTitle.setForeground(new Color(23, 54, 93));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDesc = new JLabel(moTa);
        lblDesc.setFont(new Font("Arial", Font.PLAIN, 14));
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        box.add(lblTitle);
        box.add(Box.createVerticalStrut(15));
        box.add(lblDesc);

        panel.add(box);
        return panel;
    }

    private void moManHinhHoaDon() {
        if (hoaDonPanel == null) {
            // Callback nhan ma hoa don do nguoi dung chon.
            hoaDonPanel = new HoaDonPanel(
                    this::moManHinhChiTietHoaDon,
                    this::moManHinhThanhToan);
            pnlContent.add(hoaDonPanel, "HOADON");
        } else {
            // Tai lai de thay tong tien vua cap nhat tu man hinh chi tiet.
            hoaDonPanel.lamMoiDanhSach();
        }

        cardLayout.show(pnlContent, "HOADON");
    }

    private void moManHinhChiTietHoaDon() {
        moManHinhChiTietHoaDon(null);
    }

    private void moManHinhChiTietHoaDon(String maHoaDon) {
        if (chiTietHoaDonPanel == null) {
            chiTietHoaDonPanel = new ChiTietHoaDonPanel();

            pnlContent.add(
                    chiTietHoaDonPanel,
                    "CHI_TIET_HOA_DON");
        }

        cardLayout.show(pnlContent, "CHI_TIET_HOA_DON");

        if (maHoaDon != null && !maHoaDon.isBlank()) {
            chiTietHoaDonPanel.hienThiHoaDon(maHoaDon);
        }
    }

    private void moManHinhThanhToan() {
        moManHinhThanhToan(null);
    }

    private void moManHinhThanhToan(String maHoaDon) {
        if (thanhToanPanel == null) {
            thanhToanPanel = new ThanhToanPanel();
            pnlContent.add(thanhToanPanel, "THANHTOAN");
        }

        cardLayout.show(pnlContent, "THANHTOAN");

        if (maHoaDon != null && !maHoaDon.isBlank()) {
            thanhToanPanel.hienThiHoaDon(maHoaDon);
        }
    }

    private void moManHinhThongKeTaiChinh() {
        if (thongKeTaiChinhPanel == null) {
            thongKeTaiChinhPanel = new ThongKeTaiChinhPanel();
            pnlContent.add(thongKeTaiChinhPanel, "THONGKE");
        } else {
            thongKeTaiChinhPanel.taiThongKe();
        }

        cardLayout.show(pnlContent, "THONGKE");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new MainFrame().setVisible(true);
        });
    }
}

package Module_TaiChinh.view;

import QLDichVu.view.DichVuPanel;
import Module_PhanHoi.view.PhanHoiPanel;
import Module_QuanLyNhaCungCap.view.NhaCungCapPanel;
import Module_QuanLyNhapHang.view.PhieuNhapPanel;
import Module_QuanLyVatTu.view.VatTuPanel;
import common.database.ConnectionFactory;
import common.database.DatabaseConnection;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import module.baotri.controller.BaoTriController;
import module.baotri.dao.SqlServerBaoTriDAO;
import module.baotri.service.BaoTriService;
import module.baotri.view.BaoTriPanel;
import module.buongphong.controller.BuongPhongController;
import module.buongphong.dao.SqlServerVeSinhDAO;
import module.buongphong.service.BuongPhongService;
import module.buongphong.view.BuongPhongPanel;
import module.datphong.view.DatPhongView;
import module.phong.controller.PhongController;
import module.phong.dao.SqlServerPhongDAO;
import module.phong.service.PhongService;
import module.phong.view.PhongPanel;
import nhom3.model.TaiKhoan;
import nhom3.service.NhanSuAccessService;
import nhom3.util.PhienDangNhap;
import nhom3.view.CaLamPanel;
import nhom3.view.NhanVienPanel;
import nhom3.view.PhanCaPanel;
import nhom3.view.TaiKhoanPanel;

public class MainFrame extends JFrame {

    private final JPanel pnlContent;
    private final CardLayout cardLayout;
    private DichVuPanel dichVuPanel;
    private PhanHoiPanel phanHoiPanel;
    private HoaDonPanel hoaDonPanel;
    private ChiTietHoaDonPanel chiTietHoaDonPanel;
    private ThanhToanPanel thanhToanPanel;
    private ThongKeTaiChinhPanel thongKeTaiChinhPanel;
    private DatPhongView leTanPanel;
    private PhongController phongController;
    private BuongPhongController buongPhongController;
    private BaoTriController baoTriController;
    private PhongPanel phongPanel;
    private BuongPhongPanel buongPhongPanel;
    private BaoTriPanel baoTriPanel;
    private VatTuPanel vatTuPanel;
    private NhaCungCapPanel nhaCungCapPanel;
    private PhieuNhapPanel phieuNhapPanel;
    private NhanVienPanel nhanVienPanel;
    private CaLamPanel caLamPanel;
    private PhanCaPanel phanCaPanel;
    private TaiKhoanPanel taiKhoanPanel;
    private final NhanSuAccessService nhanSuAccess = new NhanSuAccessService();
    private boolean daKiemTraKetNoiChung;

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

        pnlSidebar.add(taoNutMenu("Quản lý khách hàng", "KHACHHANG"));
        pnlSidebar.add(Box.createVerticalStrut(8));

        pnlSidebar.add(taoNutMenu("Quản lý đặt phòng", "DATPHONG"));
        pnlSidebar.add(Box.createVerticalStrut(8));

        pnlSidebar.add(taoNutMenu("Nhận / trả phòng", "LUUTRU"));
        pnlSidebar.add(Box.createVerticalStrut(8));

        pnlSidebar.add(taoNutMenu("Quản lý phòng", "PHONG"));
        pnlSidebar.add(Box.createVerticalStrut(8));

        pnlSidebar.add(taoNutMenu("Buồng phòng", "BUONGPHONG"));
        pnlSidebar.add(Box.createVerticalStrut(8));

        pnlSidebar.add(taoNutMenu("Bảo trì", "BAOTRI"));
        pnlSidebar.add(Box.createVerticalStrut(8));

        pnlSidebar.add(taoNutMenu("Kho vật tư", "VATTU"));
        pnlSidebar.add(Box.createVerticalStrut(8));
        pnlSidebar.add(taoNutMenu("Nhà cung cấp", "NHACUNGCAP"));
        pnlSidebar.add(Box.createVerticalStrut(8));
        pnlSidebar.add(taoNutMenu("Nhập hàng", "PHIEUNHAP"));
        pnlSidebar.add(Box.createVerticalStrut(8));

        pnlSidebar.add(taoNutMenu("Nhân viên", "NHANVIEN"));
        pnlSidebar.add(Box.createVerticalStrut(8));
        pnlSidebar.add(taoNutMenu("Ca làm", "CALAM"));
        pnlSidebar.add(Box.createVerticalStrut(8));
        pnlSidebar.add(taoNutMenu("Lịch trực & bàn giao", "PHANCA"));
        pnlSidebar.add(Box.createVerticalStrut(8));
        pnlSidebar.add(taoNutMenu("Tài khoản & phân quyền", "TAIKHOAN"));
        pnlSidebar.add(Box.createVerticalStrut(8));
        pnlSidebar.add(taoNutMenu("Đăng xuất nhân sự", "DANGXUAT_NHANSU"));
        pnlSidebar.add(Box.createVerticalStrut(8));

        pnlSidebar.add(taoNutMenu("Quản lý hóa đơn", "HOADON"));
        pnlSidebar.add(Box.createVerticalStrut(8));

        pnlSidebar.add(taoNutMenu("Chi tiết hóa đơn", "CHITIET"));
        pnlSidebar.add(Box.createVerticalStrut(8));

        pnlSidebar.add(taoNutMenu("Thanh toán", "THANHTOAN"));
        pnlSidebar.add(Box.createVerticalStrut(8));

        pnlSidebar.add(taoNutMenu("Thống kê tài chính", "THONGKE"));
        pnlSidebar.add(Box.createVerticalStrut(8));
        pnlSidebar.add(taoNutMenu("Quản lý dịch vụ", "DICHVU"));
        pnlSidebar.add(Box.createVerticalStrut(8));
        pnlSidebar.add(taoNutMenu("<html>Quản lý phản hồi<br>khách hàng</html>", "PHANHOI"));

        pnlSidebar.add(Box.createVerticalGlue());

        JLabel lblFooter = new JLabel("Phiên bản 1.0");
        lblFooter.setForeground(new Color(190, 205, 225));
        lblFooter.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlSidebar.add(lblFooter);

        JScrollPane sidebarScroll = new JScrollPane(
                pnlSidebar,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sidebarScroll.setPreferredSize(new Dimension(235, 0));
        sidebarScroll.setBorder(null);
        sidebarScroll.getViewport().setBackground(new Color(23, 54, 93));
        sidebarScroll.getVerticalScrollBar().setUnitIncrement(18);

        add(sidebarScroll, BorderLayout.WEST);
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
                case "KHACHHANG" -> moManHinhLeTan("KHACHHANG");
                case "DATPHONG" -> moManHinhLeTan("DATPHONG");
                case "LUUTRU" -> moManHinhLeTan("LUUTRU");
                case "PHONG" -> moManHinhNhom2("PHONG");
                case "BUONGPHONG" -> moManHinhNhom2("BUONGPHONG");
                case "BAOTRI" -> moManHinhNhom2("BAOTRI");
                case "VATTU", "NHACUNGCAP", "PHIEUNHAP" -> moManHinhKho(maTrang);
                case "NHANVIEN", "CALAM", "PHANCA", "TAIKHOAN" -> moManHinhNhanSu(maTrang);
                case "DANGXUAT_NHANSU" -> dangXuatNhanSu();
                case "HOADON" -> moManHinhHoaDon();
                case "CHITIET" -> moManHinhChiTietHoaDon();
                case "THANHTOAN" -> moManHinhThanhToan();
                case "THONGKE" -> moManHinhThongKeTaiChinh();
                case "DICHVU" -> moManHinhDichVu();
                case "PHANHOI" -> moManHinhPhanHoi();
                default -> cardLayout.show(pnlContent, maTrang);
            }
        });

        // Tao trang tam cho cac chuc nang chua xay dung
        if (!"HOME".equals(maTrang)
                && !"KHACHHANG".equals(maTrang)
                && !"DATPHONG".equals(maTrang)
                && !"LUUTRU".equals(maTrang)
                && !"PHONG".equals(maTrang)
                && !"BUONGPHONG".equals(maTrang)
                && !"BAOTRI".equals(maTrang)
                && !"VATTU".equals(maTrang)
                && !"NHACUNGCAP".equals(maTrang)
                && !"PHIEUNHAP".equals(maTrang)
                && !"NHANVIEN".equals(maTrang)
                && !"CALAM".equals(maTrang)
                && !"PHANCA".equals(maTrang)
                && !"TAIKHOAN".equals(maTrang)
                && !"DANGXUAT_NHANSU".equals(maTrang)
                && !"HOADON".equals(maTrang)
                && !"CHITIET".equals(maTrang)
                && !"THANHTOAN".equals(maTrang)
                && !"THONGKE".equals(maTrang)
                && !"DICHVU".equals(maTrang)
                && !"PHANHOI".equals(maTrang)) {
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

    private void moManHinhLeTan(String trang) {
        if (!damBaoKetNoiChung()) return;
        if (leTanPanel == null) {
            leTanPanel = new DatPhongView();
            pnlContent.add(leTanPanel, "LE_TAN");
        }

        switch (trang) {
            case "KHACHHANG" -> leTanPanel.hienThiKhachHang();
            case "LUUTRU" -> leTanPanel.hienThiLuuTru();
            default -> leTanPanel.hienThiDatPhong();
        }
        cardLayout.show(pnlContent, "LE_TAN");
    }

    private void khoiTaoNhom2() {
        if (phongPanel != null) return;

        ConnectionFactory connections = DatabaseConnection::getConnection;
        SqlServerPhongDAO phongDAO = new SqlServerPhongDAO(connections);
        phongController = new PhongController(new PhongService(phongDAO));
        buongPhongController = new BuongPhongController(
                new BuongPhongService(new SqlServerVeSinhDAO(connections, phongDAO)));
        baoTriController = new BaoTriController(
                new BaoTriService(new SqlServerBaoTriDAO(connections, phongDAO)));

        phongPanel = new PhongPanel(phongController);
        buongPhongPanel = new BuongPhongPanel(buongPhongController, phongController);
        baoTriPanel = new BaoTriPanel(baoTriController, phongController);
        pnlContent.add(phongPanel, "PHONG");
        pnlContent.add(buongPhongPanel, "BUONGPHONG");
        pnlContent.add(baoTriPanel, "BAOTRI");
    }

    private void moManHinhNhom2(String trang) {
        if (!damBaoKetNoiChung()) return;
        khoiTaoNhom2();
        cardLayout.show(pnlContent, trang);
        switch (trang) {
            case "BUONGPHONG" -> buongPhongController.refresh();
            case "BAOTRI" -> baoTriController.refresh();
            default -> phongController.refresh();
        }
    }

    private boolean damBaoKetNoiChung() {
        if (daKiemTraKetNoiChung) return true;

        if (DatabaseConnection.hasConfiguredCredentials()) {
            try (Connection connection = DatabaseConnection.getConnection()) {
                daKiemTraKetNoiChung = true;
                return true;
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Kết nối đã cấu hình chưa dùng được:\n" + ex.getMessage()
                        + "\nHãy kiểm tra lại máy chủ, database và tài khoản.",
                        "Kết nối cơ sở dữ liệu", JOptionPane.WARNING_MESSAGE);
            }
        }

        JTextField urlField = new JTextField(DatabaseConnection.getDefaultUrl(), 42);
        JTextField userField = new JTextField(DatabaseConnection.getDefaultUsername(), 24);
        JPasswordField passwordField = new JPasswordField(24);
        JPanel form = new JPanel(new GridLayout(0, 1, 4, 4));
        form.add(new JLabel("JDBC URL (máy chủ và database QuanLyKhachSan):"));
        form.add(urlField);
        form.add(new JLabel("Tài khoản SQL Server:"));
        form.add(userField);
        form.add(new JLabel("Mật khẩu:"));
        form.add(passwordField);

        while (true) {
            int choice = JOptionPane.showConfirmDialog(this, form,
                    "Kết nối database cho Khách hàng, Đặt phòng và Phòng",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (choice != JOptionPane.OK_OPTION) return false;

            char[] password = passwordField.getPassword();
            if (urlField.getText().isBlank() || userField.getText().isBlank()
                    || password.length == 0) {
                JOptionPane.showMessageDialog(this,
                        "Hãy nhập đủ JDBC URL, tài khoản và mật khẩu.",
                        "Thiếu thông tin kết nối", JOptionPane.WARNING_MESSAGE);
                Arrays.fill(password, '\0');
                continue;
            }
            DatabaseConnection.configureForCurrentRun(
                    urlField.getText().trim(), userField.getText().trim(), password);
            Arrays.fill(password, '\0');
            passwordField.setText("");
            try (Connection connection = DatabaseConnection.getConnection()) {
                daKiemTraKetNoiChung = true;
                return true;
            } catch (SQLException ex) {
                DatabaseConnection.clearRuntimeCredentials();
                JOptionPane.showMessageDialog(this,
                        "Chưa kết nối được:\n" + ex.getMessage()
                        + "\nKiểm tra tên instance, cổng, database và quyền tài khoản.",
                        "Lỗi kết nối cơ sở dữ liệu", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void moManHinhKho(String trang) {
        if (!damBaoKetNoiChung()) return;
        switch (trang) {
            case "VATTU" -> {
                if (vatTuPanel == null) {
                    vatTuPanel = new VatTuPanel();
                    pnlContent.add(vatTuPanel, "VATTU");
                }
                vatTuPanel.taiDuLieu();
            }
            case "NHACUNGCAP" -> {
                if (nhaCungCapPanel == null) {
                    nhaCungCapPanel = new NhaCungCapPanel();
                    pnlContent.add(nhaCungCapPanel, "NHACUNGCAP");
                }
                nhaCungCapPanel.taiDuLieu();
            }
            case "PHIEUNHAP" -> {
                if (phieuNhapPanel == null) {
                    phieuNhapPanel = new PhieuNhapPanel();
                    pnlContent.add(phieuNhapPanel, "PHIEUNHAP");
                }
                phieuNhapPanel.taiDuLieu();
            }
            default -> { return; }
        }
        cardLayout.show(pnlContent, trang);
    }

    private void moManHinhNhanSu(String trang) {
        if (!damBaoKetNoiChung() || !damBaoDangNhapNhanSu()) return;
        boolean admin = PhienDangNhap.laAdmin();
        boolean keToan = PhienDangNhap.laVaiTro("KETOAN");
        if (("NHANVIEN".equals(trang) && !admin && !keToan)
                || (("CALAM".equals(trang) || "TAIKHOAN".equals(trang)) && !admin)) {
            JOptionPane.showMessageDialog(this, "Tài khoản này không có quyền mở chức năng.");
            return;
        }

        switch (trang) {
            case "NHANVIEN" -> {
                if (nhanVienPanel == null) {
                    nhanVienPanel = new NhanVienPanel(keToan);
                    pnlContent.add(nhanVienPanel, "NHANVIEN");
                }
            }
            case "CALAM" -> {
                if (caLamPanel == null) {
                    caLamPanel = new CaLamPanel();
                    pnlContent.add(caLamPanel, "CALAM");
                }
            }
            case "PHANCA" -> {
                if (phanCaPanel == null) {
                    phanCaPanel = new PhanCaPanel();
                    pnlContent.add(phanCaPanel, "PHANCA");
                }
            }
            case "TAIKHOAN" -> {
                if (taiKhoanPanel == null) {
                    taiKhoanPanel = new TaiKhoanPanel();
                    pnlContent.add(taiKhoanPanel, "TAIKHOAN");
                }
            }
            default -> { return; }
        }
        cardLayout.show(pnlContent, trang);
    }

    private boolean damBaoDangNhapNhanSu() {
        if (PhienDangNhap.getTaiKhoan() != null) return true;
        try {
            if (!nhanSuAccess.daCoTaiKhoan() && !taoQuanTriNhanSuDauTien()) return false;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Không kiểm tra được tài khoản:\n"
                    + ex.getMessage(), "Lỗi cơ sở dữ liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        JTextField userField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JPanel form = new JPanel(new GridLayout(0, 1, 4, 4));
        form.add(new JLabel("Tên đăng nhập hệ thống nhân sự:"));
        form.add(userField);
        form.add(new JLabel("Mật khẩu hệ thống nhân sự:"));
        form.add(passwordField);
        for (int attempt = 0; attempt < 3; attempt++) {
            if (JOptionPane.showConfirmDialog(this, form, "Đăng nhập nhân sự",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)
                    != JOptionPane.OK_OPTION) return false;
            char[] password = passwordField.getPassword();
            try {
                TaiKhoan account = nhanSuAccess.dangNhap(userField.getText(), password);
                if (account != null) {
                    PhienDangNhap.setTaiKhoan(account);
                    return true;
                }
                JOptionPane.showMessageDialog(this,
                        "Sai thông tin đăng nhập, tài khoản bị khóa hoặc nhân viên đã nghỉ việc.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi đăng nhập:\n" + ex.getMessage(),
                        "Lỗi cơ sở dữ liệu", JOptionPane.ERROR_MESSAGE);
                return false;
            } finally {
                Arrays.fill(password, '\0');
                passwordField.setText("");
            }
        }
        return false;
    }

    private boolean taoQuanTriNhanSuDauTien() {
        JTextField nameField = new JTextField(20);
        JTextField userField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JPasswordField confirmField = new JPasswordField(20);
        JPanel form = new JPanel(new GridLayout(0, 1, 4, 4));
        form.add(new JLabel("Database chưa có tài khoản. Tạo Admin nhân sự đầu tiên:"));
        form.add(new JLabel("Họ tên:"));
        form.add(nameField);
        form.add(new JLabel("Tên đăng nhập:"));
        form.add(userField);
        form.add(new JLabel("Mật khẩu (tối thiểu 6 ký tự):"));
        form.add(passwordField);
        form.add(new JLabel("Nhập lại mật khẩu:"));
        form.add(confirmField);
        if (JOptionPane.showConfirmDialog(this, form, "Thiết lập nhân sự lần đầu",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)
                != JOptionPane.OK_OPTION) return false;
        char[] password = passwordField.getPassword();
        char[] confirmation = confirmField.getPassword();
        try {
            if (!Arrays.equals(password, confirmation)) {
                JOptionPane.showMessageDialog(this, "Hai mật khẩu không khớp.");
                return false;
            }
            nhanSuAccess.taoQuanTriDauTien(
                    nameField.getText(), userField.getText(), password);
            JOptionPane.showMessageDialog(this,
                    "Đã tạo Admin đầu tiên. Hãy đăng nhập để mở module nhân sự.");
            return true;
        } catch (SQLException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Không tạo được Admin", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            Arrays.fill(password, '\0');
            Arrays.fill(confirmation, '\0');
        }
    }

    private void dangXuatNhanSu() {
        if (PhienDangNhap.getTaiKhoan() == null) return;
        PhienDangNhap.dangXuat();
        if (nhanVienPanel != null) pnlContent.remove(nhanVienPanel);
        if (caLamPanel != null) pnlContent.remove(caLamPanel);
        if (phanCaPanel != null) pnlContent.remove(phanCaPanel);
        if (taiKhoanPanel != null) pnlContent.remove(taiKhoanPanel);
        nhanVienPanel = null;
        caLamPanel = null;
        phanCaPanel = null;
        taiKhoanPanel = null;
        cardLayout.show(pnlContent, "HOME");
        JOptionPane.showMessageDialog(this, "Đã đăng xuất khỏi phần nhân sự.");
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

    private void moManHinhDichVu() {
        if (dichVuPanel == null) {
            dichVuPanel = new DichVuPanel();
            pnlContent.add(dichVuPanel, "DICHVU");
        }
        cardLayout.show(pnlContent, "DICHVU");
    }
    private void moManHinhPhanHoi() {
        if (phanHoiPanel == null) {
            phanHoiPanel = new PhanHoiPanel();
            pnlContent.add(phanHoiPanel, "PHANHOI");
        }
        cardLayout.show(pnlContent, "PHANHOI");
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

package app.view;

import Module_TaiChinh.view.ChiTietHoaDonPanel;
import Module_TaiChinh.view.HoaDonPanel;
import Module_TaiChinh.view.ThanhToanPanel;
import Module_TaiChinh.view.ThongKeTaiChinhPanel;

import Module_DichVu.view.DichVuPanel;
import Module_PhanHoiKhachHang.view.PhanHoiPanel;
import Module_NhaCungCap.view.NhaCungCapPanel;
import Module_NhapHang.view.PhieuNhapPanel;
import Module_KhoVatTu.view.VatTuPanel;
import common.database.ConnectionFactory;
import common.database.DatabaseConnection;
import common.ui.ColoredButton;
import app.security.QuyenTruyCap;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import Module_BaoTri.controller.BaoTriController;
import Module_BaoTri.dao.SqlServerBaoTriDAO;
import Module_BaoTri.service.BaoTriService;
import Module_BaoTri.view.BaoTriPanel;
import Module_BuongPhong.controller.BuongPhongController;
import Module_BuongPhong.dao.SqlServerVeSinhDAO;
import Module_BuongPhong.service.BuongPhongService;
import Module_BuongPhong.view.BuongPhongPanel;
import Module_DatPhong.view.DatPhongView;
import Module_Phong.controller.PhongController;
import Module_Phong.dao.SqlServerPhongDAO;
import Module_Phong.service.PhongService;
import Module_Phong.view.PhongPanel;
import Module_TaiKhoanPhanQuyen.model.TaiKhoan;
import Module_TaiKhoanPhanQuyen.service.NhanSuAccessService;
import Module_TaiKhoanPhanQuyen.util.PhienDangNhap;
import Module_CaLamViec.view.CaLamPanel;
import Module_NhanVien.view.NhanVienPanel;
import Module_CaLamViec.view.PhanCaPanel;
import Module_TaiKhoanPhanQuyen.view.TaiKhoanPanel;

public class MainFrame extends JFrame {

    private static final List<DashboardPanel.MenuGroup> MENU_GROUPS = List.of(
            new DashboardPanel.MenuGroup("Lễ tân & lưu trú", "Khách, đặt và nhận/trả phòng", List.of(
                    new DashboardPanel.MenuItem("Khách hàng", "KHACHHANG"),
                    new DashboardPanel.MenuItem("Đặt phòng", "DATPHONG"),
                    new DashboardPanel.MenuItem("Nhận / trả phòng", "LUUTRU"))),
            new DashboardPanel.MenuGroup("Phòng & vận hành", "Phòng, buồng phòng, bảo trì", List.of(
                    new DashboardPanel.MenuItem("Quản lý phòng", "PHONG"),
                    new DashboardPanel.MenuItem("Buồng phòng", "BUONGPHONG"),
                    new DashboardPanel.MenuItem("Bảo trì", "BAOTRI"))),
            new DashboardPanel.MenuGroup("Nhân sự & hệ thống", "Nhân viên, ca và tài khoản", List.of(
                    new DashboardPanel.MenuItem("Nhân viên", "NHANVIEN"),
                    new DashboardPanel.MenuItem("Ca làm", "CALAM"),
                    new DashboardPanel.MenuItem("Lịch trực & bàn giao", "PHANCA"),
                    new DashboardPanel.MenuItem("Tài khoản & phân quyền", "TAIKHOAN"))),
            new DashboardPanel.MenuGroup("Kho & cung ứng", "Vật tư, nhà cung cấp, nhập hàng", List.of(
                    new DashboardPanel.MenuItem("Kho vật tư", "VATTU"),
                    new DashboardPanel.MenuItem("Nhà cung cấp", "NHACUNGCAP"),
                    new DashboardPanel.MenuItem("Nhập hàng", "PHIEUNHAP"))),
            new DashboardPanel.MenuGroup("Tài chính & dịch vụ", "Hóa đơn, dịch vụ và phản hồi", List.of(
                    new DashboardPanel.MenuItem("Hóa đơn", "HOADON"),
                    new DashboardPanel.MenuItem("Chi tiết hóa đơn", "CHITIET"),
                    new DashboardPanel.MenuItem("Thanh toán", "THANHTOAN"),
                    new DashboardPanel.MenuItem("Thống kê tài chính", "THONGKE"),
                    new DashboardPanel.MenuItem("Quản lý dịch vụ", "DICHVU"),
                    new DashboardPanel.MenuItem("Phản hồi khách hàng", "PHANHOI"))));

    private final JPanel pnlContent;
    private final CardLayout cardLayout;
    private final DashboardPanel dashboard;
    private final List<JPanel> nhomDangMo = new ArrayList<>();
    private final List<Runnable> capNhatMenu = new ArrayList<>();
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

        dashboard = new DashboardPanel(MENU_GROUPS, this::moTrang, this::dangNhapTuTrangChu);
        pnlContent.add(dashboard, "HOME");

        // Them cac muc dieu huong
        pnlSidebar.add(taoNutMenu("Trang chủ", "HOME"));
        pnlSidebar.add(Box.createVerticalStrut(12));
        for (DashboardPanel.MenuGroup group : MENU_GROUPS) {
            pnlSidebar.add(taoNhomMenu(group));
            pnlSidebar.add(Box.createVerticalStrut(5));
        }
        capNhatQuyenMenu();

        pnlSidebar.add(Box.createVerticalGlue());
        pnlSidebar.add(taoNutMenu("Đăng xuất nhân sự", "DANGXUAT_NHANSU"));
        pnlSidebar.add(Box.createVerticalStrut(12));

        JLabel lblFooter = new JLabel("Phiên bản 1.0");
        lblFooter.setForeground(new Color(190, 205, 225));
        lblFooter.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlSidebar.add(lblFooter);

        JScrollPane sidebarScroll = new JScrollPane(
                pnlSidebar,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sidebarScroll.setPreferredSize(new Dimension(225, 0));
        sidebarScroll.setBorder(null);
        sidebarScroll.getViewport().setBackground(new Color(23, 54, 93));
        sidebarScroll.getVerticalScrollBar().setUnitIncrement(18);

        add(sidebarScroll, BorderLayout.WEST);
        add(pnlContent, BorderLayout.CENTER);

        // Hien thi man hinh mac dinh
        cardLayout.show(pnlContent, "HOME");
    }

    private JButton taoNutMenu(String ten, String maTrang) {
        JButton btn = new ColoredButton(ten,
                new Color(35, 78, 125), new Color(53, 106, 164), Color.WHITE);

        btn.setMaximumSize(new Dimension(
                Integer.MAX_VALUE, 45));
        btn.setPreferredSize(new Dimension(195, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 15, 10, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> moTrang(maTrang));
        return btn;
    }

    private JPanel taoNhomMenu(DashboardPanel.MenuGroup group) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(new Color(23, 54, 93));
        wrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel items = new JPanel();
        items.setLayout(new BoxLayout(items, BoxLayout.Y_AXIS));
        items.setBackground(new Color(23, 54, 93));
        items.setVisible(false);
        nhomDangMo.add(items);
        JButton toggle = new ColoredButton(group.ten() + "  ▾",
                new Color(45, 91, 141), new Color(65, 119, 177), Color.WHITE);
        toggle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        toggle.setAlignmentX(Component.CENTER_ALIGNMENT);
        toggle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        toggle.setHorizontalAlignment(SwingConstants.LEFT);
        toggle.setBorder(new EmptyBorder(8, 10, 8, 4));
        toggle.addActionListener(e -> {
            boolean mo = !items.isVisible();
            for (JPanel panel : nhomDangMo) panel.setVisible(false);
            items.setVisible(mo);
            wrapper.revalidate();
            wrapper.repaint();
        });
        wrapper.add(toggle);
        List<JButton> buttons = new ArrayList<>();
        for (DashboardPanel.MenuItem item : group.chucNang()) {
            JButton child = taoNutMenu("  " + item.ten(), item.maTrang());
            child.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            child.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
            items.add(child);
            buttons.add(child);
        }
        wrapper.add(items);
        capNhatMenu.add(() -> {
            TaiKhoan account = PhienDangNhap.getTaiKhoan();
            int visible = 0;
            for (int i = 0; i < buttons.size(); i++) {
                boolean allowed = account == null || QuyenTruyCap.duocMo(
                        account.getMaVaiTro(), group.chucNang().get(i).maTrang());
                buttons.get(i).setVisible(allowed);
                if (allowed) visible++;
            }
            items.setVisible(false);
            wrapper.setVisible(visible > 0);
        });
        return wrapper;
    }

    private void capNhatQuyenMenu() {
        for (Runnable update : capNhatMenu) update.run();
        dashboard.capNhatQuyen();
    }

    private void moTrang(String maTrang) {
        if (!"HOME".equals(maTrang) && !"DANGXUAT_NHANSU".equals(maTrang)) {
            if (!damBaoKetNoiChung() || !damBaoDangNhapNhanSu()) return;
            TaiKhoan account = PhienDangNhap.getTaiKhoan();
            if (!QuyenTruyCap.duocMo(account.getMaVaiTro(), maTrang)) {
                JOptionPane.showMessageDialog(this,
                        "Vai trò hiện tại không có quyền mở chức năng này.",
                        "Không có quyền truy cập", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        switch (maTrang) {
            case "HOME" -> {
                cardLayout.show(pnlContent, "HOME");
                dashboard.taiLai();
            }
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
    }

    private void dangNhapTuTrangChu() {
        if (damBaoKetNoiChung() && damBaoDangNhapNhanSu()) dashboard.taiLai();
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
        if (!QuyenTruyCap.duocMo(PhienDangNhap.getTaiKhoan().getMaVaiTro(), trang)) {
            JOptionPane.showMessageDialog(this, "Tài khoản này không có quyền mở chức năng.");
            return;
        }

        switch (trang) {
            case "NHANVIEN" -> {
                if (nhanVienPanel == null) {
                    nhanVienPanel = new NhanVienPanel(false);
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
        for (int attempt = 0; attempt < 3; attempt++) {
            if (!hienThiHopThoaiDangNhap(userField, passwordField)) return false;
            char[] password = passwordField.getPassword();
            try {
                TaiKhoan account = nhanSuAccess.dangNhap(userField.getText(), password);
                if (account != null) {
                    PhienDangNhap.setTaiKhoan(account);
                    capNhatQuyenMenu();
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

    private boolean hienThiHopThoaiDangNhap(JTextField userField,
            JPasswordField passwordField) {
        Color navy = new Color(23, 54, 93);
        JDialog dialog = new JDialog(this, "Đăng nhập nhân sự", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createLineBorder(navy, 2));

        JPanel heading = new JPanel(new BorderLayout(0, 5));
        heading.setBackground(navy);
        heading.setBorder(new EmptyBorder(17, 22, 17, 22));
        JLabel title = new JLabel("ĐĂNG NHẬP NHÂN SỰ");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JLabel subtitle = new JLabel("Hệ thống quản lý khách sạn");
        subtitle.setForeground(new Color(210, 226, 245));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        heading.add(title, BorderLayout.NORTH);
        heading.add(subtitle, BorderLayout.SOUTH);
        content.add(heading, BorderLayout.NORTH);

        JPanel fields = new JPanel();
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));
        fields.setBackground(Color.WHITE);
        fields.setBorder(new EmptyBorder(18, 22, 10, 22));
        JLabel userLabel = new JLabel("Tên đăng nhập");
        JLabel passwordLabel = new JLabel("Mật khẩu");
        for (JLabel label : new JLabel[]{userLabel, passwordLabel}) {
            label.setForeground(navy);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        for (JTextField field : new JTextField[]{userField, passwordField}) {
            field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            field.setAlignmentX(Component.LEFT_ALIGNMENT);
            field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(189, 205, 224)),
                    new EmptyBorder(6, 9, 6, 9)));
        }
        fields.add(userLabel);
        fields.add(Box.createVerticalStrut(5));
        fields.add(userField);
        fields.add(Box.createVerticalStrut(13));
        fields.add(passwordLabel);
        fields.add(Box.createVerticalStrut(5));
        fields.add(passwordField);
        content.add(fields, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 9, 0));
        actions.setBackground(Color.WHITE);
        actions.setBorder(new EmptyBorder(8, 22, 19, 22));
        JButton cancel = new ColoredButton("Hủy", new Color(228, 237, 247),
                new Color(210, 226, 243), navy);
        JButton login = new ColoredButton("Đăng nhập", new Color(40, 81, 130),
                new Color(55, 107, 165), Color.WHITE);
        for (JButton button : new JButton[]{cancel, login}) {
            button.setFont(new Font("Segoe UI", Font.BOLD, 13));
            button.setPreferredSize(new Dimension(105, 36));
        }
        boolean[] accepted = {false};
        cancel.addActionListener(e -> dialog.dispose());
        login.addActionListener(e -> {
            accepted[0] = true;
            dialog.dispose();
        });
        actions.add(cancel);
        actions.add(login);
        content.add(actions, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.getRootPane().setDefaultButton(login);
        dialog.pack();
        dialog.setSize(Math.max(400, dialog.getWidth()),
                Math.max(340, dialog.getHeight()));
        dialog.setLocationRelativeTo(this);
        SwingUtilities.invokeLater(userField::requestFocusInWindow);
        dialog.setVisible(true);
        return accepted[0];
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
        dashboard.xoaDuLieuCaNhan();
        if (nhanVienPanel != null) pnlContent.remove(nhanVienPanel);
        if (caLamPanel != null) pnlContent.remove(caLamPanel);
        if (phanCaPanel != null) pnlContent.remove(phanCaPanel);
        if (taiKhoanPanel != null) pnlContent.remove(taiKhoanPanel);
        if (phanHoiPanel != null) pnlContent.remove(phanHoiPanel);
        nhanVienPanel = null;
        caLamPanel = null;
        phanCaPanel = null;
        taiKhoanPanel = null;
        phanHoiPanel = null;
        capNhatQuyenMenu();
        cardLayout.show(pnlContent, "HOME");
        JOptionPane.showMessageDialog(this, "Đã đăng xuất khỏi phần nhân sự.");
    }

    private void moManHinhHoaDon() {
        if (!damBaoKetNoiChung()) return;
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
        if (!duocMoHienTai("CHITIET")) return;
        if (!damBaoKetNoiChung()) return;
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
        if (!duocMoHienTai("THANHTOAN")) return;
        if (!damBaoKetNoiChung()) return;
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
        if (!damBaoKetNoiChung()) return;
        if (thongKeTaiChinhPanel == null) {
            thongKeTaiChinhPanel = new ThongKeTaiChinhPanel();
            pnlContent.add(thongKeTaiChinhPanel, "THONGKE");
        } else {
            thongKeTaiChinhPanel.taiThongKe();
        }

        cardLayout.show(pnlContent, "THONGKE");
    }

    private void moManHinhDichVu() {
        if (!damBaoKetNoiChung()) return;
        if (dichVuPanel == null) {
            dichVuPanel = new DichVuPanel();
            pnlContent.add(dichVuPanel, "DICHVU");
        }
        cardLayout.show(pnlContent, "DICHVU");
    }
    private void moManHinhPhanHoi() {
        if (!damBaoKetNoiChung()) return;
        if (phanHoiPanel == null) {
            phanHoiPanel = new PhanHoiPanel(
                    QuyenTruyCap.chiXemPhanHoi(PhienDangNhap.getTaiKhoan().getMaVaiTro()));
            pnlContent.add(phanHoiPanel, "PHANHOI");
        }
        cardLayout.show(pnlContent, "PHANHOI");
    }

    private boolean duocMoHienTai(String trang) {
        TaiKhoan account = PhienDangNhap.getTaiKhoan();
        return account != null && QuyenTruyCap.duocMo(account.getMaVaiTro(), trang);
    }

}

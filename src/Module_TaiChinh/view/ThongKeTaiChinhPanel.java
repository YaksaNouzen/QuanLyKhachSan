package Module_TaiChinh.view;

import Module_TaiChinh.controller.ThongKeTaiChinhController;
import Module_TaiChinh.model.ThongKeTaiChinh;
import Module_TaiChinh.model.ThongKeTheoNgay;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class ThongKeTaiChinhPanel extends JPanel {

    private final ThongKeTaiChinhController controller;
    private final NumberFormat dinhDangTien
            = NumberFormat.getNumberInstance(
                    Locale.forLanguageTag("vi-VN"));

    private JTextField txtTuNgay;
    private JTextField txtDenNgay;
    private JLabel lblSoHoaDon;
    private JLabel lblDaThanhToan;
    private JLabel lblChuaThanhToan;
    private JLabel lblDoanhThu;
    private JLabel lblThucThu;
    private JLabel lblConNo;
    private JTable tblThongKe;
    private DefaultTableModel tableModel;

    public ThongKeTaiChinhPanel() {
        controller = new ThongKeTaiChinhController();
        taoGiaoDien();
        datKhoangNgayThangHienTai();
        taiThongKe();
    }

    // =====================================================
    // 1. TAO GIAO DIEN
    // =====================================================
    private void taoGiaoDien() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(18, 20, 18, 20));
        setBackground(new Color(245, 247, 250));

        JPanel panelTren = new JPanel();
        panelTren.setOpaque(false);
        panelTren.setLayout(new BoxLayout(panelTren, BoxLayout.Y_AXIS));

        JLabel lblTieuDe = new JLabel("THONG KE TAI CHINH");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 25));
        lblTieuDe.setForeground(new Color(23, 54, 93));

        JLabel lblMoTa = new JLabel(
                "So lieu duoc loc theo ngay lap hoa don");
        lblMoTa.setForeground(Color.GRAY);

        // BoxLayout cua panelTren co the keo rong JLabel va lam chu
        // trong nhu bi lech. Dat hai nhan vao panel rieng de can giua.
        JPanel panelTieuDe = new JPanel();
        panelTieuDe.setOpaque(false);
        panelTieuDe.setLayout(
                new BoxLayout(panelTieuDe, BoxLayout.Y_AXIS));

        lblTieuDe.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblMoTa.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelTieuDe.add(lblTieuDe);
        panelTieuDe.add(Box.createVerticalStrut(5));
        panelTieuDe.add(lblMoTa);

        panelTren.add(panelTieuDe);
        panelTren.add(Box.createVerticalStrut(15));
        panelTren.add(taoPanelBoLoc());
        panelTren.add(Box.createVerticalStrut(10));
        panelTren.add(taoPanelTongQuan());

        add(panelTren, BorderLayout.NORTH);
        add(taoPanelBang(), BorderLayout.CENTER);
    }

    private JPanel taoPanelBoLoc() {
        JPanel panel = taoPanelTrangTri("Khoang thoi gian");
        JPanel noiDung = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 3));
        noiDung.setBackground(Color.WHITE);

        txtTuNgay = new JTextField(12);
        txtDenNgay = new JTextField(12);
        JButton btnThongKe = new JButton("Thong ke");
        JButton btnThangNay = new JButton("Thang hien tai");
        JButton btnTatCa = new JButton("Tat ca thoi gian");

        noiDung.add(new JLabel("Tu ngay (yyyy-MM-dd):"));
        noiDung.add(txtTuNgay);
        noiDung.add(new JLabel("Den ngay:"));
        noiDung.add(txtDenNgay);
        noiDung.add(btnThongKe);
        noiDung.add(btnThangNay);
        noiDung.add(btnTatCa);
        panel.add(noiDung, BorderLayout.CENTER);

        btnThongKe.addActionListener(e -> taiThongKe());
        btnThangNay.addActionListener(e -> {
            datKhoangNgayThangHienTai();
            taiThongKe();
        });
        btnTatCa.addActionListener(e -> {
            txtTuNgay.setText("");
            txtDenNgay.setText("");
            taiThongKe();
        });
        txtTuNgay.addActionListener(e -> taiThongKe());
        txtDenNgay.addActionListener(e -> taiThongKe());
        return panel;
    }

    private JPanel taoPanelTongQuan() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 10, 10));
        panel.setOpaque(false);

        lblSoHoaDon = taoTheThongKe(
                "SO HOA DON", "0", new Color(41, 98, 160));
        lblDaThanhToan = taoTheThongKe(
                "DA THANH TOAN", "0", new Color(39, 145, 87));
        lblChuaThanhToan = taoTheThongKe(
                "CHUA THANH TOAN", "0", new Color(230, 151, 37));
        lblDoanhThu = taoTheThongKe(
                "DOANH THU HOA DON", "0 VND", new Color(92, 73, 145));
        lblThucThu = taoTheThongKe(
                "TIEN THUC THU", "0 VND", new Color(32, 130, 120));
        lblConNo = taoTheThongKe(
                "CON NO", "0 VND", new Color(192, 57, 43));

        panel.add(lblSoHoaDon);
        panel.add(lblDaThanhToan);
        panel.add(lblChuaThanhToan);
        panel.add(lblDoanhThu);
        panel.add(lblThucThu);
        panel.add(lblConNo);
        return panel;
    }

    private JLabel taoTheThongKe(
            String tieuDe, String giaTri, Color mau) {
        JLabel label = new JLabel(
                taoNoiDungThe(tieuDe, giaTri));
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        label.setForeground(mau);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, mau),
                new EmptyBorder(12, 14, 12, 14)));
        return label;
    }

    private JPanel taoPanelBang() {
        JPanel panel = taoPanelTrangTri(
                "Chi tiet theo ngay lap hoa don");

        String[] cot = {
            "Ngay lap",
            "So hoa don",
            "Doanh thu hoa don",
            "Tien thuc thu",
            "Con no"
        };

        tableModel = new DefaultTableModel(cot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblThongKe = new JTable(tableModel);
        tblThongKe.setRowHeight(28);
        tblThongKe.setAutoCreateRowSorter(true);
        tblThongKe.getTableHeader().setReorderingAllowed(false);

        tblThongKe.getColumnModel().getColumn(0).setPreferredWidth(110);
        tblThongKe.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblThongKe.getColumnModel().getColumn(2).setPreferredWidth(160);
        tblThongKe.getColumnModel().getColumn(3).setPreferredWidth(150);
        tblThongKe.getColumnModel().getColumn(4).setPreferredWidth(140);

        panel.add(new JScrollPane(tblThongKe), BorderLayout.CENTER);
        return panel;
    }

    private JPanel taoPanelTrangTri(String tieuDe) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(220, 225, 232)),
                new EmptyBorder(10, 10, 10, 10)));

        JLabel label = new JLabel(tieuDe);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(23, 54, 93));
        panel.add(label, BorderLayout.NORTH);
        return panel;
    }

    // =====================================================
    // 2. TAI VA HIEN THI THONG KE
    // =====================================================
    public void taiThongKe() {
        String tuNgay = txtTuNgay.getText().trim();
        String denNgay = txtDenNgay.getText().trim();

        try {
            ThongKeTaiChinh tongQuan
                    = controller.thongKeTongQuan(tuNgay, denNgay);
            List<ThongKeTheoNgay> theoNgay
                    = controller.thongKeTheoNgay(tuNgay, denNgay);

            capNhatTongQuan(tongQuan);
            capNhatBang(theoNgay);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Du lieu khong hop le",
                    JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            hienThiLoi("Khong the tai thong ke tai chinh.", e);
        }
    }

    private void capNhatTongQuan(ThongKeTaiChinh thongKe) {
        lblSoHoaDon.setText(taoNoiDungThe(
                "SO HOA DON", String.valueOf(thongKe.getSoHoaDon())));
        lblDaThanhToan.setText(taoNoiDungThe(
                "DA THANH TOAN",
                String.valueOf(thongKe.getSoDaThanhToan())));
        lblChuaThanhToan.setText(taoNoiDungThe(
                "CHUA THANH TOAN",
                String.valueOf(thongKe.getSoChuaThanhToan())));
        lblDoanhThu.setText(taoNoiDungThe(
                "DOANH THU HOA DON",
                dinhDangTien.format(thongKe.getTongDoanhThu()) + " VND"));
        lblThucThu.setText(taoNoiDungThe(
                "TIEN THUC THU",
                dinhDangTien.format(thongKe.getTongThucThu()) + " VND"));
        lblConNo.setText(taoNoiDungThe(
                "CON NO",
                dinhDangTien.format(thongKe.getTongConNo()) + " VND"));
    }

    private void capNhatBang(List<ThongKeTheoNgay> danhSach) {
        tableModel.setRowCount(0);

        if (danhSach == null) {
            return;
        }

        for (ThongKeTheoNgay dong : danhSach) {
            tableModel.addRow(new Object[]{
                dong.getNgayLap(),
                dong.getSoHoaDon(),
                dinhDangTien.format(dong.getDoanhThu()),
                dinhDangTien.format(dong.getThucThu()),
                dinhDangTien.format(dong.getConNo())
            });
        }
    }

    private String taoNoiDungThe(String tieuDe, String giaTri) {
        return "<html><span style='font-size:10px'>" + tieuDe
                + "</span><br><span style='font-size:17px'><b>"
                + giaTri + "</b></span></html>";
    }

    private void datKhoangNgayThangHienTai() {
        LocalDate homNay = LocalDate.now();
        txtTuNgay.setText(homNay.withDayOfMonth(1).toString());
        txtDenNgay.setText(homNay.toString());
    }

    private void hienThiLoi(String thongBao, Exception e) {
        e.printStackTrace();

        String chiTiet = e.getMessage();
        if (chiTiet == null || chiTiet.isBlank()) {
            chiTiet = e.getClass().getSimpleName();
        }

        JOptionPane.showMessageDialog(
                this,
                thongBao + "\n" + chiTiet,
                "Loi",
                JOptionPane.ERROR_MESSAGE);
    }
}

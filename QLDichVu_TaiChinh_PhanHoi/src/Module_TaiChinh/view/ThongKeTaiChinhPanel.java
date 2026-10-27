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
        setBackground(TaiChinhUI.BACKGROUND);
        JPanel panelTren = new JPanel();
        panelTren.setOpaque(false);
        panelTren.setLayout(new BoxLayout(panelTren, BoxLayout.Y_AXIS));
        panelTren.add(TaiChinhUI.tieuDe("THỐNG KÊ TÀI CHÍNH",
                "Tổng hợp doanh thu, thực thu và công nợ theo ngày lập hóa đơn"));
        panelTren.add(Box.createVerticalStrut(15));
        panelTren.add(taoPanelBoLoc());
        panelTren.add(Box.createVerticalStrut(12));
        panelTren.add(taoPanelTongQuan());
        add(panelTren, BorderLayout.NORTH);
        add(taoPanelBang(), BorderLayout.CENTER);
    }

    private JPanel taoPanelBoLoc() {
        JPanel panel = taoPanelTrangTri("Khoảng thời gian thống kê");
        JPanel noiDung = new JPanel(new BorderLayout(0, 5));
        noiDung.setBackground(Color.WHITE);
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        txtTuNgay = new JTextField(12);
        txtDenNgay = new JTextField(12);
        TaiChinhUI.truong(form, "Từ ngày (yyyy-MM-dd):", txtTuNgay, 0, 0);
        TaiChinhUI.truong(form, "Đến ngày:", txtDenNgay, 2, 0);
        JButton btnThongKe = TaiChinhUI.nut("Thống kê", TaiChinhUI.BLUE);
        JButton btnThangNay = TaiChinhUI.nut("Tháng hiện tại", new Color(32, 130, 120));
        JButton btnTatCa = TaiChinhUI.nut("Tất cả thời gian", TaiChinhUI.GRAY);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttons.setOpaque(false);
        buttons.add(btnThongKe);
        buttons.add(btnThangNay);
        buttons.add(btnTatCa);
        noiDung.add(form, BorderLayout.NORTH);
        noiDung.add(buttons, BorderLayout.SOUTH);
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
                BorderFactory.createLineBorder(new Color(220, 225, 232)),
                new EmptyBorder(12, 12, 12, 12)));
        return label;
    }

    private JPanel taoPanelBang() {
        JPanel panel = taoPanelTrangTri(
                "Thống kê theo ngày lập hóa đơn");

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
        TaiChinhUI.bang(tblThongKe);
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
        return TaiChinhUI.khung(tieuDe);
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

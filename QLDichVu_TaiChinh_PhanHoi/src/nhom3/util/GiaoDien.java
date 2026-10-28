package nhom3.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

/**
 * Gom các màu, font và hàm tạo nút/khung dùng chung
 * để mọi màn hình nhìn giống nhau (giống màn Quản lý hóa đơn).
 */
public class GiaoDien {

    // Màu sắc
    public static final Color MAU_MENU = new Color(23, 58, 99);
    public static final Color MAU_NEN = new Color(244, 246, 250);
    public static final Color MAU_TIEU_DE = new Color(31, 59, 100);
    public static final Color XANH_LA = new Color(46, 139, 87);
    public static final Color CAM = new Color(232, 150, 46);
    public static final Color DO = new Color(208, 57, 59);
    public static final Color XANH_DUONG = new Color(43, 101, 168);
    public static final Color TIM = new Color(106, 76, 156);
    public static final Color XANH_NGOC = new Color(36, 137, 122);
    public static final Color XAM = new Color(108, 117, 125);

    // Font chữ
    public static final Font FONT_CHU = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_DAM = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_KHUNG = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_TIEU_DE = new Font("Segoe UI", Font.BOLD, 30);

    // Nút có màu nền, chữ trắng
    public static JButton taoNut(String chu, Color mauNen) {
        JButton nut = new JButton(chu);
        nut.setFont(FONT_DAM);
        nut.setBackground(mauNen);
        nut.setForeground(Color.WHITE);
        nut.setFocusPainted(false);
        nut.setBorderPainted(false);
        nut.setOpaque(true);
        nut.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nut.setPreferredSize(new Dimension(nut.getPreferredSize().width + 30, 42));
        return nut;
    }

    // Khung trắng có tiêu đề, VD: "Thông tin nhân viên"
    public static JPanel taoKhung(String tieuDe) {
        JPanel khung = new JPanel();
        khung.setBackground(Color.WHITE);
        khung.setAlignmentX(Component.LEFT_ALIGNMENT);
        TitledBorder vien = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(225, 228, 235)), tieuDe);
        vien.setTitleFont(FONT_KHUNG);
        vien.setTitleColor(MAU_TIEU_DE);
        khung.setBorder(BorderFactory.createCompoundBorder(vien,
                BorderFactory.createEmptyBorder(8, 10, 10, 10)));
        return khung;
    }

    // Phần đầu trang: tiêu đề lớn + dòng mô tả
    public static JPanel taoTieuDe(String tieuDe, String moTa) {
        JPanel pn = new JPanel();
        pn.setLayout(new BoxLayout(pn, BoxLayout.Y_AXIS));
        pn.setBackground(MAU_NEN);
        pn.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        pn.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTieuDe = new JLabel(tieuDe);
        lblTieuDe.setFont(FONT_TIEU_DE);
        lblTieuDe.setForeground(MAU_TIEU_DE);

        JLabel lblMoTa = new JLabel(moTa);
        lblMoTa.setFont(FONT_CHU);
        lblMoTa.setForeground(Color.GRAY);

        pn.add(lblTieuDe);
        pn.add(lblMoTa);
        return pn;
    }

    // Lưới form 4 cột: nhãn - ô nhập - nhãn - ô nhập
    public static JPanel taoLuoiForm() {
        JPanel pn = new JPanel(new GridLayout(0, 4, 15, 10));
        pn.setBackground(Color.WHITE);
        return pn;
    }

    public static JLabel taoNhan(String chu) {
        JLabel lbl = new JLabel(chu);
        lbl.setFont(FONT_CHU);
        return lbl;
    }

    // Hàng chứa các nút chức năng ở cuối màn hình
    public static JPanel taoHangNut() {
        JPanel pn = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        pn.setBackground(MAU_NEN);
        return pn;
    }

    public static void dinhDangBang(JTable bang) {
        bang.setFont(FONT_CHU);
        bang.setRowHeight(30);
        bang.getTableHeader().setFont(FONT_DAM);
        bang.getTableHeader().setPreferredSize(new Dimension(0, 34));
        bang.setSelectionBackground(new Color(200, 220, 245));
        bang.setSelectionForeground(Color.BLACK);
    }

    // Thẻ số liệu ở Trang chủ: số to ở trên, chữ mô tả ở dưới
    public static JPanel taoTheThongKe(String tieuDe, int so, Color mauNen) {
        JPanel the = new JPanel();
        the.setLayout(new BoxLayout(the, BoxLayout.Y_AXIS));
        the.setBackground(mauNen);
        the.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblSo = new JLabel(String.valueOf(so));
        lblSo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblSo.setForeground(Color.WHITE);

        JLabel lblTieuDe = new JLabel(tieuDe);
        lblTieuDe.setFont(FONT_DAM);
        lblTieuDe.setForeground(Color.WHITE);

        the.add(lblSo);
        the.add(lblTieuDe);
        return the;
    }
}

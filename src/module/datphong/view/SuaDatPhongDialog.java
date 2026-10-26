
package module.datphong.view;

import module.datphong.controller.DatPhongController;
import module.datphong.model.DatPhong;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class SuaDatPhongDialog extends JDialog {

    private final DatPhongController controller;
    private final DatPhong datPhong;
    private final Timestamp ngayNhanGoc;
private final Timestamp ngayTraGoc;
    private boolean capNhatThanhCong = false;

    private final JTextField txtMaDatPhong = new JTextField(20);
    private final JTextField txtNgayNhan = new JTextField(20);
    private final JTextField txtNgayTra = new JTextField(20);

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SuaDatPhongDialog(
            JFrame parent,
            DatPhongController controller,
            DatPhong datPhong) {

        super(parent, "SUA LICH DAT PHONG", true);

        this.controller = controller;
this.datPhong = datPhong;

this.ngayNhanGoc = datPhong.getNgayNhanDuKien();
this.ngayTraGoc = datPhong.getNgayTraDuKien();

        setSize(450, 250);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        txtMaDatPhong.setText(datPhong.getMaDatPhong());
        txtMaDatPhong.setEditable(false);

        if (datPhong.getNgayNhanDuKien() != null) {
            txtNgayNhan.setText(
                    datPhong.getNgayNhanDuKien().toLocalDateTime()
                            .format(FORMATTER));
        }

        if (datPhong.getNgayTraDuKien() != null) {
            txtNgayTra.setText(
                    datPhong.getNgayTraDuKien().toLocalDateTime()
                            .format(FORMATTER));
        }

        JPanel panelFields = new JPanel(new GridLayout(3, 2, 8, 10));

        panelFields.add(new JLabel("Ma dat phong:"));
        panelFields.add(txtMaDatPhong);

        panelFields.add(new JLabel("Ngay nhan du kien:"));
        panelFields.add(txtNgayNhan);

        panelFields.add(new JLabel("Ngay tra du kien:"));
        panelFields.add(txtNgayTra);

        JButton btnLuu = new JButton("Luu thay doi");
        JButton btnDong = new JButton("Dong");

        JPanel panelButtons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT));

        panelButtons.add(btnLuu);
        panelButtons.add(btnDong);

        setLayout(new BorderLayout(10, 10));
        add(panelFields, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);

        btnLuu.addActionListener(e -> capNhatLich());
        btnDong.addActionListener(e -> dispose());
    }

    
private void capNhatLich() {
    String ngayNhanText = txtNgayNhan.getText().trim();
    String ngayTraText = txtNgayTra.getText().trim();

    if (ngayNhanText.isEmpty() || ngayTraText.isEmpty()) {
        JOptionPane.showMessageDialog(
                this,
                "Vui lòng nhập đầy đủ ngày nhận và ngày trả.",
                "Thiếu thông tin",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    try {
        LocalDateTime ngayNhan =
                LocalDateTime.parse(ngayNhanText, FORMATTER);

        LocalDateTime ngayTra =
                LocalDateTime.parse(ngayTraText, FORMATTER);

        if (!ngayTra.isAfter(ngayNhan)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ngày trả phải sau ngày nhận.",
                    "Thời gian không hợp lệ",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Timestamp ngayNhanMoi = Timestamp.valueOf(ngayNhan);
        Timestamp ngayTraMoi = Timestamp.valueOf(ngayTra);

        // Kiểm tra dữ liệu mới nhất trước khi lưu.
        DatPhong donMoiNhat = null;

        for (DatPhong dp : controller.layDanhSachDatPhong()) {
            if (datPhong.getMaDatPhong().equals(dp.getMaDatPhong())) {
                donMoiNhat = dp;
                break;
            }
        }

        if (donMoiNhat == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Đơn đặt phòng không còn tồn tại.",
                    "Dữ liệu đã thay đổi",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!"GIU_PHONG".equals(donMoiNhat.getTrangThai())) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không thể sửa lịch.\n"
                    + "Trạng thái hiện tại: "
                    + donMoiNhat.getTrangThai(),
                    "Trạng thái đã thay đổi",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!ngayNhanGoc.equals(
                    donMoiNhat.getNgayNhanDuKien())
                || !ngayTraGoc.equals(
                    donMoiNhat.getNgayTraDuKien())) {

            JOptionPane.showMessageDialog(
                    this,
                    "Lịch đặt phòng đã được thay đổi "
                    + "bởi một thao tác khác.\n"
                    + "Vui lòng đóng hộp thoại và tải lại dữ liệu.",
                    "Lịch đã thay đổi",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Tạo đối tượng riêng, không sửa đối tượng gốc.
        DatPhong duLieuCapNhat = new DatPhong();

        duLieuCapNhat.setMaDatPhong(datPhong.getMaDatPhong());
        duLieuCapNhat.setNgayNhanDuKien(ngayNhanMoi);
        duLieuCapNhat.setNgayTraDuKien(ngayTraMoi);

        controller.capNhatLichDatPhong(
                duLieuCapNhat,
                ngayNhanGoc,
                ngayTraGoc
        );

        capNhatThanhCong = true;

        JOptionPane.showMessageDialog(
                this,
                "Cập nhật lịch đặt phòng thành công.",
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();

    } catch (DateTimeParseException ex) {
        JOptionPane.showMessageDialog(
                this,
                "Ngày giờ phải đúng định dạng yyyy-MM-dd HH:mm:ss.",
                "Sai định dạng",
                JOptionPane.WARNING_MESSAGE
        );

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(
                this,
                "Không thể cập nhật lịch đặt phòng:\n"
                + ex.getMessage(),
                "Lỗi cơ sở dữ liệu",
                JOptionPane.ERROR_MESSAGE
        );
    }
}


    public boolean isCapNhatThanhCong() {
        return capNhatThanhCong;
    }
}

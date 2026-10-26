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

public class ThemDatPhongDialog extends JDialog {

    private final DatPhongController controller;

    private final JTextField txtMaDatPhong = new JTextField(20);
    private final JTextField txtMaKhachHang = new JTextField(20);
    private final JTextField txtMaPhong = new JTextField(20);
    private final JTextField txtGiaPhong = new JTextField(20);
    private final JTextField txtNgayNhan = new JTextField(20);
    private final JTextField txtNgayTra = new JTextField(20);

    private boolean taoThanhCong = false;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ThemDatPhongDialog(
            JFrame parent,
            DatPhongController controller) {

        super(parent, "Them dat phong", true);
        this.controller = controller;

        setSize(480, 390);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        form.add(new JLabel("Ma dat phong:"));
        form.add(txtMaDatPhong);

        form.add(new JLabel("Ma khach hang:"));
        form.add(txtMaKhachHang);

        form.add(new JLabel("Ma phong:"));
        form.add(txtMaPhong);

        form.add(new JLabel("Gia phong chot:"));
        form.add(txtGiaPhong);

        form.add(new JLabel("Ngay nhan (yyyy-MM-dd HH:mm:ss):"));
        form.add(txtNgayNhan);

        form.add(new JLabel("Ngay tra (yyyy-MM-dd HH:mm:ss):"));
        form.add(txtNgayTra);

        JButton btnLuu = new JButton("Luu");
        JButton btnHuy = new JButton("Dong");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(btnLuu);
        buttons.add(btnHuy);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        btnLuu.addActionListener(e -> xuLyTaoDatPhong());
        btnHuy.addActionListener(e -> dispose());
    }

    private void xuLyTaoDatPhong() {
        try {
            String maDatPhong = txtMaDatPhong.getText().trim();
            String maKhachHang = txtMaKhachHang.getText().trim();
            String maPhong = txtMaPhong.getText().trim();
            String giaText = txtGiaPhong.getText().trim();
            String ngayNhanText = txtNgayNhan.getText().trim();
            String ngayTraText = txtNgayTra.getText().trim();

            if (maDatPhong.isEmpty()
                    || maKhachHang.isEmpty()
                    || maPhong.isEmpty()
                    || giaText.isEmpty()
                    || ngayNhanText.isEmpty()
                    || ngayTraText.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Vui long nhap day du thong tin."
                );
                return;
            }

            if (maDatPhong.length() > 20
                    || maKhachHang.length() > 20
                    || maPhong.length() > 20) {

                JOptionPane.showMessageDialog(
                        this,
                        "Ma dat phong, ma khach hang va ma phong toi da 20 ky tu."
                );
                return;
            }

            BigDecimal giaPhong = new BigDecimal(giaText);

            if (giaPhong.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Gia phong khong duoc am."
                );
                return;
            }

            LocalDateTime ngayNhan =
                    LocalDateTime.parse(ngayNhanText, FORMATTER);

            LocalDateTime ngayTra =
                    LocalDateTime.parse(ngayTraText, FORMATTER);

            if (!ngayTra.isAfter(ngayNhan)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Ngay tra phai sau ngay nhan."
                );
                return;
            }

            DatPhong dp = new DatPhong(
                    maDatPhong,
                    maKhachHang,
                    maPhong,
                    giaPhong,
                    Timestamp.valueOf(ngayNhan),
                    Timestamp.valueOf(ngayTra),
                    "GIU_PHONG"
            );

            controller.taoDatPhong(dp);

            taoThanhCong = true;

            JOptionPane.showMessageDialog(
                    this,
                    "Tao dat phong thanh cong!"
            );

            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Gia phong khong hop le."
            );

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Thoi gian phai dung dinh dang yyyy-MM-dd HH:mm:ss."
            );

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Khong the tao dat phong:\n" + e.getMessage(),
                    "Loi SQL Server",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public boolean isTaoThanhCong() {
        return taoThanhCong;
    }
}

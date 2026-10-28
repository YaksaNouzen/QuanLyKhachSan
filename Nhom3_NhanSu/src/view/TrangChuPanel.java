package view;

import dao.ThongKeDAO;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.TaiKhoan;
import util.GiaoDien;
import util.PhienDangNhap;

/**
 * TRANG CHỦ: lời chào + THỐNG KÊ NHÂN SỰ + thông tin đăng nhập.
 * Mỗi lần bấm menu "Trang chủ" là tạo panel mới nên số liệu luôn mới nhất.
 */
public class TrangChuPanel extends JPanel {

    private final ThongKeDAO dao = new ThongKeDAO();

    public TrangChuPanel() {
        initComponents();
    }

    private void initComponents() {
        TaiKhoan tk = PhienDangNhap.getTaiKhoan();
        setLayout(new BorderLayout());
        setBackground(GiaoDien.MAU_NEN);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        add(GiaoDien.taoTieuDe("XIN CHÀO, " + tk.getTenNV().toUpperCase(),
                "Chào mừng bạn đến với hệ thống quản lý khách sạn"), BorderLayout.NORTH);

        // ----- Lấy số liệu -----
        Map<String, Integer> theoTrangThai = dao.demTheoTrangThai();
        Map<String, Integer> theoBoPhan = dao.demTheoBoPhan();
        int dangLam = theoTrangThai.getOrDefault("Đang làm", 0);
        int nghiPhep = theoTrangThai.getOrDefault("Nghỉ phép", 0);
        int caHomNay = dao.demCaHomNay();
        int caChuaBanGiao = dao.demCaChuaBanGiao();

        // ----- Hàng 4 thẻ số liệu -----
        JPanel hangThe = new JPanel(new GridLayout(1, 4, 15, 0));
        hangThe.setOpaque(false);
        hangThe.add(GiaoDien.taoTheThongKe("Nhân viên đang làm", dangLam, GiaoDien.XANH_DUONG));
        hangThe.add(GiaoDien.taoTheThongKe("Đang nghỉ phép", nghiPhep, GiaoDien.CAM));
        hangThe.add(GiaoDien.taoTheThongKe("Ca làm hôm nay", caHomNay, GiaoDien.XANH_NGOC));
        // Còn ca chưa bàn giao thì tô đỏ để nhắc, hết rồi thì tô xanh
        hangThe.add(GiaoDien.taoTheThongKe("Ca chưa bàn giao", caChuaBanGiao,
                caChuaBanGiao > 0 ? GiaoDien.DO : GiaoDien.XANH_LA));
        hangThe.setAlignmentX(LEFT_ALIGNMENT);
        hangThe.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        // ----- 2 bảng: theo bộ phận và theo trạng thái -----
        JPanel hangBang = new JPanel(new GridLayout(1, 2, 15, 0));
        hangBang.setOpaque(false);
        hangBang.add(taoKhungBang("Nhân viên theo bộ phận", "Bộ phận", theoBoPhan));
        hangBang.add(taoKhungBang("Nhân viên theo trạng thái", "Trạng thái", theoTrangThai));
        hangBang.setAlignmentX(LEFT_ALIGNMENT);
        hangBang.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        // ----- Thông tin đăng nhập -----
        JPanel khungTK = GiaoDien.taoKhung("Thông tin đăng nhập");
        khungTK.setLayout(new GridLayout(2, 4, 10, 10));
        khungTK.add(GiaoDien.taoNhan("Tên đăng nhập:"));
        khungTK.add(GiaoDien.taoNhan(tk.getTenDangNhap()));
        khungTK.add(GiaoDien.taoNhan("Mã nhân viên:"));
        khungTK.add(GiaoDien.taoNhan(tk.getMaNV()));
        khungTK.add(GiaoDien.taoNhan("Họ tên:"));
        khungTK.add(GiaoDien.taoNhan(tk.getTenNV()));
        khungTK.add(GiaoDien.taoNhan("Vai trò:"));
        khungTK.add(GiaoDien.taoNhan(tk.getTenVaiTro()));
        khungTK.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JPanel giua = new JPanel();
        giua.setLayout(new BoxLayout(giua, BoxLayout.Y_AXIS));
        giua.setOpaque(false);
        giua.add(hangThe);
        giua.add(Box.createVerticalStrut(15));
        giua.add(hangBang);
        giua.add(Box.createVerticalStrut(15));
        giua.add(khungTK);
        add(giua, BorderLayout.CENTER);
    }

    // Khung có bảng 2 cột: tên nhóm - số người
    private JPanel taoKhungBang(String tieuDe, String tenCot, Map<String, Integer> duLieu) {
        JPanel khung = GiaoDien.taoKhung(tieuDe);
        khung.setLayout(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{tenCot, "Số người"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (Map.Entry<String, Integer> dong : duLieu.entrySet()) {
            model.addRow(new Object[]{dong.getKey(), dong.getValue()});
        }
        JTable bang = new JTable(model);
        GiaoDien.dinhDangBang(bang);
        khung.add(new JScrollPane(bang));
        return khung;
    }
}

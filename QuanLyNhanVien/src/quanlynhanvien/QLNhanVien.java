import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class QLNhanVien extends JFrame {

    // =========================
    // KHAI BÁO COMPONENT
    // =========================

    private JList<PhongBan> lstPhongBan;
    private JList<NhanVien> lstNhanVien;

    private DefaultListModel<PhongBan> modelPhongBan;
    private DefaultListModel<NhanVien> modelNhanVien;

    private JTextField txtMaNV;
    private JTextField txtHoTen;
    private JTextField txtPhai;
    private JTextField txtTuoi;

    private JButton btnAddPB;
    private JButton btnRemovePB;
    private JButton btnUpdatePB;

    private JButton btnAddNV;
    private JButton btnRemoveNV;
    private JButton btnUpdateNV;
    private JButton btnClear;

    // =========================
    // CONSTRUCTOR
    // =========================

    public QLNhanVien() {

        initComponents();

        loadPhongBan();

        clearNhanVien();
    }

    // =========================
    // TẠO GIAO DIỆN
    // =========================

    private void initComponents() {

        setTitle("Quản lý nhân viên");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(null);

        // =========================
        // TIÊU ĐỀ
        // =========================

        JLabel lblTitle =
                new JLabel("CÔNG TY TNHH THÀNH AN");

        lblTitle.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        32
                )
        );

        lblTitle.setForeground(
                new Color(170, 80, 220)
        );

        lblTitle.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        lblTitle.setBounds(
                0, 15, 880, 50
        );

        add(lblTitle);

        // =========================
        // PANEL PHÒNG BAN
        // =========================

        JPanel pnlPhongBan =
                new JPanel();

        pnlPhongBan.setLayout(null);

        pnlPhongBan.setBorder(
                BorderFactory.createTitledBorder(
                        "Danh sách phòng ban"
                )
        );

        pnlPhongBan.setBounds(
                10, 80, 280, 500
        );

        add(pnlPhongBan);

        // JList phòng ban

        modelPhongBan =
                new DefaultListModel<>();

        lstPhongBan =
                new JList<>(modelPhongBan);

        lstPhongBan.setFont(
                new Font("Arial", Font.PLAIN, 15)
        );

        JScrollPane scrollPhongBan =
                new JScrollPane(lstPhongBan);

        scrollPhongBan.setBounds(
                10, 25, 255, 370
        );

        pnlPhongBan.add(
                scrollPhongBan
        );

        // Button Add phòng ban

        btnAddPB =
                new JButton("Add");

        btnAddPB.setBounds(
                10, 410, 75, 35
        );

        pnlPhongBan.add(btnAddPB);

        // Remove

        btnRemovePB =
                new JButton("Remove");

        btnRemovePB.setBounds(
                90, 410, 85, 35
        );

        pnlPhongBan.add(btnRemovePB);

        // Update

        btnUpdatePB =
                new JButton("Update");

        btnUpdatePB.setBounds(
                180, 410, 85, 35
        );

        pnlPhongBan.add(btnUpdatePB);

        // =========================
        // PANEL NHÂN VIÊN
        // =========================

        JPanel pnlNhanVien =
                new JPanel();

        pnlNhanVien.setLayout(null);

        pnlNhanVien.setBorder(
                BorderFactory.createTitledBorder(
                        "Danh sách nhân viên"
                )
        );

        pnlNhanVien.setBounds(
                300, 80, 580, 500
        );

        add(pnlNhanVien);

        // JList nhân viên

        modelNhanVien =
                new DefaultListModel<>();

        lstNhanVien =
                new JList<>(modelNhanVien);

        lstNhanVien.setFont(
                new Font("Arial", Font.PLAIN, 15)
        );

        JScrollPane scrollNhanVien =
                new JScrollPane(lstNhanVien);

        scrollNhanVien.setBounds(
                10, 25, 550, 150
        );

        pnlNhanVien.add(
                scrollNhanVien
        );

        // =========================
        // MÃ NHÂN VIÊN
        // =========================

        JLabel lblMaNV =
                new JLabel("Mã nhân viên:");

        lblMaNV.setBounds(
                20, 200, 110, 30
        );

        pnlNhanVien.add(lblMaNV);

        txtMaNV =
                new JTextField();

        txtMaNV.setBounds(
                130, 200, 420, 30
        );

        pnlNhanVien.add(txtMaNV);

        // =========================
        // HỌ TÊN
        // =========================

        JLabel lblHoTen =
                new JLabel("Họ tên nhân viên:");

        lblHoTen.setBounds(
                20, 245, 110, 30
        );

        pnlNhanVien.add(lblHoTen);

        txtHoTen =
                new JTextField();

        txtHoTen.setBounds(
                130, 245, 420, 30
        );

        pnlNhanVien.add(txtHoTen);

        // =========================
        // PHÁI
        // =========================

        JLabel lblPhai =
                new JLabel("Phái:");

        lblPhai.setBounds(
                20, 290, 110, 30
        );

        pnlNhanVien.add(lblPhai);

        txtPhai =
                new JTextField();

        txtPhai.setBounds(
                130, 290, 420, 30
        );

        pnlNhanVien.add(txtPhai);

        // =========================
        // TUỔI
        // =========================

        JLabel lblTuoi =
                new JLabel("Tuổi:");

        lblTuoi.setBounds(
                20, 335, 110, 30
        );

        pnlNhanVien.add(lblTuoi);

        txtTuoi =
                new JTextField();

        txtTuoi.setBounds(
                130, 335, 420, 30
        );

        pnlNhanVien.add(txtTuoi);

        // =========================
        // BUTTON NHÂN VIÊN
        // =========================

        btnAddNV =
                new JButton("Add");

        btnAddNV.setBounds(
                120, 400, 80, 35
        );

        pnlNhanVien.add(btnAddNV);

        btnRemoveNV =
                new JButton("Remove");

        btnRemoveNV.setBounds(
                210, 400, 90, 35
        );

        pnlNhanVien.add(btnRemoveNV);

        btnUpdateNV =
                new JButton("Update");

        btnUpdateNV.setBounds(
                310, 400, 90, 35
        );

        pnlNhanVien.add(btnUpdateNV);

        btnClear =
                new JButton("Clear");

        btnClear.setBounds(
                410, 400, 80, 35
        );

        pnlNhanVien.add(btnClear);

        // =========================
        // SỰ KIỆN
        // =========================

        lstPhongBan.addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                loadNhanVien();
            }
        });

        lstNhanVien.addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                hienThiNhanVien();
            }
        });

        btnAddPB.addActionListener(e ->
                addPhongBan()
        );

        btnRemovePB.addActionListener(e ->
                removePhongBan()
        );

        btnUpdatePB.addActionListener(e ->
                updatePhongBan()
        );

        btnAddNV.addActionListener(e ->
                addNhanVien()
        );

        btnRemoveNV.addActionListener(e ->
                removeNhanVien()
        );

        btnUpdateNV.addActionListener(e ->
                updateNhanVien()
        );

        btnClear.addActionListener(e ->
                clearNhanVien()
        );
    }

    // =====================================================
    // LOAD PHÒNG BAN
    // =====================================================

    private void loadPhongBan() {

        modelPhongBan.clear();

        String sql =
                "SELECT * FROM PhongBan ORDER BY MaPB";

        try (
                Connection conn =
                        DBConnection.getConnection();

                Statement st =
                        conn.createStatement();

                ResultSet rs =
                        st.executeQuery(sql)
        ) {

            while (rs.next()) {

                int maPB =
                        rs.getInt("MaPB");

                String tenPB =
                        rs.getString("TenPB");

                PhongBan pb =
                        new PhongBan(
                                maPB,
                                tenPB
                        );

                modelPhongBan.addElement(pb);
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Không thể tải phòng ban!\n"
                    + e.getMessage()
            );
        }
    }

    // =====================================================
    // LOAD NHÂN VIÊN THEO PHÒNG BAN
    // =====================================================

    private void loadNhanVien() {

        modelNhanVien.clear();

        PhongBan pb =
                lstPhongBan.getSelectedValue();

        if (pb == null) {

            return;
        }

        String sql =
                "SELECT * FROM NhanVien "
                + "WHERE MaPB = ?";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    pb.getMaPB()
            );

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                NhanVien nv =
                        new NhanVien();

                nv.setMaNV(
                        rs.getString("MaNV")
                );

                nv.setHoTen(
                        rs.getString("HoTen")
                );

                nv.setPhai(
                        rs.getString("Phai")
                );

                nv.setTuoi(
                        rs.getInt("Tuoi")
                );

                modelNhanVien.addElement(nv);
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Không thể tải nhân viên!\n"
                    + e.getMessage()
            );
        }
    }

    // =====================================================
    // CLICK NHÂN VIÊN
    // =====================================================

    private void hienThiNhanVien() {

        NhanVien nv =
                lstNhanVien.getSelectedValue();

        if (nv == null) {

            return;
        }

        txtMaNV.setText(
                nv.getMaNV()
        );

        txtHoTen.setText(
                nv.getHoTen()
        );

        txtPhai.setText(
                nv.getPhai()
        );

        txtTuoi.setText(
                String.valueOf(
                        nv.getTuoi()
                )
        );
    }

    // =====================================================
    // THÊM PHÒNG BAN
    // =====================================================

    private void addPhongBan() {

        try {

            String ma =
                    JOptionPane.showInputDialog(
                            this,
                            "Nhập mã phòng ban:"
                    );

            if (ma == null) {

                return;
            }

            String ten =
                    JOptionPane.showInputDialog(
                            this,
                            "Nhập tên phòng ban:"
                    );

            if (ten == null
                    || ten.trim().isEmpty()) {

                return;
            }

            int maPB =
                    Integer.parseInt(
                            ma.trim()
                    );

            String sql =
                    "INSERT INTO PhongBan "
                    + "(MaPB, TenPB) "
                    + "VALUES (?, ?)";

            try (
                    Connection conn =
                            DBConnection.getConnection();

                    PreparedStatement ps =
                            conn.prepareStatement(sql)
            ) {

                ps.setInt(1, maPB);
                ps.setString(2, ten);

                ps.executeUpdate();
            }

            loadPhongBan();

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Mã phòng ban phải là số!"
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Không thể thêm phòng ban!\n"
                    + e.getMessage()
            );
        }
    }

    // =====================================================
    // XÓA PHÒNG BAN
    // =====================================================

    private void removePhongBan() {

        PhongBan pb =
                lstPhongBan.getSelectedValue();

        if (pb == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Hãy chọn phòng ban!"
            );

            return;
        }

        String sql =
                "DELETE FROM PhongBan "
                + "WHERE MaPB = ?";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    pb.getMaPB()
            );

            ps.executeUpdate();

            loadPhongBan();

            modelNhanVien.clear();

            clearNhanVien();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Không thể xóa phòng ban.\n"
                    + "Có thể phòng ban đang có nhân viên."
            );
        }
    }

    // =====================================================
    // SỬA PHÒNG BAN
    // =====================================================

    private void updatePhongBan() {

        PhongBan pb =
                lstPhongBan.getSelectedValue();

        if (pb == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Hãy chọn phòng ban!"
            );

            return;
        }

        String tenMoi =
                JOptionPane.showInputDialog(
                        this,
                        "Tên phòng ban mới:",
                        pb.getTenPB()
                );

        if (tenMoi == null
                || tenMoi.trim().isEmpty()) {

            return;
        }

        String sql =
                "UPDATE PhongBan "
                + "SET TenPB = ? "
                + "WHERE MaPB = ?";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    tenMoi
            );

            ps.setInt(
                    2,
                    pb.getMaPB()
            );

            ps.executeUpdate();

            loadPhongBan();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Không thể cập nhật!"
            );
        }
    }

    // =====================================================
    // THÊM NHÂN VIÊN
    // =====================================================

    private void addNhanVien() {

        PhongBan pb =
                lstPhongBan.getSelectedValue();

        if (pb == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Hãy chọn phòng ban!"
            );

            return;
        }

        String maNV =
                txtMaNV.getText().trim();

        String hoTen =
                txtHoTen.getText().trim();

        String phai =
                txtPhai.getText().trim();

        if (maNV.isEmpty()
                || hoTen.isEmpty()
                || phai.isEmpty()
                || txtTuoi.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Không được để trống!"
            );

            return;
        }

        try {

            int tuoi =
                    Integer.parseInt(
                            txtTuoi.getText().trim()
                    );

            String sql =
                    "INSERT INTO NhanVien "
                    + "(MaNV, HoTen, Phai, Tuoi, MaPB) "
                    + "VALUES (?, ?, ?, ?, ?)";

            try (
                    Connection conn =
                            DBConnection.getConnection();

                    PreparedStatement ps =
                            conn.prepareStatement(sql)
            ) {

                ps.setString(1, maNV);
                ps.setString(2, hoTen);
                ps.setString(3, phai);
                ps.setInt(4, tuoi);
                ps.setInt(
                        5,
                        pb.getMaPB()
                );

                ps.executeUpdate();
            }

            loadNhanVien();

            clearNhanVien();

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Tuổi phải là số!"
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Không thể thêm nhân viên!\n"
                    + e.getMessage()
            );
        }
    }

    // =====================================================
    // XÓA NHÂN VIÊN
    // =====================================================

    private void removeNhanVien() {

        NhanVien nv =
                lstNhanVien.getSelectedValue();

        if (nv == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Hãy chọn nhân viên!"
            );

            return;
        }

        String sql =
                "DELETE FROM NhanVien "
                + "WHERE MaNV = ?";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    nv.getMaNV()
            );

            ps.executeUpdate();

            loadNhanVien();

            clearNhanVien();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Không thể xóa nhân viên!"
            );
        }
    }

    // =====================================================
    // SỬA NHÂN VIÊN
    // =====================================================

    private void updateNhanVien() {

        NhanVien nv =
                lstNhanVien.getSelectedValue();

        if (nv == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Hãy chọn nhân viên!"
            );

            return;
        }

        String hoTen =
                txtHoTen.getText().trim();

        String phai =
                txtPhai.getText().trim();

        if (hoTen.isEmpty()
                || phai.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Không được để trống!"
            );

            return;
        }

        try {

            int tuoi =
                    Integer.parseInt(
                            txtTuoi.getText().trim()
                    );

            String sql =
                    "UPDATE NhanVien "
                    + "SET HoTen = ?, "
                    + "Phai = ?, "
                    + "Tuoi = ? "
                    + "WHERE MaNV = ?";

            try (
                    Connection conn =
                            DBConnection.getConnection();

                    PreparedStatement ps =
                            conn.prepareStatement(sql)
            ) {

                ps.setString(1, hoTen);
                ps.setString(2, phai);
                ps.setInt(3, tuoi);
                ps.setString(
                        4,
                        nv.getMaNV()
                );

                ps.executeUpdate();
            }

            loadNhanVien();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Tuổi phải là số!"
            );
        }
    }

    // =====================================================
    // CLEAR
    // =====================================================

    private void clearNhanVien() {

        txtMaNV.setText("");
        txtHoTen.setText("");
        txtPhai.setText("");
        txtTuoi.setText("");

        lstNhanVien.clearSelection();
    }

    // =====================================================
    // MAIN
    // =====================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new QLNhanVien().setVisible(true);
        });
    }
}
package app.dashboard;

import Module_TaiChinh.model.ThongKeTheoNgay;
import Module_TaiChinh.service.ThongKeTaiChinhService;
import Module_TaiKhoanPhanQuyen.model.TaiKhoan;
import app.dashboard.DashboardData.CaHomNay;
import app.dashboard.DashboardData.CongViec;
import common.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Gom dữ liệu từ schema hiện có, chỉ dùng SELECT và chỉ lấy việc của người đăng nhập. */
public final class DashboardService {

    public DashboardData taiDuLieu(TaiKhoan taiKhoan, LocalDate homNay) throws SQLException {
        if (taiKhoan == null || taiKhoan.getMaNV() == null) {
            throw new IllegalArgumentException("Cần đăng nhập để xem lịch và công việc cá nhân.");
        }
        List<CaHomNay> ca = new ArrayList<>();
        List<CongViec> viec = new ArrayList<>();
        List<CongViec> canhBao = new ArrayList<>();
        String ten;
        try (Connection con = DatabaseConnection.getConnection()) {
            ten = layTenNhanVien(con, taiKhoan.getMaNV());
            layCaHomNay(con, taiKhoan.getMaNV(), homNay, ca);
            layBanGiao(con, taiKhoan.getMaNV(), homNay, viec, canhBao);
            // Việc vận hành đang lưu tên tự do, nên không thể gán an toàn nếu trùng họ tên.
            if (!ten.isBlank() && tenDuyNhat(con, ten)) {
                layVeSinh(con, ten, homNay, viec, canhBao);
                layBaoTri(con, ten, homNay, viec, canhBao);
            } else if (!ten.isBlank()) {
                canhBao.add(new CongViec("Chưa xác định được việc theo tên",
                        "Có nhiều nhân viên trùng họ tên; hãy xem trực tiếp trong module công việc.",
                        null));
            }
        }
        List<ThongKeTheoNgay> doanhThu = List.of();
        if (duocXemDoanhThu(taiKhoan)) {
            doanhThu = new ThongKeTaiChinhService().thongKeTheoNgay(
                    homNay.minusDays(6).toString(), homNay.toString());
        }
        return new DashboardData(ten, List.copyOf(ca), List.copyOf(viec),
                List.copyOf(canhBao), List.copyOf(doanhThu));
    }

    public static boolean duocXemDoanhThu(TaiKhoan taiKhoan) {
        return taiKhoan != null && ("ADMIN".equals(taiKhoan.getMaVaiTro())
                || "KETOAN".equals(taiKhoan.getMaVaiTro()));
    }

    private String layTenNhanVien(Connection con, String maNV) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT HoTen FROM dbo.NhanVien WHERE MaNV = ?")) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1).trim() : "";
            }
        }
    }

    private boolean tenDuyNhat(Connection con, String ten) throws SQLException {
        String sql = "SELECT COUNT(*) FROM dbo.NhanVien "
                + "WHERE LTRIM(RTRIM(HoTen)) COLLATE Latin1_General_100_BIN2 = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ten);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) == 1;
            }
        }
    }

    private void layCaHomNay(Connection con, String maNV, LocalDate ngay,
            List<CaHomNay> ca) throws SQLException {
        String sql = "SELECT c.TenCa, c.GioBatDau, c.GioKetThuc, p.TrangThai "
                + "FROM dbo.PhanCa p JOIN dbo.CaLam c ON c.MaCa = p.MaCa "
                + "WHERE p.MaNV = ? AND p.NgayLam = ? ORDER BY c.GioBatDau";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNV);
            ps.setDate(2, Date.valueOf(ngay));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String gio = rs.getTime("GioBatDau").toLocalTime().toString()
                            + " – " + rs.getTime("GioKetThuc").toLocalTime();
                    ca.add(new CaHomNay(rs.getString("TenCa"), gio,
                            rs.getString("TrangThai")));
                }
            }
        }
    }

    private void layBanGiao(Connection con, String maNV, LocalDate ngay,
            List<CongViec> viec, List<CongViec> canhBao) throws SQLException {
        String sql = "SELECT TOP (10) p.NgayLam, c.TenCa FROM dbo.PhanCa p "
                + "JOIN dbo.CaLam c ON c.MaCa = p.MaCa "
                + "WHERE p.MaNV = ? AND p.NgayLam <= ? "
                + "AND p.TrangThai = N'Chưa bàn giao' ORDER BY p.NgayLam DESC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNV);
            ps.setDate(2, Date.valueOf(ngay));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate ngayCa = rs.getDate("NgayLam").toLocalDate();
                    CongViec item = new CongViec("Bàn giao " + rs.getString("TenCa"),
                            "Ca ngày " + ngayCa, "PHANCA");
                    (ngayCa.isBefore(ngay) ? canhBao : viec).add(item);
                }
            }
        }
    }

    private void layVeSinh(Connection con, String ten, LocalDate ngay,
            List<CongViec> viec, List<CongViec> canhBao) throws SQLException {
        String sql = "SELECT TOP (10) MaPhong, NoiDung, NgayPhanCong "
                + "FROM dbo.VeSinh WHERE LTRIM(RTRIM(NguoiPhuTrach)) "
                + "COLLATE Latin1_General_100_BIN2 = ? "
                + "AND TrangThai IN ('CHO_DON', 'DANG_DON') ORDER BY NgayPhanCong";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ten);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate ngayGiao = rs.getTimestamp("NgayPhanCong")
                            .toLocalDateTime().toLocalDate();
                    CongViec item = new CongViec("Dọn phòng " + rs.getString("MaPhong"),
                            rs.getString("NoiDung"), "BUONGPHONG");
                    (ngayGiao.isBefore(ngay) ? canhBao : viec).add(item);
                }
            }
        }
    }

    private void layBaoTri(Connection con, String ten, LocalDate ngay,
            List<CongViec> viec, List<CongViec> canhBao) throws SQLException {
        String sql = "SELECT TOP (10) MaPhong, NoiDung, NgayBao, MucDo "
                + "FROM dbo.BaoTri WHERE LTRIM(RTRIM(NguoiPhuTrach)) "
                + "COLLATE Latin1_General_100_BIN2 = ? "
                + "AND TrangThai IN ('BAO_HONG', 'DANG_SUA') ORDER BY NgayBao";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ten);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate ngayBao = rs.getTimestamp("NgayBao")
                            .toLocalDateTime().toLocalDate();
                    boolean khan = "CAO".equals(rs.getString("MucDo"))
                            || ngayBao.isBefore(ngay);
                    CongViec item = new CongViec("Bảo trì phòng " + rs.getString("MaPhong"),
                            rs.getString("NoiDung"), "BAOTRI");
                    (khan ? canhBao : viec).add(item);
                }
            }
        }
    }
}

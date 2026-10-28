package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import model.CaLam;
import model.PhanCa;
import util.DBConnection;

public class PhanCaDAO {

    /**
     * Xem lịch trực: lấy các ca từ ngày -> đến ngày, có thể lọc theo 1 nhân viên.
     * maNV rỗng thì lấy tất cả nhân viên.
     */
    public List<PhanCa> getLichTruc(Date tuNgay, Date denNgay, String maNV) {
        List<PhanCa> list = new ArrayList<>();
        String sql = "SELECT pc.*, nv.HoTen, ca.TenCa, ca.GioBatDau, ca.GioKetThuc "
                + "FROM PhanCa pc "
                + "JOIN NhanVien nv ON pc.MaNV = nv.MaNV "
                + "JOIN CaLam ca ON pc.MaCa = ca.MaCa "
                + "WHERE pc.NgayLam BETWEEN ? AND ? "
                + "AND (? = '' OR pc.MaNV = ?) "
                + "ORDER BY pc.NgayLam, ca.GioBatDau";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, tuNgay);
            ps.setDate(2, denNgay);
            ps.setString(3, maNV);
            ps.setString(4, maNV);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PhanCa pc = new PhanCa();
                pc.setMaPhanCa(rs.getInt("MaPhanCa"));
                pc.setMaNV(rs.getString("MaNV"));
                pc.setMaCa(rs.getString("MaCa"));
                pc.setNgayLam(rs.getDate("NgayLam"));
                pc.setTrangThai(rs.getString("TrangThai"));
                pc.setNoiDungBanGiao(rs.getString("NoiDungBanGiao"));
                pc.setTenNV(rs.getString("HoTen"));
                pc.setTenCa(rs.getString("TenCa"));
                String gioBD = CaLam.catGio(rs.getTime("GioBatDau"));
                String gioKT = CaLam.catGio(rs.getTime("GioKetThuc"));
                pc.setGioLam(gioBD + " - " + gioKT);
                list.add(pc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy danh sách ca mà 1 nhân viên đã được phân trong 1 ngày
    public List<CaLam> getCaCuaNhanVienTrongNgay(String maNV, Date ngay) {
        List<CaLam> list = new ArrayList<>();
        String sql = "SELECT ca.* FROM PhanCa pc JOIN CaLam ca ON pc.MaCa = ca.MaCa "
                + "WHERE pc.MaNV = ? AND pc.NgayLam = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            ps.setDate(2, ngay);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String gioBD = CaLam.catGio(rs.getTime("GioBatDau"));
                String gioKT = CaLam.catGio(rs.getTime("GioKetThuc"));
                list.add(new CaLam(rs.getString("MaCa"), rs.getString("TenCa"), gioBD, gioKT));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * KIỂM TRA CA TRÙNG
     * Trả về ca bị trùng (nếu có), không trùng thì trả về null.
     *
     * Hai khoảng giờ [A1, A2) và [B1, B2) bị chồng nhau khi:  A1 < B2  và  B1 < A2
     * Kiểm tra cả ngày trước và ngày sau để phát hiện ca qua đêm bị chồng lấn.
     * VD: ca 22:00 hôm trước - 06:00 hôm nay sẽ trùng ca 05:00 - 07:00 hôm nay.
     */
    public CaLam kiemTraTrungCa(String maNV, CaLam caMoi, Date ngay) {
        LocalDate ngayMoi = ngay.toLocalDate();
        Date tuNgay = Date.valueOf(ngayMoi.minusDays(1));
        Date denNgay = Date.valueOf(ngayMoi.plusDays(1));
        int batDauMoi = caMoi.getPhutBatDau();
        int ketThucMoi = caMoi.getPhutKetThuc();

        String sql = "SELECT pc.NgayLam, ca.* FROM PhanCa pc "
                + "JOIN CaLam ca ON pc.MaCa = ca.MaCa "
                + "WHERE pc.MaNV = ? AND pc.NgayLam BETWEEN ? AND ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            ps.setDate(2, tuNgay);
            ps.setDate(3, denNgay);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CaLam caDaCo = new CaLam(
                            rs.getString("MaCa"),
                            rs.getString("TenCa"),
                            CaLam.catGio(rs.getTime("GioBatDau")),
                            CaLam.catGio(rs.getTime("GioKetThuc")));
                    long lechNgay = ChronoUnit.DAYS.between(
                            ngayMoi, rs.getDate("NgayLam").toLocalDate());
                    int dichPhut = Math.toIntExact(lechNgay * 24L * 60L);
                    int batDauCu = caDaCo.getPhutBatDau() + dichPhut;
                    int ketThucCu = caDaCo.getPhutKetThuc() + dichPhut;
                    boolean chongNhau = batDauMoi < ketThucCu
                            && batDauCu < ketThucMoi;
                    if (chongNhau) {
                        return caDaCo;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean them(PhanCa pc) {
        String sql = "INSERT INTO PhanCa (MaNV, MaCa, NgayLam, TrangThai) VALUES (?, ?, ?, N'Chưa bàn giao')";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pc.getMaNV());
            ps.setString(2, pc.getMaCa());
            ps.setDate(3, pc.getNgayLam());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean xoa(int maPhanCa) {
        String sql = "DELETE FROM PhanCa WHERE MaPhanCa = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maPhanCa);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Bàn giao ca: ghi nội dung bàn giao và đổi trạng thái
    public boolean banGiao(int maPhanCa, String noiDung) {
        String sql = "UPDATE PhanCa SET NoiDungBanGiao = ?, TrangThai = N'Đã bàn giao' WHERE MaPhanCa = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, noiDung);
            ps.setInt(2, maPhanCa);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

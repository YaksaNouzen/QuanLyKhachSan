package module.khachhang.dao;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import common.database.DatabaseConnection;
import module.datphong.model.DatPhong;
import module.khachhang.model.KhachHang;

public class KhachHangDAO {
    private static final String COLUMNS =
            "MaKhachHang, HoTen, LoaiGiayTo, SoGiayTo, QuocGiaCap, "
            + "SoDienThoai, Email, NgaySinh, GioiTinh, QuocTich, "
            + "DiaChi, NgayTao, TrangThai";

    public List<KhachHang> timKhachHang(String tuKhoa, String trangThai)
            throws SQLException {
        String keyword = tuKhoa == null ? "" : tuKhoa.trim();
        String status = trangThai == null ? "" : trangThai.trim();
        String like = "%" + keyword + "%";
        String sql = "SELECT " + COLUMNS + " FROM dbo.KhachHang "
                + "WHERE (? = '' OR MaKhachHang LIKE ? OR HoTen LIKE ? "
                + "OR SoDienThoai LIKE ?) AND (? = '' OR TrangThai = ?) "
                + "ORDER BY HoTen, MaKhachHang";
        List<KhachHang> result = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, keyword);
            statement.setString(2, like);
            statement.setString(3, like);
            statement.setString(4, like);
            statement.setString(5, status);
            statement.setString(6, status);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) result.add(map(rs));
            }
        }
        return result;
    }

    public String themKhachHang(KhachHang khachHang) throws SQLException {
        String sql = "INSERT INTO dbo.KhachHang "
                + "(MaKhachHang, HoTen, LoaiGiayTo, SoGiayTo, QuocGiaCap, "
                + "SoDienThoai, Email, NgaySinh, GioiTinh, QuocTich, DiaChi) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            try {
                // Kiểm tra khóa nghiệp vụ trước khi tiêu thụ một số sequence.
                if (khachHang.getSoGiayTo() != null) {
                    String duplicateSql = "SELECT 1 FROM dbo.KhachHang WITH (UPDLOCK,HOLDLOCK) "
                            + "WHERE LoaiGiayTo=? AND QuocGiaCap=? AND SoGiayTo=?";
                    try (PreparedStatement duplicate = connection.prepareStatement(duplicateSql)) {
                        duplicate.setString(1, khachHang.getLoaiGiayTo());
                        duplicate.setString(2, khachHang.getQuocGiaCap());
                        duplicate.setString(3, khachHang.getSoGiayTo());
                        try (ResultSet rs = duplicate.executeQuery()) {
                            if (rs.next()) {
                                throw new SQLException("Khách hàng có giấy tờ này đã tồn tại.");
                            }
                        }
                    }
                }

                String id;
                try (CallableStatement statement = connection.prepareCall(
                        "{CALL dbo.sp_LayMaTuDong(?)}")) {
                    statement.setString(1, "KHACH_HANG");
                    try (ResultSet rs = statement.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Không nhận được mã khách hàng mới.");
                        id = rs.getString("MaMoi");
                    }
                }
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, id);
                    bindEditableFields(statement, khachHang, 2);
                    statement.executeUpdate();
                }
                connection.commit();
                return id;
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public void capNhatKhachHang(KhachHang khachHang) throws SQLException {
        String sql = "UPDATE dbo.KhachHang SET HoTen=?, LoaiGiayTo=?, SoGiayTo=?, "
                + "QuocGiaCap=?, SoDienThoai=?, Email=?, NgaySinh=?, GioiTinh=?, "
                + "QuocTich=?, DiaChi=? WHERE MaKhachHang=? AND TrangThai='HOAT_DONG'";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindEditableFields(statement, khachHang, 1);
            statement.setString(11, khachHang.getMaKhachHang());
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Khách hàng không tồn tại hoặc đã ngừng hoạt động.");
            }
        }
    }

    public void ngungHoatDong(String maKhachHang) throws SQLException {
        String sql = "UPDATE dbo.KhachHang SET TrangThai='NGUNG_HOAT_DONG' "
                + "WHERE MaKhachHang=? AND TrangThai='HOAT_DONG'";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maKhachHang);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Khách hàng không tồn tại hoặc đã ngừng hoạt động.");
            }
        }
    }

    public void kichHoatLai(String maKhachHang) throws SQLException {
        String sql = "UPDATE dbo.KhachHang SET TrangThai='HOAT_DONG' "
                + "WHERE MaKhachHang=? AND TrangThai='NGUNG_HOAT_DONG'";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maKhachHang);
            if (statement.executeUpdate() != 1) {
                throw new SQLException(
                        "Khách hàng không tồn tại hoặc không ở trạng thái ngừng hoạt động.");
            }
        }
    }

    public List<DatPhong> layLichSu(String maKhachHang) throws SQLException {
        String sql = "SELECT MaDatPhong, MaKhachHang, MaPhong, GiaPhongChot, "
                + "NgayDat, NgayNhan, NgayTra, NgayNhanDuKien, "
                + "NgayTraDuKien, TrangThai FROM dbo.DatPhong "
                + "WHERE MaKhachHang=? ORDER BY NgayNhanDuKien DESC";
        List<DatPhong> result = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maKhachHang);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    DatPhong item = new DatPhong();
                    item.setMaDatPhong(rs.getString("MaDatPhong"));
                    item.setMaKhachHang(rs.getString("MaKhachHang"));
                    item.setMaPhong(rs.getString("MaPhong"));
                    item.setGiaPhongChot(rs.getBigDecimal("GiaPhongChot"));
                    item.setNgayDat(rs.getTimestamp("NgayDat"));
                    item.setNgayNhan(rs.getTimestamp("NgayNhan"));
                    item.setNgayTra(rs.getTimestamp("NgayTra"));
                    item.setNgayNhanDuKien(rs.getTimestamp("NgayNhanDuKien"));
                    item.setNgayTraDuKien(rs.getTimestamp("NgayTraDuKien"));
                    item.setTrangThai(rs.getString("TrangThai"));
                    result.add(item);
                }
            }
        }
        return result;
    }

    private void bindEditableFields(
            PreparedStatement statement, KhachHang value, int start) throws SQLException {
        statement.setString(start, value.getHoTen());
        statement.setString(start + 1, value.getLoaiGiayTo());
        statement.setString(start + 2, value.getSoGiayTo());
        statement.setString(start + 3, value.getQuocGiaCap());
        statement.setString(start + 4, value.getSoDienThoai());
        statement.setString(start + 5, value.getEmail());
        if (value.getNgaySinh() == null) statement.setNull(start + 6, java.sql.Types.DATE);
        else statement.setDate(start + 6, Date.valueOf(value.getNgaySinh()));
        statement.setString(start + 7, value.getGioiTinh());
        statement.setString(start + 8, value.getQuocTich());
        statement.setString(start + 9, value.getDiaChi());
    }

    private KhachHang map(ResultSet rs) throws SQLException {
        KhachHang item = new KhachHang();
        item.setMaKhachHang(rs.getString("MaKhachHang"));
        item.setHoTen(rs.getString("HoTen"));
        item.setLoaiGiayTo(rs.getString("LoaiGiayTo"));
        item.setSoGiayTo(rs.getString("SoGiayTo"));
        item.setQuocGiaCap(rs.getString("QuocGiaCap"));
        item.setSoDienThoai(rs.getString("SoDienThoai"));
        item.setEmail(rs.getString("Email"));
        Date birthDate = rs.getDate("NgaySinh");
        item.setNgaySinh(birthDate == null ? null : birthDate.toLocalDate());
        item.setGioiTinh(rs.getString("GioiTinh"));
        item.setQuocTich(rs.getString("QuocTich"));
        item.setDiaChi(rs.getString("DiaChi"));
        item.setNgayTao(rs.getTimestamp("NgayTao"));
        item.setTrangThai(rs.getString("TrangThai"));
        return item;
    }
}

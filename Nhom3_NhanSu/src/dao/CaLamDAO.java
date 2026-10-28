package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import model.CaLam;
import util.DBConnection;

public class CaLamDAO {

    public List<CaLam> getAll() {
        List<CaLam> list = new ArrayList<>();
        String sql = "SELECT * FROM CaLam ORDER BY GioBatDau";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(docDong(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public CaLam getById(String maCa) {
        String sql = "SELECT * FROM CaLam WHERE MaCa = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maCa);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return docDong(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean them(CaLam ca) {
        String sql = "INSERT INTO CaLam VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ca.getMaCa());
            ps.setString(2, ca.getTenCa());
            // "06:00" -> Time 06:00:00
            ps.setTime(3, Time.valueOf(ca.getGioBatDau() + ":00"));
            ps.setTime(4, Time.valueOf(ca.getGioKetThuc() + ":00"));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean sua(CaLam ca) {
        String sql = "UPDATE CaLam SET TenCa = ?, GioBatDau = ?, GioKetThuc = ? WHERE MaCa = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ca.getTenCa());
            ps.setTime(2, Time.valueOf(ca.getGioBatDau() + ":00"));
            ps.setTime(3, Time.valueOf(ca.getGioKetThuc() + ":00"));
            ps.setString(4, ca.getMaCa());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Ca đã được phân cho nhân viên thì không xóa được (lỗi khóa ngoại)
    public boolean xoa(String maCa) {
        String sql = "DELETE FROM CaLam WHERE MaCa = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maCa);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private CaLam docDong(ResultSet rs) throws SQLException {
        String gioBD = CaLam.catGio(rs.getTime("GioBatDau"));
        String gioKT = CaLam.catGio(rs.getTime("GioKetThuc"));
        return new CaLam(rs.getString("MaCa"), rs.getString("TenCa"), gioBD, gioKT);
    }
}

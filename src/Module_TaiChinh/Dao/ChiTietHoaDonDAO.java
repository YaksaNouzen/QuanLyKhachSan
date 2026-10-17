/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Module_TaiChinh.Dao;


import Module_TaiChinh.model.ChiTietHoaDon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAO {

    // 1. LẤY TOÀN BỘ CHI TIẾT HÓA ĐƠN
    public List<ChiTietHoaDon> getAll() {

        List<ChiTietHoaDon> list = new ArrayList<>();

        String sql = "SELECT * FROM ChiTietHoaDon ORDER BY MaChiTiet";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                ChiTietHoaDon ct = new ChiTietHoaDon();

                ct.setMaChiTiet(rs.getString("MaChiTiet"));
                ct.setMaHoaDon(rs.getString("MaHoaDon"));
                ct.setMaPhong(rs.getString("MaPhong"));
                ct.setMaDichVu(rs.getString("MaDichVu"));
                ct.setLoaiChiPhi(rs.getString("LoaiChiPhi"));
                ct.setMoTa(rs.getString("MoTa"));
                ct.setSoLuong(rs.getInt("SoLuong"));
                ct.setDonGia(rs.getDouble("DonGia"));
                ct.setThanhTien(rs.getDouble("ThanhTien"));

                list.add(ct);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    // 2. LẤY CHI TIẾT THEO MÃ HÓA ĐƠN
    public List<ChiTietHoaDon> getByMaHoaDon(String maHoaDon) {

        List<ChiTietHoaDon> list = new ArrayList<>();

        String sql = """
                SELECT *
                FROM ChiTietHoaDon
                WHERE MaHoaDon = ?
                ORDER BY MaChiTiet
                """;

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, maHoaDon);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    ChiTietHoaDon ct = new ChiTietHoaDon();

                    ct.setMaChiTiet(rs.getString("MaChiTiet"));
                    ct.setMaHoaDon(rs.getString("MaHoaDon"));
                    ct.setMaPhong(rs.getString("MaPhong"));
                    ct.setMaDichVu(rs.getString("MaDichVu"));
                    ct.setLoaiChiPhi(rs.getString("LoaiChiPhi"));
                    ct.setMoTa(rs.getString("MoTa"));
                    ct.setSoLuong(rs.getInt("SoLuong"));
                    ct.setDonGia(rs.getDouble("DonGia"));
                    ct.setThanhTien(rs.getDouble("ThanhTien"));

                    list.add(ct);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    // 3. THÊM CHI TIẾT HÓA ĐƠN
    public boolean insert(ChiTietHoaDon ct) {

        String sql = """
                INSERT INTO ChiTietHoaDon
                (
                    MaChiTiet, MaHoaDon, MaPhong, MaDichVu,
                    LoaiChiPhi, MoTa, SoLuong, DonGia, ThanhTien
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, ct.getMaChiTiet());
            ps.setString(2, ct.getMaHoaDon());

            // MaPhong và MaDichVu có thể để NULL
            if (ct.getMaPhong() == null
                    || ct.getMaPhong().trim().isEmpty()) {
                ps.setNull(3, java.sql.Types.VARCHAR);
            } else {
                ps.setString(3, ct.getMaPhong());
            }

            if (ct.getMaDichVu() == null
                    || ct.getMaDichVu().trim().isEmpty()) {
                ps.setNull(4, java.sql.Types.VARCHAR);
            } else {
                ps.setString(4, ct.getMaDichVu());
            }

            ps.setString(5, ct.getLoaiChiPhi());
            ps.setString(6, ct.getMoTa());
            ps.setInt(7, ct.getSoLuong());
            ps.setDouble(8, ct.getDonGia());
            ps.setDouble(9, ct.getThanhTien());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // 4. SỬA CHI TIẾT HÓA ĐƠN
    public boolean update(ChiTietHoaDon ct) {

        String sql = """
                UPDATE ChiTietHoaDon
                SET MaHoaDon = ?,
                    MaPhong = ?,
                    MaDichVu = ?,
                    LoaiChiPhi = ?,
                    MoTa = ?,
                    SoLuong = ?,
                    DonGia = ?,
                    ThanhTien = ?
                WHERE MaChiTiet = ?
                """;

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, ct.getMaHoaDon());

            if (ct.getMaPhong() == null
                    || ct.getMaPhong().trim().isEmpty()) {
                ps.setNull(2, java.sql.Types.VARCHAR);
            } else {
                ps.setString(2, ct.getMaPhong());
            }

            if (ct.getMaDichVu() == null
                    || ct.getMaDichVu().trim().isEmpty()) {
                ps.setNull(3, java.sql.Types.VARCHAR);
            } else {
                ps.setString(3, ct.getMaDichVu());
            }

            ps.setString(4, ct.getLoaiChiPhi());
            ps.setString(5, ct.getMoTa());
            ps.setInt(6, ct.getSoLuong());
            ps.setDouble(7, ct.getDonGia());
            ps.setDouble(8, ct.getThanhTien());
            ps.setString(9, ct.getMaChiTiet());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // 5. XÓA CHI TIẾT HÓA ĐƠN THEO MÃ CHI TIẾT
    public boolean delete(String maChiTiet) {

        String sql =
                "DELETE FROM ChiTietHoaDon WHERE MaChiTiet = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, maChiTiet);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // 6. XÓA TẤT CẢ CHI TIẾT CỦA MỘT HÓA ĐƠN
    public boolean deleteByMaHoaDon(String maHoaDon) {

        String sql =
                "DELETE FROM ChiTietHoaDon WHERE MaHoaDon = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, maHoaDon);

            // Trả về true nếu câu lệnh thực thi thành công.
            // Có thể không có dòng nào bị xóa.
            ps.executeUpdate();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

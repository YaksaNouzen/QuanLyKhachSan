
package Module_DichVu.dao;

import Module_DichVu.DBConnection;
import Module_DichVu.model.LoaiDichVu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoaiDichVuDAO {

    public List<LoaiDichVu> getAll() throws SQLException {

        List<LoaiDichVu> danhSach = new ArrayList<>();

        String sql = "SELECT MaLoaiDV, TenLoaiDV "
                   + "FROM LoaiDichVu "
                   + "ORDER BY TenLoaiDV";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LoaiDichVu loai = new LoaiDichVu();

                loai.setMaLoaiDV(rs.getInt("MaLoaiDV"));
                loai.setTenLoaiDV(rs.getString("TenLoaiDV"));

                danhSach.add(loai);
            }
        }

        return danhSach;
    }
}

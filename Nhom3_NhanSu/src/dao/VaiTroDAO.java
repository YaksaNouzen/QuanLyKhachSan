package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.VaiTro;
import util.DBConnection;

public class VaiTroDAO {

    public List<VaiTro> getAll() {
        List<VaiTro> list = new ArrayList<>();
        String sql = "SELECT * FROM VaiTro";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new VaiTro(rs.getString("MaVaiTro"), rs.getString("TenVaiTro")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}

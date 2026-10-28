package nhom3.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nhom3.model.VaiTro;
import common.database.DatabaseConnection;

public class VaiTroDAO {

    public List<VaiTro> getAll() {
        List<VaiTro> list = new ArrayList<>();
        String sql = "SELECT * FROM VaiTro";
        try (Connection conn = DatabaseConnection.getConnection();
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

package Module_Phong.dao;

import common.database.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import Module_Phong.model.LoaiPhong;
import Module_Phong.model.Phong;
import Module_Phong.model.Tang;
import Module_Phong.model.TrangThaiPhong;

public class SqlServerPhongDAO implements PhongDAO {
    private final ConnectionFactory connections;

    public SqlServerPhongDAO(ConnectionFactory connections) {
        this.connections = connections;
    }

    public <T> T giaoDich(Function<Session, T> work) {
        try (Connection connection = connections.open()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            try {
                T value = work.apply(session(connection));
                connection.commit();
                return value;
            } catch (RuntimeException | SQLException ex) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    ex.addSuppressed(rollbackError);
                }
                throw ex;
            }
        } catch (SQLException ex) {
            throw loi(ex);
        }
    }

    public Session session(Connection connection) {
        return new JdbcSession(connection);
    }

    private static RuntimeException loi(SQLException ex) {
        if (ex.getErrorCode() == 2601 || ex.getErrorCode() == 2627)
            return new IllegalArgumentException("Ma hoac so phong da ton tai.", ex);
        if (ex.getErrorCode() == 547)
            return new IllegalArgumentException("Du lieu dang duoc tham chieu.", ex);
        return new IllegalStateException(
                "Khong the truy cap SQL Server. SQLState=" + ex.getSQLState(), ex);
    }

    private static class JdbcSession implements Session {
        private final Connection connection;

        JdbcSession(Connection connection) {
            this.connection = connection;
        }

        private <T> List<T> query(String sql, Mapper<T> mapper, String... values) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (int i = 0; i < values.length; i++)
                    statement.setString(i + 1, values[i]);
                try (ResultSet rows = statement.executeQuery()) {
                    List<T> result = new ArrayList<>();
                    while (rows.next()) result.add(mapper.map(rows));
                    return result;
                }
            } catch (SQLException ex) {
                throw loi(ex);
            }
        }

        private void update(String sql, Object... values) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (int i = 0; i < values.length; i++)
                    statement.setObject(i + 1, values[i]);
                if (statement.executeUpdate() != 1)
                    throw new IllegalArgumentException("Ban ghi khong ton tai hoac da thay doi.");
            } catch (SQLException ex) {
                throw loi(ex);
            }
        }

        private boolean exists(String sql, String maPhong) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, maPhong);
                try (ResultSet rows = statement.executeQuery()) {
                    return rows.next();
                }
            } catch (SQLException ex) {
                throw loi(ex);
            }
        }

        public List<Phong> phongs() {
            return query(
                    "SELECT MaPhong,SoPhong,MaLoaiPhong,MaTang,GiaPhong,TrangThai,NgungSuDung "
                            + "FROM dbo.Phong ORDER BY MaPhong",
                    this::mapPhong);
        }

        public Phong phong(String maPhong) {
            List<Phong> result = query(
                    "SELECT MaPhong,SoPhong,MaLoaiPhong,MaTang,GiaPhong,TrangThai,NgungSuDung "
                            + "FROM dbo.Phong WITH (UPDLOCK,HOLDLOCK) WHERE MaPhong=?",
                    this::mapPhong, maPhong);
            if (result.isEmpty()) throw new IllegalArgumentException("Khong tim thay phong.");
            return result.get(0);
        }

        private Phong mapPhong(ResultSet row) throws SQLException {
            return new Phong(
                    row.getString("MaPhong"), row.getString("SoPhong"),
                    row.getString("MaLoaiPhong"), row.getString("MaTang"),
                    row.getBigDecimal("GiaPhong"),
                    TrangThaiPhong.valueOf(row.getString("TrangThai")),
                    row.getBoolean("NgungSuDung"));
        }

        public List<LoaiPhong> loais() {
            return query(
                    "SELECT MaLoaiPhong,TenLoai,SoNguoiToiDa,GiaTheoNgay "
                            + "FROM dbo.LoaiPhong ORDER BY MaLoaiPhong",
                    row -> new LoaiPhong(
                            row.getString("MaLoaiPhong"), row.getString("TenLoai"),
                            row.getInt("SoNguoiToiDa"), row.getBigDecimal("GiaTheoNgay")));
        }

        public List<Tang> tangs() {
            return query(
                    "SELECT MaTang,TenTang FROM dbo.Tang ORDER BY MaTang",
                    row -> new Tang(row.getString("MaTang"), row.getString("TenTang")));
        }

        public void luuPhong(Phong phong, boolean sua) {
            if (sua) {
                update("UPDATE dbo.Phong SET SoPhong=?,MaLoaiPhong=?,MaTang=?,GiaPhong=? "
                                + "WHERE MaPhong=?",
                        phong.soPhong(), phong.maLoaiPhong(), phong.maTang(),
                        phong.giaPhong(), phong.maPhong());
            } else {
                update("INSERT dbo.Phong(MaPhong,SoPhong,MaLoaiPhong,MaTang,GiaPhong,TrangThai,NgungSuDung) "
                                + "VALUES(?,?,?,?,?,?,?)",
                        phong.maPhong(), phong.soPhong(), phong.maLoaiPhong(), phong.maTang(),
                        phong.giaPhong(), phong.trangThai().name(), phong.ngungSuDung());
            }
        }

        public void capNhatTrangThai(String maPhong, TrangThaiPhong tu, TrangThaiPhong den) {
            update("UPDATE dbo.Phong SET TrangThai=? WHERE MaPhong=? AND TrangThai=?",
                    den.name(), maPhong, tu.name());
        }

        public void datNgungSuDung(String maPhong, boolean ngungSuDung) {
            update("UPDATE dbo.Phong SET NgungSuDung=? WHERE MaPhong=?", ngungSuDung, maPhong);
        }

        public void xoaPhong(String maPhong) {
            try {
                update("DELETE dbo.Phong WHERE MaPhong=?", maPhong);
            } catch (IllegalArgumentException ex) {
                if (ex.getCause() instanceof SQLException sql && sql.getErrorCode() == 547)
                    throw new IllegalArgumentException(
                            "Phòng đã có dữ liệu liên quan nên không thể xóa. Hãy ngừng sử dụng phòng.", ex);
                throw ex;
            }
        }

        public void luuLoai(LoaiPhong loai, boolean sua) {
            update(sua
                            ? "UPDATE dbo.LoaiPhong SET TenLoai=?,SoNguoiToiDa=?,GiaTheoNgay=? WHERE MaLoaiPhong=?"
                            : "INSERT dbo.LoaiPhong(TenLoai,SoNguoiToiDa,GiaTheoNgay,MaLoaiPhong) VALUES(?,?,?,?)",
                    loai.tenLoai(), loai.soNguoiToiDa(), loai.giaTheoNgay(), loai.maLoaiPhong());
        }

        public void luuTang(Tang tang, boolean sua) {
            update(sua
                            ? "UPDATE dbo.Tang SET TenTang=? WHERE MaTang=?"
                            : "INSERT dbo.Tang(TenTang,MaTang) VALUES(?,?)",
                    tang.tenTang(), tang.maTang());
        }

        public void xoaLoai(String maLoaiPhong) {
            update("DELETE dbo.LoaiPhong WHERE MaLoaiPhong=?", maLoaiPhong);
        }

        public void xoaTang(String maTang) {
            update("DELETE dbo.Tang WHERE MaTang=?", maTang);
        }

        public boolean phongDangCoKhach(String maPhong) {
            return exists("SELECT TOP 1 1 FROM dbo.DatPhong "
                    + "WHERE MaPhong=? AND TrangThai='DANG_O'", maPhong);
        }

        public boolean phongConDonGiu(String maPhong) {
            return exists("SELECT TOP 1 1 FROM dbo.DatPhong "
                    + "WHERE MaPhong=? AND TrangThai='GIU_PHONG' "
                    + "AND NgayTraDuKien>SYSDATETIME()", maPhong);
        }

        private interface Mapper<T> {
            T map(ResultSet row) throws SQLException;
        }
    }
}

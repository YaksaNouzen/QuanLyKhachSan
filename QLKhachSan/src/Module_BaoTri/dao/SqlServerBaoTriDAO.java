package Module_BaoTri.dao;

import common.database.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import Module_BaoTri.model.MucDoUuTien;
import Module_BaoTri.model.PhieuBaoTri;
import Module_BaoTri.model.TrangThaiBaoTri;
import Module_Phong.dao.PhongDAO;
import Module_Phong.dao.SqlServerPhongDAO;

public class SqlServerBaoTriDAO implements BaoTriDAO {
    private final ConnectionFactory connections;
    private final SqlServerPhongDAO phongDAO;

    public SqlServerBaoTriDAO(ConnectionFactory connections, SqlServerPhongDAO phongDAO) {
        this.connections = connections;
        this.phongDAO = phongDAO;
    }

    public <T> T giaoDich(Function<Session, T> work) {
        try (Connection connection = connections.open()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            try {
                Session session = new JdbcSession(connection, phongDAO.session(connection));
                T result = work.apply(session);
                connection.commit();
                return result;
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

    private static RuntimeException loi(SQLException ex) {
        if (ex.getErrorCode() == 547)
            return new IllegalArgumentException("Phong khong ton tai hoac dang duoc su dung.", ex);
        return new IllegalStateException(
                "Khong the truy cap bang BaoTri. SQLState=" + ex.getSQLState(), ex);
    }

    private static Timestamp timestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    private static LocalDateTime date(ResultSet rows, String column) throws SQLException {
        Timestamp value = rows.getTimestamp(column);
        return value == null ? null : value.toLocalDateTime();
    }

    private class JdbcSession implements Session {
        private final Connection connection;
        private final PhongDAO.Session phongSession;

        JdbcSession(Connection connection, PhongDAO.Session phongSession) {
            this.connection = connection;
            this.phongSession = phongSession;
        }

        public List<PhieuBaoTri> phieuBaoTris() {
            return query("SELECT MaBaoTri,MaPhong,NoiDung,MucDo,NguoiBao,NgayBao,"
                    + "NguoiPhuTrach,TrangThai,BatDau,KetThuc,ChiPhi,KetQua "
                    + "FROM dbo.BaoTri ORDER BY NgayBao DESC", null);
        }

        public PhieuBaoTri phieu(String maBaoTri) {
            List<PhieuBaoTri> result = query("SELECT MaBaoTri,MaPhong,NoiDung,MucDo,NguoiBao,"
                    + "NgayBao,NguoiPhuTrach,TrangThai,BatDau,KetThuc,ChiPhi,KetQua "
                    + "FROM dbo.BaoTri WHERE MaBaoTri=?", maBaoTri);
            if (result.isEmpty()) throw new IllegalArgumentException("Khong tim thay phieu bao tri.");
            return result.get(0);
        }

        private List<PhieuBaoTri> query(String sql, String maBaoTri) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                if (maBaoTri != null) statement.setString(1, maBaoTri);
                statement.setQueryTimeout(15);
                try (ResultSet rows = statement.executeQuery()) {
                    List<PhieuBaoTri> result = new ArrayList<>();
                    while (rows.next()) {
                        result.add(new PhieuBaoTri(
                                rows.getString("MaBaoTri"), rows.getString("MaPhong"),
                                rows.getString("NoiDung"),
                                MucDoUuTien.valueOf(rows.getString("MucDo")),
                                rows.getString("NguoiBao"), date(rows, "NgayBao"),
                                rows.getString("NguoiPhuTrach"),
                                TrangThaiBaoTri.valueOf(rows.getString("TrangThai")),
                                date(rows, "BatDau"), date(rows, "KetThuc"),
                                rows.getBigDecimal("ChiPhi"), rows.getString("KetQua")));
                    }
                    return result;
                }
            } catch (SQLException ex) {
                throw loi(ex);
            }
        }

        public void luu(PhieuBaoTri phieu, boolean sua) {
            String sql = sua
                    ? "UPDATE dbo.BaoTri SET MaPhong=?,NoiDung=?,MucDo=?,NguoiBao=?,NgayBao=?,"
                            + "NguoiPhuTrach=?,TrangThai=?,BatDau=?,KetThuc=?,ChiPhi=?,KetQua=? "
                            + "WHERE MaBaoTri=?"
                    : "INSERT dbo.BaoTri(MaPhong,NoiDung,MucDo,NguoiBao,NgayBao,NguoiPhuTrach,"
                            + "TrangThai,BatDau,KetThuc,ChiPhi,KetQua,MaBaoTri) "
                            + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, phieu.maPhong());
                statement.setString(2, phieu.noiDung());
                statement.setString(3, phieu.mucDo().name());
                statement.setString(4, phieu.nguoiBao());
                statement.setTimestamp(5, timestamp(phieu.ngayBao()));
                statement.setString(6, phieu.nguoiPhuTrach());
                statement.setString(7, phieu.trangThai().name());
                statement.setTimestamp(8, timestamp(phieu.batDau()));
                statement.setTimestamp(9, timestamp(phieu.ketThuc()));
                statement.setBigDecimal(10, phieu.chiPhi());
                statement.setString(11, phieu.ketQua());
                statement.setString(12, phieu.maBaoTri());
                if (statement.executeUpdate() != 1)
                    throw new IllegalArgumentException("Phieu khong ton tai hoac da thay doi.");
            } catch (SQLException ex) {
                throw loi(ex);
            }
        }

        public PhongDAO.Session phong() {
            return phongSession;
        }
    }
}

package module.buongphong.dao;

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
import module.buongphong.model.CongViecVeSinh;
import module.buongphong.model.TrangThaiVeSinh;
import module.phong.dao.PhongDAO;
import module.phong.dao.SqlServerPhongDAO;

public class SqlServerVeSinhDAO implements VeSinhDAO {
    private final ConnectionFactory connections;
    private final SqlServerPhongDAO phongDAO;

    public SqlServerVeSinhDAO(ConnectionFactory connections, SqlServerPhongDAO phongDAO) {
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
                "Khong the truy cap bang VeSinh. SQLState=" + ex.getSQLState(), ex);
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

        public List<CongViecVeSinh> congViecs() {
            String sql = "SELECT MaVeSinh,MaPhong,NoiDung,NguoiPhuTrach,TrangThai,"
                    + "NgayPhanCong,BatDau,KetThuc,NguoiXacNhan,XacNhanLuc "
                    + "FROM dbo.VeSinh ORDER BY NgayPhanCong DESC";
            return query(sql, null);
        }

        public CongViecVeSinh congViec(String maVeSinh) {
            List<CongViecVeSinh> result = query(
                    "SELECT MaVeSinh,MaPhong,NoiDung,NguoiPhuTrach,TrangThai,NgayPhanCong,"
                            + "BatDau,KetThuc,NguoiXacNhan,XacNhanLuc "
                            + "FROM dbo.VeSinh WHERE MaVeSinh=?", maVeSinh);
            if (result.isEmpty()) throw new IllegalArgumentException("Khong tim thay cong viec.");
            return result.get(0);
        }

        private List<CongViecVeSinh> query(String sql, String maVeSinh) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                if (maVeSinh != null) statement.setString(1, maVeSinh);
                statement.setQueryTimeout(15);
                try (ResultSet rows = statement.executeQuery()) {
                    List<CongViecVeSinh> result = new ArrayList<>();
                    while (rows.next()) {
                        result.add(new CongViecVeSinh(
                                rows.getString("MaVeSinh"), rows.getString("MaPhong"),
                                rows.getString("NoiDung"), rows.getString("NguoiPhuTrach"),
                                TrangThaiVeSinh.valueOf(rows.getString("TrangThai")),
                                date(rows, "NgayPhanCong"), date(rows, "BatDau"),
                                date(rows, "KetThuc"), rows.getString("NguoiXacNhan"),
                                date(rows, "XacNhanLuc")));
                    }
                    return result;
                }
            } catch (SQLException ex) {
                throw loi(ex);
            }
        }

        public void luu(CongViecVeSinh job, boolean sua) {
            String sql = sua
                    ? "UPDATE dbo.VeSinh SET MaPhong=?,NoiDung=?,NguoiPhuTrach=?,TrangThai=?,"
                            + "NgayPhanCong=?,BatDau=?,KetThuc=?,NguoiXacNhan=?,XacNhanLuc=? "
                            + "WHERE MaVeSinh=?"
                    : "INSERT dbo.VeSinh(MaPhong,NoiDung,NguoiPhuTrach,TrangThai,NgayPhanCong,"
                            + "BatDau,KetThuc,NguoiXacNhan,XacNhanLuc,MaVeSinh) "
                            + "VALUES(?,?,?,?,?,?,?,?,?,?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, job.maPhong());
                statement.setString(2, job.noiDung());
                statement.setString(3, job.nguoiPhuTrach());
                statement.setString(4, job.trangThai().name());
                statement.setTimestamp(5, timestamp(job.ngayPhanCong()));
                statement.setTimestamp(6, timestamp(job.batDau()));
                statement.setTimestamp(7, timestamp(job.ketThuc()));
                statement.setString(8, job.nguoiXacNhan());
                statement.setTimestamp(9, timestamp(job.xacNhanLuc()));
                statement.setString(10, job.maVeSinh());
                if (statement.executeUpdate() != 1)
                    throw new IllegalArgumentException("Cong viec khong ton tai hoac da thay doi.");
            } catch (SQLException ex) {
                throw loi(ex);
            }
        }

        public PhongDAO.Session phong() {
            return phongSession;
        }
    }
}

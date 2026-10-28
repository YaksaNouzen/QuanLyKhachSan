package Module_PhanHoiKhachHang.service;

import Module_PhanHoiKhachHang.dao.*;
import Module_PhanHoiKhachHang.model.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class PhanHoiService {
    private final ConnectionProvider connections;
    private final PhanHoiDAO phanHoiDAO;
    private final LichSuXuLyPhanHoiDAO lichSuDAO;

    public PhanHoiService() { this(DBConnection::getConnection, new PhanHoiDAO(), new LichSuXuLyPhanHoiDAO()); }
    public PhanHoiService(ConnectionProvider connections, PhanHoiDAO dao, LichSuXuLyPhanHoiDAO history) {
        this.connections = connections; this.phanHoiDAO = dao; this.lichSuDAO = history;
    }

    public List<PhanHoi> search(String keyword, LoaiPhanHoi type, TrangThaiPhanHoi state) {
        String text = keyword == null ? "" : keyword.trim();
        if (text.length() > 2000) throw new IllegalArgumentException("Từ khóa tối đa 2000 ký tự.");
        try (Connection con = connections.getConnection()) { return phanHoiDAO.search(con, text, type, state); }
        catch (SQLException ex) { throw databaseError(); }
    }

    public PhanHoi findById(long id) {
        requireId(id);
        try (Connection con = connections.getConnection()) {
            PhanHoi result = phanHoiDAO.findById(con, id);
            if (result == null) throw new IllegalArgumentException("Không tìm thấy phản hồi.");
            return result;
        } catch (SQLException ex) { throw databaseError(); }
    }

    public List<LichSuXuLyPhanHoi> history(long id) {
        requireId(id);
        try (Connection con = connections.getConnection()) { return lichSuDAO.getByMaPhanHoi(con, id); }
        catch (SQLException ex) { throw databaseError(); }
    }

    public long tiepNhan(String customer, LoaiPhanHoi type, String content,
            LocalDateTime received, String actor) {
        String ma = required(customer, "Mã khách hàng", 20);
        // VARCHAR reference only; intentionally no customer lookup until shared DB integration.
        String note = required(content, "Nội dung phản hồi", 2000);
        String user = optionalActor(actor);
        if (type == null) throw new IllegalArgumentException("Hãy chọn loại phản hồi.");
        if (received == null || received.getYear() < 1 || received.getYear() > 9999
                || received.isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException("Thời gian tiếp nhận không hợp lệ hoặc ở tương lai.");
        PhanHoi p = new PhanHoi();
        p.setMaKhachHang(ma); p.setLoaiPhanHoi(type); p.setNoiDung(note);
        p.setThoiGianTiepNhan(received.withNano(0));
        return transaction(con -> {
            long id = phanHoiDAO.insert(con, p);
            lichSuDAO.insert(con, id, null, TrangThaiPhanHoi.MOI_TIEP_NHAN, "Tiếp nhận phản hồi.", user);
            return id;
        });
    }

    public void capNhatXuLy(long id, byte[] version, TrangThaiPhanHoi target, String content, String actor) {
        requireId(id);
        String note = required(content, "Nội dung xử lý", 2000);
        String user = optionalActor(actor);
        if (version == null || version.length != 8) throw new IllegalArgumentException("Hãy tải lại phản hồi trước khi xử lý.");
        byte[] expected = version.clone();
        if (target == null || target == TrangThaiPhanHoi.MOI_TIEP_NHAN)
            throw new IllegalArgumentException("Chọn Đang xử lý hoặc Đã xử lý.");
        transaction(con -> {
            PhanHoi current = phanHoiDAO.findById(con, id);
            if (current == null) throw new IllegalArgumentException("Không tìm thấy phản hồi.");
            if (!Arrays.equals(current.getPhienBan(), expected)) throw conflict();
            if (current.getTrangThai() == TrangThaiPhanHoi.DA_XU_LY)
                throw new IllegalArgumentException("Phản hồi đã xử lý chỉ được tra cứu.");
            if (!phanHoiDAO.updateStatus(con, id, expected, target)) throw conflict();
            lichSuDAO.insert(con, id, current.getTrangThai(), target, note, user);
            return null;
        });
    }

    private <T> T transaction(SqlWork<T> work) {
        try (Connection con = connections.getConnection()) {
            con.setAutoCommit(false);
            try {
                T result = work.run(con);
                con.commit();
                return result;
            } catch (SQLException | RuntimeException ex) {
                try { con.rollback(); } catch (SQLException rollback) { ex.addSuppressed(rollback); }
                throw ex;
            }
        } catch (SQLException ex) { throw databaseError(); }
    }

    @FunctionalInterface private interface SqlWork<T> { T run(Connection con) throws SQLException; }
    private static void requireId(long id) {
        if (id <= 0) throw new IllegalArgumentException("Hãy chọn một phản hồi.");
    }
    private static String required(String value, String label, int max) {
        String s = value == null ? "" : value.trim();
        if (s.isBlank() || s.length() > max)
            throw new IllegalArgumentException(label + " phải có từ 1 đến " + max + " ký tự.");
        return s;
    }
    private static String optionalActor(String actor) {
        return actor == null || actor.isBlank() ? null : required(actor, "Người thực hiện", 100);
    }
    private static IllegalStateException conflict() {
        return new IllegalStateException("Phản hồi đã được người khác cập nhật. Hãy đóng hộp thoại và tải lại.");
    }
    private static IllegalStateException databaseError() {
        // Do not expose JDBC messages containing server/account details to the UI.
        return new IllegalStateException("Không thể hoàn tất thao tác với QLPhanHoi. Kiểm tra kết nối và script trong README-PHANHOI.md. Nếu mất kết nối lúc lưu, hãy tải lại để kiểm tra trước khi thử lại.");
    }
}

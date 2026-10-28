package module.phong.service;

import common.database.ConnectionFactory;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;
import module.phong.dao.MemoryPhongDAO;
import module.phong.dao.PhongDAO;
import module.phong.dao.SqlServerPhongDAO;
import module.phong.model.DuLieuPhong;
import module.phong.model.LoaiPhong;
import module.phong.model.Phong;
import module.phong.model.Tang;
import module.phong.model.TrangThaiPhong;
import static module.phong.model.TrangThaiPhong.TRONG;

public class PhongService {
    private final PhongDAO dao;

    public PhongService(PhongDAO dao) {
        this.dao = dao;
    }

    private <T> T tx(Function<PhongDAO.Session, T> work) {
        return dao.giaoDich(work);
    }

    public DuLieuPhong taiDuLieu() {
        return tx(s -> new DuLieuPhong(s.phongs(), s.loais(), s.tangs()));
    }

    public List<Phong> getPhongTrong() {
        return tx(s -> s.phongs().stream()
                .filter(p -> !p.ngungSuDung() && p.trangThai() == TRONG)
                .toList());
    }

    public Phong getPhong(String maPhong) {
        return tx(s -> s.phong(maPhong));
    }

    public BigDecimal getGiaHienHanh(String maPhong) {
        return tx(s -> gia(s, maPhong));
    }

    public Phong getPhong(Connection connection, String maPhong) throws SQLException {
        return session(connection).phong(maPhong);
    }

    public BigDecimal getGiaHienHanh(Connection connection, String maPhong)
            throws SQLException {
        return gia(session(connection), maPhong);
    }

    private BigDecimal gia(PhongDAO.Session session, String maPhong) {
        Phong phong = session.phong(maPhong);
        if (phong.giaPhong() != null) return phong.giaPhong();
        return session.loais().stream()
                .filter(loai -> loai.maLoaiPhong().equals(phong.maLoaiPhong()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay loai phong."))
                .giaTheoNgay();
    }

    private PhongDAO.Session session(Connection connection) throws SQLException {
        require(dao instanceof SqlServerPhongDAO,
                "API Connection chi dung cho SQL Server.");
        require(!connection.getAutoCommit(),
                "Ben goi phai tu quan ly commit va rollback.");
        return ((SqlServerPhongDAO) dao).session(connection);
    }

    private static void require(boolean ok, String message) {
        if (!ok) throw new IllegalArgumentException(message);
    }

    private static void code(String value) {
        require(value != null && value.matches("[A-Z0-9_-]{1,20}"),
                "Ma gom 1-20 ky tu A-Z, 0-9, _ hoac -.");
    }

    private static void text(String value, int max, String field) {
        require(value != null && !value.isBlank() && value.length() <= max,
                field + " bat buoc, toi da " + max + " ky tu.");
    }

    private static void price(BigDecimal value) {
        require(value != null && value.signum() > 0
                        && value.stripTrailingZeros().scale() <= 2,
                "Gia phai lon hon 0 va co toi da 2 so thap phan.");
    }

    public void luuPhong(Phong phong, boolean sua) {
        tx(s -> {
            code(phong.maPhong());
            text(phong.soPhong(), 20, "So phong");
            require(phong.trangThai() != null, "Thieu trang thai.");
            require(s.loais().stream().anyMatch(l -> l.maLoaiPhong().equals(phong.maLoaiPhong())),
                    "Loai phong khong hop le.");
            require(s.tangs().stream().anyMatch(t -> t.maTang().equals(phong.maTang())),
                    "Tang khong hop le.");
            if (phong.giaPhong() != null) price(phong.giaPhong());
            require(s.phongs().stream().noneMatch(p ->
                            !p.maPhong().equals(phong.maPhong())
                                    && p.maTang().equals(phong.maTang())
                                    && p.soPhong().equalsIgnoreCase(phong.soPhong())),
                    "So phong da duoc su dung tren tang nay.");
            if (sua) {
                Phong cu = s.phong(phong.maPhong());
                require(!cu.ngungSuDung() && cu.trangThai() == TRONG,
                        "Chi sua thong tin khi phong dang trong va dang su dung.");
            } else {
                require(phong.trangThai() == TRONG && !phong.ngungSuDung(),
                        "Phong moi phai co trang thai TRONG.");
            }
            s.luuPhong(phong, sua);
            return null;
        });
    }

    public void luuLoai(LoaiPhong loai, boolean sua) {
        tx(s -> {
            code(loai.maLoaiPhong());
            text(loai.tenLoai(), 100, "Ten loai");
            price(loai.giaTheoNgay());
            require(loai.soNguoiToiDa() > 0 && loai.soNguoiToiDa() <= 20,
                    "Suc chua tu 1 den 20 nguoi.");
            s.luuLoai(loai, sua);
            return null;
        });
    }

    public void luuTang(Tang tang, boolean sua) {
        tx(s -> {
            code(tang.maTang());
            text(tang.tenTang(), 100, "Ten tang");
            s.luuTang(tang, sua);
            return null;
        });
    }

    public void xoaLoai(String maLoai) {
        tx(s -> {
            require(s.loais().stream().anyMatch(l -> l.maLoaiPhong().equals(maLoai)),
                    "Khong tim thay loai phong.");
            require(s.phongs().stream().noneMatch(p -> p.maLoaiPhong().equals(maLoai)),
                    "Loai phong van duoc phong su dung.");
            s.xoaLoai(maLoai);
            return null;
        });
    }

    public void xoaTang(String maTang) {
        tx(s -> {
            require(s.tangs().stream().anyMatch(t -> t.maTang().equals(maTang)),
                    "Khong tim thay tang.");
            require(s.phongs().stream().noneMatch(p -> p.maTang().equals(maTang)),
                    "Tang van con phong tham chieu.");
            s.xoaTang(maTang);
            return null;
        });
    }

    public void ngungSuDung(String maPhong) {
        tx(s -> {
            Phong phong = s.phong(maPhong);
            require(!phong.ngungSuDung() && phong.trangThai() == TRONG,
                    "Chi ngung su dung phong dang trong.");
            require(!s.phongDangCoKhach(maPhong) && !s.phongConDonGiu(maPhong),
                    "Phong co khach hoac booking con hieu luc.");
            s.datNgungSuDung(maPhong, true);
            return null;
        });
    }

    public static PhongService demo() {
        return demo(new MemoryPhongDAO());
    }

    public static PhongService demo(MemoryPhongDAO dao) {
        PhongService service = new PhongService(dao);
        service.luuLoai(new LoaiPhong("STD", "Standard", 2,
                new BigDecimal("650000")), false);
        service.luuLoai(new LoaiPhong("DLX", "Deluxe", 3,
                new BigDecimal("950000")), false);
        for (int tang = 1; tang <= 3; tang++) {
            service.luuTang(new Tang("T" + tang, "Tang " + tang), false);
            for (int so = 1; so <= 3; so++) {
                String maPhong = "P" + tang + "0" + so;
                String maLoai = so == 3 ? "DLX" : "STD";
                service.luuPhong(new Phong(
                        maPhong, tang + "0" + so, maLoai, "T" + tang,
                        null, TRONG, false), false);
            }
        }
        return service;
    }
}

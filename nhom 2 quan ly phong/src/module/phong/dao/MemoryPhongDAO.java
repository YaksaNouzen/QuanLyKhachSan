package module.phong.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import module.phong.model.LoaiPhong;
import module.phong.model.Phong;
import module.phong.model.Tang;
import module.phong.model.TrangThaiPhong;

public class MemoryPhongDAO implements PhongDAO {
    private final Map<String, Phong> phongs = new LinkedHashMap<>();
    private final Map<String, LoaiPhong> loais = new LinkedHashMap<>();
    private final Map<String, Tang> tangs = new LinkedHashMap<>();
    private final Set<String> phongDangCoKhach = new LinkedHashSet<>();
    private final Map<String, LocalDateTime> phongConGiuDen = new LinkedHashMap<>();

    public <T> T giaoDich(java.util.function.Function<Session, T> work) {
        synchronized (this) {
            Map<String, Phong> phongCu = new LinkedHashMap<>(phongs);
            Map<String, LoaiPhong> loaiCu = new LinkedHashMap<>(loais);
            Map<String, Tang> tangCu = new LinkedHashMap<>(tangs);
            try {
                return work.apply(new MemorySession());
            } catch (RuntimeException ex) {
                phongs.clear();
                phongs.putAll(phongCu);
                loais.clear();
                loais.putAll(loaiCu);
                tangs.clear();
                tangs.putAll(tangCu);
                throw ex;
            }
        }
    }

    public void setPhongDangCoKhach(String maPhong, boolean dangO) {
        if (dangO) phongDangCoKhach.add(maPhong);
        else phongDangCoKhach.remove(maPhong);
    }

    public void setPhongConGiuDen(String maPhong, LocalDateTime den) {
        if (den == null) phongConGiuDen.remove(maPhong);
        else phongConGiuDen.put(maPhong, den);
    }

    private class MemorySession implements Session {
        public List<Phong> phongs() {
            return List.copyOf(phongs.values());
        }

        public Phong phong(String maPhong) {
            Phong phong = phongs.get(maPhong);
            if (phong == null) throw new IllegalArgumentException("Khong tim thay phong.");
            return phong;
        }

        public List<LoaiPhong> loais() {
            return List.copyOf(loais.values());
        }

        public List<Tang> tangs() {
            return List.copyOf(tangs.values());
        }

        public void luuPhong(Phong phong, boolean sua) {
            if (sua) {
                Phong cu = phong(phong.maPhong());
                phongs.put(
                        phong.maPhong(),
                        new Phong(
                                phong.maPhong(), phong.soPhong(), phong.maLoaiPhong(),
                                phong.maTang(), phong.giaPhong(), cu.trangThai(), cu.ngungSuDung()));
            } else {
                if (phongs.containsKey(phong.maPhong()))
                    throw new IllegalArgumentException("Ma phong da ton tai.");
                phongs.put(phong.maPhong(), phong);
            }
        }

        public void capNhatTrangThai(String maPhong, TrangThaiPhong tu, TrangThaiPhong den) {
            Phong cu = phong(maPhong);
            if (cu.trangThai() != tu)
                throw new IllegalArgumentException("Trang thai phong da thay doi.");
            phongs.put(maPhong, new Phong(
                    cu.maPhong(), cu.soPhong(), cu.maLoaiPhong(), cu.maTang(),
                    cu.giaPhong(), den, cu.ngungSuDung()));
        }

        public void datNgungSuDung(String maPhong, boolean ngungSuDung) {
            Phong cu = phong(maPhong);
            phongs.put(maPhong, new Phong(
                    cu.maPhong(), cu.soPhong(), cu.maLoaiPhong(), cu.maTang(),
                    cu.giaPhong(), cu.trangThai(), ngungSuDung));
        }

        public void luuLoai(LoaiPhong loai, boolean sua) {
            if (!sua && loais.containsKey(loai.maLoaiPhong()))
                throw new IllegalArgumentException("Ma loai phong da ton tai.");
            loais.put(loai.maLoaiPhong(), loai);
        }

        public void luuTang(Tang tang, boolean sua) {
            if (!sua && tangs.containsKey(tang.maTang()))
                throw new IllegalArgumentException("Ma tang da ton tai.");
            tangs.put(tang.maTang(), tang);
        }

        public void xoaLoai(String maLoaiPhong) {
            if (phongs.values().stream().anyMatch(p -> p.maLoaiPhong().equals(maLoaiPhong)))
                throw new IllegalArgumentException("Loai phong dang duoc su dung.");
            loais.remove(maLoaiPhong);
        }

        public void xoaTang(String maTang) {
            if (phongs.values().stream().anyMatch(p -> p.maTang().equals(maTang)))
                throw new IllegalArgumentException("Tang dang co phong.");
            tangs.remove(maTang);
        }

        public boolean phongDangCoKhach(String maPhong) {
            return phongDangCoKhach.contains(maPhong);
        }

        public boolean phongConDonGiu(String maPhong) {
            LocalDateTime den = phongConGiuDen.get(maPhong);
            return den != null && den.isAfter(LocalDateTime.now());
        }
    }
}

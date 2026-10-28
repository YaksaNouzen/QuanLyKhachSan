package Module_Phong.model;

import java.util.List;

public record DuLieuPhong(List<Phong> phongs, List<LoaiPhong> loais, List<Tang> tangs) {
    public DuLieuPhong {
        phongs = List.copyOf(phongs);
        loais = List.copyOf(loais);
        tangs = List.copyOf(tangs);
    }

    public static DuLieuPhong rong() {
        return new DuLieuPhong(List.of(), List.of(), List.of());
    }
}

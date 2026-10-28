package module.phong.dao;

import java.util.List;
import java.util.function.Function;
import module.phong.model.LoaiPhong;
import module.phong.model.Phong;
import module.phong.model.Tang;
import module.phong.model.TrangThaiPhong;

public interface PhongDAO {
    <T> T giaoDich(Function<Session, T> work);

    interface Session {
        List<Phong> phongs();

        Phong phong(String maPhong);

        List<LoaiPhong> loais();

        List<Tang> tangs();

        void luuPhong(Phong phong, boolean sua);

        void capNhatTrangThai(String maPhong, TrangThaiPhong tu, TrangThaiPhong den);

        void datNgungSuDung(String maPhong, boolean ngungSuDung);

        void luuLoai(LoaiPhong loai, boolean sua);

        void luuTang(Tang tang, boolean sua);

        void xoaLoai(String maLoaiPhong);

        void xoaTang(String maTang);

        boolean phongDangCoKhach(String maPhong);

        boolean phongConDonGiu(String maPhong);
    }
}

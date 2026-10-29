package Module_Phong.dao;

import java.util.List;
import java.util.function.Function;
import Module_Phong.model.LoaiPhong;
import Module_Phong.model.Phong;
import Module_Phong.model.Tang;
import Module_Phong.model.TrangThaiPhong;

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
        void xoaPhong(String maPhong);

        void luuLoai(LoaiPhong loai, boolean sua);

        void luuTang(Tang tang, boolean sua);

        void xoaLoai(String maLoaiPhong);

        void xoaTang(String maTang);

        boolean phongDangCoKhach(String maPhong);

        boolean phongConDonGiu(String maPhong);
    }
}

package Module_BuongPhong.dao;

import java.util.List;
import java.util.function.Function;
import Module_BuongPhong.model.CongViecVeSinh;
import Module_Phong.dao.PhongDAO;

public interface VeSinhDAO {
    <T> T giaoDich(Function<Session, T> work);

    interface Session {
        List<CongViecVeSinh> congViecs();

        CongViecVeSinh congViec(String maVeSinh);

        void luu(CongViecVeSinh congViec, boolean sua);

        PhongDAO.Session phong();
    }
}

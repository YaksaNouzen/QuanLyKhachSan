package Module_BaoTri.dao;

import java.util.List;
import java.util.function.Function;
import Module_BaoTri.model.PhieuBaoTri;
import Module_Phong.dao.PhongDAO;

public interface BaoTriDAO {
    <T> T giaoDich(Function<Session, T> work);

    interface Session {
        List<PhieuBaoTri> phieuBaoTris();

        PhieuBaoTri phieu(String maBaoTri);

        void luu(PhieuBaoTri phieu, boolean sua);

        PhongDAO.Session phong();
    }
}

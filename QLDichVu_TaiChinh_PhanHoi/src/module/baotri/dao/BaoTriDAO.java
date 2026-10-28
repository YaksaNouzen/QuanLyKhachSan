package module.baotri.dao;

import java.util.List;
import java.util.function.Function;
import module.baotri.model.PhieuBaoTri;
import module.phong.dao.PhongDAO;

public interface BaoTriDAO {
    <T> T giaoDich(Function<Session, T> work);

    interface Session {
        List<PhieuBaoTri> phieuBaoTris();

        PhieuBaoTri phieu(String maBaoTri);

        void luu(PhieuBaoTri phieu, boolean sua);

        PhongDAO.Session phong();
    }
}

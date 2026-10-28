package Module_BaoTri.dao;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import Module_BaoTri.model.PhieuBaoTri;
import Module_Phong.dao.MemoryPhongDAO;
import Module_Phong.dao.PhongDAO;

public class MemoryBaoTriDAO implements BaoTriDAO {
    private final MemoryPhongDAO phongDAO;
    private final Map<String, PhieuBaoTri> phieus = new LinkedHashMap<>();

    public MemoryBaoTriDAO(MemoryPhongDAO phongDAO) {
        this.phongDAO = phongDAO;
    }

    public <T> T giaoDich(Function<Session, T> work) {
        synchronized (phieus) {
            Map<String, PhieuBaoTri> banSao = new LinkedHashMap<>(phieus);
            try {
                return phongDAO.giaoDich(phongSession ->
                        work.apply(new MemorySession(phongSession)));
            } catch (RuntimeException ex) {
                phieus.clear();
                phieus.putAll(banSao);
                throw ex;
            }
        }
    }

    private class MemorySession implements Session {
        private final PhongDAO.Session phongSession;

        MemorySession(PhongDAO.Session phongSession) {
            this.phongSession = phongSession;
        }

        public List<PhieuBaoTri> phieuBaoTris() {
            return List.copyOf(phieus.values());
        }

        public PhieuBaoTri phieu(String maBaoTri) {
            PhieuBaoTri phieu = phieus.get(maBaoTri);
            if (phieu == null) throw new IllegalArgumentException("Khong tim thay phieu bao tri.");
            return phieu;
        }

        public void luu(PhieuBaoTri phieu, boolean sua) {
            boolean tonTai = phieus.containsKey(phieu.maBaoTri());
            if (sua != tonTai)
                throw new IllegalArgumentException("Ma phieu bao tri khong hop le.");
            phieus.put(phieu.maBaoTri(), phieu);
        }

        public PhongDAO.Session phong() {
            return phongSession;
        }
    }
}

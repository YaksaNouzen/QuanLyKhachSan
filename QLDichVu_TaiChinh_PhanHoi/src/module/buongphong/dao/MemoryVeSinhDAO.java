package module.buongphong.dao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import module.buongphong.model.CongViecVeSinh;
import module.phong.dao.MemoryPhongDAO;
import module.phong.dao.PhongDAO;

public class MemoryVeSinhDAO implements VeSinhDAO {
    private final MemoryPhongDAO phongDAO;
    private final Map<String, CongViecVeSinh> congViecs = new LinkedHashMap<>();

    public MemoryVeSinhDAO(MemoryPhongDAO phongDAO) {
        this.phongDAO = phongDAO;
    }

    public <T> T giaoDich(Function<Session, T> work) {
        synchronized (congViecs) {
            Map<String, CongViecVeSinh> banSao = new LinkedHashMap<>(congViecs);
            try {
                return phongDAO.giaoDich(phongSession ->
                        work.apply(new MemorySession(phongSession)));
            } catch (RuntimeException ex) {
                congViecs.clear();
                congViecs.putAll(banSao);
                throw ex;
            }
        }
    }

    private class MemorySession implements Session {
        private final PhongDAO.Session phongSession;

        MemorySession(PhongDAO.Session phongSession) {
            this.phongSession = phongSession;
        }

        public List<CongViecVeSinh> congViecs() {
            return List.copyOf(new ArrayList<>(congViecs.values()));
        }

        public CongViecVeSinh congViec(String maVeSinh) {
            CongViecVeSinh congViec = congViecs.get(maVeSinh);
            if (congViec == null)
                throw new IllegalArgumentException("Khong tim thay cong viec ve sinh.");
            return congViec;
        }

        public void luu(CongViecVeSinh congViec, boolean sua) {
            boolean tonTai = congViecs.containsKey(congViec.maVeSinh());
            if (sua != tonTai)
                throw new IllegalArgumentException("Ma cong viec khong hop le.");
            congViecs.put(congViec.maVeSinh(), congViec);
        }

        public PhongDAO.Session phong() {
            return phongSession;
        }
    }
}
